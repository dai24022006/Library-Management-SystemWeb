package com.library.management.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.library.management.dto.borrow.BorrowRequest;
import com.library.management.dto.borrow.BookBorrowRequest;
import com.library.management.dto.borrow.BookReturnRequest;
import com.library.management.dto.borrow.BorrowResponse;
import com.library.management.dto.borrow.ReturnBorrowRequest;
import com.library.management.dto.borrow.BorrowDetailResponse;
import com.library.management.enums.BookStatus;
import com.library.management.enums.BorrowStatus;
import com.library.management.enums.PaymentStatus;
import com.library.management.exception.ConflictException;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.model.Book;
import com.library.management.model.Borrow;
import com.library.management.model.Reader;
import com.library.management.model.BorrowDetail;
import com.library.management.repository.BookRepository;
import com.library.management.repository.BorrowRepository;
import com.library.management.repository.ReaderRepository;

@Service
public class BorrowService {
    private final BorrowRepository borrowRepository;
    private final ReaderRepository readerRepository;
    private final BookRepository bookRepository;
    private final BigDecimal fine = BigDecimal.valueOf(50000);

    public BorrowService(
            BorrowRepository borrowRepository,
            ReaderRepository readerRepository,
            BookRepository bookRepository) {
        this.borrowRepository = borrowRepository;
        this.readerRepository = readerRepository;
        this.bookRepository = bookRepository;
    }

    private BorrowResponse toResponse(Borrow borrow) {

        BorrowResponse response = new BorrowResponse();

        response.setBorrowId(borrow.getBorrowId());

        response.setReaderId(borrow.getReader().getReaderId());
        response.setReaderName(borrow.getReader().getFullName());

        List<BorrowDetailResponse> details = borrow.getDetails()
                .stream()
                .map(detail -> {
                    BorrowDetailResponse detailResponse = new BorrowDetailResponse();

                    detailResponse.setBookId(detail.getBook().getBookId());
                    detailResponse.setBookTitle(detail.getBook().getTitle());
                    detailResponse.setQuantity(detail.getQuantity());
                    detailResponse.setReturnedQuantity(detail.getReturnedQuantity());
                    detailResponse.setDamagedQuantity(detail.getDamagedQuantity());
                    detailResponse.setLostQuantity(detail.getLostQuantity());

                    return detailResponse;
                })
                .toList();

        response.setBooks(details);

        response.setBorrowDate(borrow.getBorrowDate());
        response.setDueDate(borrow.getDueDate());
        response.setReturnDate(borrow.getReturnDate());
        response.setStatus(borrow.getStatus());
        response.setTotalFee(borrow.getTotalFee());
        response.setPayStatus(borrow.getPayStatus());

        return response;
    }

    private List<BorrowResponse> toResponse(List<Borrow> borrows) {
        return new ArrayList<>(
                borrows.stream()
                        .map(this::toResponse)
                        .toList());
    }

    private String generateBorrowId() {
        int number = 1;

        while (true) {
            String borrowId = String.format("BR%03d", number);

            if (!borrowRepository.existsByBorrowId(borrowId)) {
                return borrowId;
            }

            number++;
        }
    }

    @Transactional
    public BorrowResponse borrowBook(BorrowRequest borrowRequest) {
        Reader reader = readerRepository.findByReaderId(borrowRequest.getReaderId());
        if (reader == null) {
            throw new ResourceNotFoundException("Reader not found");
        }

        String borrowId = generateBorrowId();
        LocalDate borrowDate = LocalDate.now();
        Borrow borrow = new Borrow(borrowId,
                reader,
                borrowDate,
                borrowDate.plusMonths(4),
                BorrowStatus.BORROWING);

        for (BookBorrowRequest item : borrowRequest.getBooks()) {
            Book book = bookRepository.findByBookId(item.getBookId());
            if (book == null) {
                throw new ResourceNotFoundException("Book not found");
            }
            Integer quantity = item.getQuantity();
            if (quantity <= 0) {
                throw new ConflictException("Quantity must be greater than 0");
            }
            if (book.getQuantity() < quantity) {
                throw new ConflictException("Not enough books");
            }

            BorrowDetail detail = new BorrowDetail();

            detail.setBorrow(borrow);
            detail.setBook(book);
            detail.setQuantity(quantity);

            borrow.getDetails().add(detail);

            book.setQuantity(book.getQuantity() - quantity);
            if (book.getQuantity() == 0) {
                book.setStatus(BookStatus.OUT_OF_STOCK);
            }
            bookRepository.save(book);
        }
        return toResponse(borrowRepository.save(borrow));
    }

