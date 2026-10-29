
package com.library.management.service;

import com.library.management.model.Book;
import com.library.management.enums.BookStatus;
import com.library.management.repository.BookRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // LẤY DANH SÁCH VÀ TÌM KIẾM SÁCH
    public List<Book> getAll(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return bookRepository.findAll();
        }

        return bookRepository.findByTitleContainingIgnoreCase(
                keyword.trim());
    }

    // XEM CHI TIẾT SÁCH
    public Book getById(Long id) {

        return bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy sách"));
    }

    // THÊM SÁCH
    @Transactional
    public Book create(Book request) {

        validate(request);

        if (bookRepository.findByBookId(
                request.getBookId().trim()) != null) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Mã sách đã tồn tại");
        }

        Book book = new Book();

        copyEditableFields(request, book);

        return bookRepository.save(book);
    }

    // SỬA SÁCH
    @Transactional
    public Book update(Long id, Book request) {

        Book book = getById(id);

        validate(request);

        Book duplicate = bookRepository.findByBookId(
                request.getBookId().trim());

        if (duplicate != null && !duplicate.getId().equals(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Mã sách đã tồn tại");
        }

        copyEditableFields(request, book);

        return bookRepository.save(book);
    }

    // KIỂM TRA DỮ LIỆU
    private void validate(Book book) {

        if (book == null
                || book.getBookId() == null
                || book.getBookId().isBlank()
                || book.getTitle() == null
                || book.getTitle().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Thiếu mã sách hoặc tên sách");
        }

        if (book.getQuantity() == null
                || book.getQuantity() < 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Số lượng sách không hợp lệ");
        }
    }

    // CẬP NHẬT THÔNG TIN SÁCH
    private void copyEditableFields(Book source, Book target) {

        target.setBookId(source.getBookId().trim());
        target.setTitle(source.getTitle().trim());
        target.setAuthor(source.getAuthor());
        target.setPublisher(source.getPublisher());
        target.setPublicationYear(source.getPublicationYear());
        target.setCategory(source.getCategory());
        target.setPrice(source.getPrice());
        target.setQuantity(source.getQuantity());

        target.setStatus(
                source.getQuantity() > 0
                        ? BookStatus.IN_STOCK
                        : BookStatus.OUT_OF_STOCK);
    }

    // XÓA SÁCH
    @Transactional
    public void delete(Long id) {

        Book book = getById(id);

        try {
            bookRepository.delete(book);
            bookRepository.flush();

        } catch (DataIntegrityViolationException ex) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Sách có lịch sử mượn/trả, không thể xóa",
                    ex);
        }
    }
}