    @Transactional
    public BorrowResponse returnBookByBorrowId(String borrowId, ReturnBorrowRequest request) {
        if (borrowId == null) {
            throw new ConflictException("BorrowID not null");
        }
        Borrow borrow = borrowRepository.findByBorrowId(borrowId);
        if (borrow == null) {
            throw new ResourceNotFoundException("Borrow not found");
        }
        if (borrow.getStatus() == BorrowStatus.RETURNED) {
            throw new ConflictException("Borrow has already been returned");
        }
        validateReturn(borrow, request);
        BigDecimal previewFee = calculatePreviewFee(borrow, request);
        if (previewFee.compareTo(BigDecimal.ZERO) > 0) {
            throw new ConflictException("Borrow has unpaid fee");
        }
        process(borrow, request);
        calculateFee(borrow);
        if (isAllReturned(borrow)) {
            borrow.setReturnDate(LocalDate.now());
            borrow.setStatus(BorrowStatus.RETURNED);
        }
        return toResponse(borrowRepository.save(borrow));
    }

    private void process(Borrow borrow, ReturnBorrowRequest request) {
        for (BookReturnRequest requestItem : request.getBooks()) {

            for (BorrowDetail item : borrow.getDetails()) {
                if (item.getBook().getBookId().equals(requestItem.getBookId())) {
                    int returned = requestItem.getReturnedQuantity();
                    int damaged = requestItem.getDamagedQuantity();
                    int lost = requestItem.getLostQuantity();
                    int total = returned + damaged + lost;

                    checkLimit(item, total);
                    checkStatus(item, returned, damaged, lost);
                    break;
                }
            }
        }
    }

    private boolean isAllReturned(Borrow borrow) {
        for (BorrowDetail item : borrow.getDetails()) {
            int processed = item.getReturnedQuantity() + item.getDamagedQuantity() + item.getLostQuantity();
            if (processed < item.getQuantity()) {
                return false;
            }
        }
        return true;
    }

    private void checkLimit(BorrowDetail item, Integer total) {

        int remaining = item.getQuantity()
                - item.getReturnedQuantity()
                - item.getDamagedQuantity()
                - item.getLostQuantity();

        if (total > remaining) {
            throw new ConflictException(
                    "Số lượng trả vượt quá số lượng đang mượn");
        }
    }

    private void validateReturn(
            Borrow borrow,
            ReturnBorrowRequest request) {

        if (request == null
                || request.getBooks() == null
                || request.getBooks().isEmpty()) {

            throw new ConflictException("Chưa nhập số lượng trả");
        }

        for (BookReturnRequest requestItem : request.getBooks()) {

            boolean found = false;

            for (BorrowDetail item : borrow.getDetails()) {

                if (item.getBook().getBookId()
                        .equals(requestItem.getBookId())) {

                    found = true;

                    int returned = requestItem.getReturnedQuantity();
                    int damaged = requestItem.getDamagedQuantity();
                    int lost = requestItem.getLostQuantity();

                    if (returned < 0
                            || damaged < 0
                            || lost < 0) {

                        throw new ConflictException(
                                "Số lượng không hợp lệ");
                    }

                    int total = returned + damaged + lost;

                    checkLimit(item, total);

                    break;
                }
            }

            if (!found) {
                throw new ResourceNotFoundException(
                        "Book không thuộc phiếu mượn");
            }
        }
    }

    private BigDecimal calculatePreviewFee(
            Borrow borrow,
            ReturnBorrowRequest request) {

        BigDecimal totalFee = BigDecimal.ZERO;
        if (borrow.getStatus() == BorrowStatus.OVERDUE) {
            totalFee = totalFee.add(fine);
        }

        for (BookReturnRequest requestItem : request.getBooks()) {

            for (BorrowDetail item : borrow.getDetails()) {

                if (item.getBook().getBookId()
                        .equals(requestItem.getBookId())) {

                    BigDecimal price = item.getBook().getPrice();

                    int damaged = requestItem.getDamagedQuantity();
                    int lost = requestItem.getLostQuantity();

                    // Hỏng: 2/3 giá sách
                    BigDecimal damagedFee = price
                            .multiply(BigDecimal.valueOf(2))
                            .divide(
                                    BigDecimal.valueOf(3),
                                    0,
                                    java.math.RoundingMode.HALF_UP)
                            .setScale(-3, java.math.RoundingMode.HALF_UP);

                    // Mất: 100% giá sách
                    BigDecimal lostFee = price;

                    BigDecimal fee = damagedFee
                            .multiply(BigDecimal.valueOf(damaged))
                            .add(lostFee.multiply(BigDecimal.valueOf(lost)));

                    totalFee = totalFee.add(fee);

                    break;
                }
            }
        }

        return totalFee;
    }

    private void calculateFee(Borrow borrow) {

        BigDecimal totalFee = BigDecimal.ZERO;
        if (borrow.getStatus() == BorrowStatus.OVERDUE) {
            totalFee = totalFee.add(fine);
        }

        for (BorrowDetail item : borrow.getDetails()) {

            BigDecimal price = item.getBook().getPrice();

            int damaged = item.getDamagedQuantity();
            int lost = item.getLostQuantity();

            // Hỏng: 2/3 giá sách
            BigDecimal damagedFee = price
                    .multiply(BigDecimal.valueOf(2))
                    .divide(
                            BigDecimal.valueOf(3),
                            0,
                            java.math.RoundingMode.HALF_UP)
                    .setScale(-3, java.math.RoundingMode.HALF_UP);

            // Mất: 100% giá sách
            BigDecimal lostFee = price;

            BigDecimal fee = damagedFee
                    .multiply(BigDecimal.valueOf(damaged))
                    .add(lostFee.multiply(BigDecimal.valueOf(lost)));

            item.setFee(fee);

            totalFee = totalFee.add(fee);
        }

        borrow.setTotalFee(totalFee);
    }

    private void checkStatus(
            BorrowDetail item,
            Integer returned,
            Integer damaged,
            Integer lost) {

        item.setReturnedQuantity(
                item.getReturnedQuantity() + returned);
        item.setDamagedQuantity(
                item.getDamagedQuantity() + damaged);
        item.setLostQuantity(
                item.getLostQuantity() + lost);

        String bookId = item.getBook().getBookId();
        Book book = bookRepository.findByBookId(bookId);
        if (book == null) {
            throw new ResourceNotFoundException("Book not found");
        }

        book.setQuantity(book.getQuantity() + returned);
        if (book.getQuantity() > 0) {
            book.setStatus(BookStatus.IN_STOCK);
        } else {
            book.setStatus(BookStatus.OUT_OF_STOCK);
        }
        item.setBook(book);

        bookRepository.saveAndFlush(book);
    }

    @Transactional(readOnly = true)
    public BorrowResponse previewReturn(String borrowId, ReturnBorrowRequest request) {
        if (borrowId == null) {
            throw new ConflictException("BorrowID not null");
        }
        Borrow borrow = borrowRepository.findByBorrowId(borrowId);
        if (borrow == null) {
            throw new ResourceNotFoundException("Borrow not found");
        }
        if (borrow.getStatus() == BorrowStatus.RETURNED) {
            throw new ConflictException("Borrow has already been returned");
        }
        validateReturn(borrow, request);
        BigDecimal totalFee = calculatePreviewFee(borrow, request);
        BorrowResponse response = toResponse(borrow);
        response.setTotalFee(totalFee);
        if (totalFee.compareTo(BigDecimal.ZERO) > 0) {
            response.setPayStatus(PaymentStatus.UNPAID);
        } else {
            response.setPayStatus(PaymentStatus.PAID);
        }
        return response;
    }

    @Transactional
    public BorrowResponse payBorrow(String borrowId, ReturnBorrowRequest request) {
        if (borrowId == null) {
            throw new ConflictException("BorrowID not null");
        }
        Borrow borrow = borrowRepository.findByBorrowId(borrowId);
        if (borrow == null) {
            throw new ResourceNotFoundException("Borrow not found");
        }
        if (borrow.getStatus() == BorrowStatus.RETURNED) {
            throw new ConflictException("Borrow has already been returned");
        }
        validateReturn(borrow, request);
        BigDecimal totalFee = calculatePreviewFee(borrow, request);
        if (totalFee.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ConflictException("Borrow has no fee");
        }
        process(borrow, request);
        calculateFee(borrow);

        borrow.setPayStatus(PaymentStatus.PAID);
        if (isAllReturned(borrow)) {
            borrow.setReturnDate(LocalDate.now());
            borrow.setStatus(BorrowStatus.RETURNED);
        }
        return toResponse(borrowRepository.save(borrow));
    }

    public List<BorrowResponse> getAllBorrows() {
        updateOverdueStatus();
        return toResponse(borrowRepository.findAll());
    }

    public List<BorrowResponse> getBorrowingBooks() {
        updateOverdueStatus();
        return toResponse(borrowRepository.findByStatus(BorrowStatus.BORROWING));
    }

    public List<BorrowResponse> getReturnedBooks() {
        return toResponse(borrowRepository.findByStatus(BorrowStatus.RETURNED));
    }

    public List<BorrowResponse> getOverDueReturn() {
        return toResponse(borrowRepository.findByStatus(BorrowStatus.OVERDUE));
    }

    @Transactional
    public void updateOverdueStatus() {

        List<Borrow> borrowingBooks = borrowRepository.findByStatus(
                BorrowStatus.BORROWING);

        LocalDate today = LocalDate.now();

        for (Borrow borrow : borrowingBooks) {

            if (borrow.getDueDate().isBefore(today)) {

                borrow.setStatus(BorrowStatus.OVERDUE);

                borrowRepository.save(borrow);
            }
        }
    }

    public List<BorrowResponse> searchBorrows(String id, String status) {
        if (id == null) {
            throw new ConflictException("Not null");
        }
        BorrowStatus borrowStatus = null;
        if (status != null && !status.isBlank()
                && !status.equalsIgnoreCase("ALL")) {

            borrowStatus = BorrowStatus.valueOf(status.toUpperCase());
        }

        return toResponse(borrowRepository.searchBorrows(id, borrowStatus));
    }

    @Transactional(readOnly = true)
    public BorrowResponse getBorrowById(String borrowId) {
        if (borrowId == null) {
            throw new ConflictException("BorrowID not null");
        }

        Borrow borrow = borrowRepository.findByBorrowId(borrowId);
        if (borrow == null) {
            throw new ConflictException("Borrow not found");
        }
        return toResponse(borrow);
    }

    public Book getBookById(String bookId) {
        Book book = bookRepository.findByBookId(bookId);
        if (book == null) {
            throw new ResourceNotFoundException("Book not found");
        }
        return book;
    }

    public List<BorrowResponse> sortBorrow(
            String id,
            String status,
            String sortBy) {

        List<BorrowResponse> borrows;

        if (id == null || id.isBlank()) {
            // Không tìm kiếm, lấy danh sách theo status
            if (status == null || status.equalsIgnoreCase("ALL")) {
                borrows = toResponse(borrowRepository.findAll());
            } else {
                BorrowStatus borrowStatus = BorrowStatus.valueOf(status.toUpperCase());

                borrows = toResponse(
                        borrowRepository.findByStatus(borrowStatus));
            }
        } else {
            // Có nhập từ khóa thì mới search
            borrows = searchBorrows(id, status);
        }

        if ("borrowDate_asc".equals(sortBy)) {
            borrows.sort(Comparator.comparing(BorrowResponse::getBorrowDate));
        } else if ("borrowDate_desc".equals(sortBy)) {
            borrows.sort(
                    Comparator.comparing(BorrowResponse::getBorrowDate).reversed());
        } else if ("dueDate_asc".equals(sortBy)) {
            borrows.sort(Comparator.comparing(BorrowResponse::getDueDate));

        } else if ("dueDate_desc".equals(sortBy)) {
            borrows.sort(
                    Comparator.comparing(BorrowResponse::getDueDate).reversed());
        } else if ("borrowId_asc".equals(sortBy)) {
            borrows.sort(
                    Comparator.comparing(BorrowResponse::getBorrowId));
        } else if ("borrowId_desc".equals(sortBy)) {
            borrows.sort(
                    Comparator.comparing(BorrowResponse::getBorrowId).reversed());
        } else if ("readerId_asc".equals(sortBy)) {
            borrows.sort(
                    Comparator.comparing(BorrowResponse::getReaderId));
        } else if ("readerId_desc".equals(sortBy)) {
            borrows.sort(
                    Comparator.comparing(BorrowResponse::getReaderId).reversed());
        }
        return borrows;
    }
}