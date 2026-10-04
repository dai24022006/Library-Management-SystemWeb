package com.library.management.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.library.management.dto.borrow.BorrowRequest;
import com.library.management.dto.borrow.BorrowResponse;
import com.library.management.dto.borrow.ReturnBorrowRequest;
import com.library.management.model.Book;
import com.library.management.service.BorrowService;

@RestController
@RequestMapping("/api/borrows")
@CrossOrigin(origins = "*")
public class BorrowController {

        private final BorrowService borrowService;

        public BorrowController(BorrowService borrowService) {
                this.borrowService = borrowService;
        }

        // Tạo phiếu mượn
        @PostMapping
        public ResponseEntity<BorrowResponse> borrowBook(
                        @RequestBody BorrowRequest request) {

                BorrowResponse borrow = borrowService.borrowBook(request);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(borrow);
        }

        // Trả sách
        @PutMapping("/{borrowId}/return")
        public ResponseEntity<BorrowResponse> returnBook(
                        @PathVariable String borrowId,
                        @RequestBody ReturnBorrowRequest request) {

                return ResponseEntity.ok(
                                borrowService.returnBookByBorrowId(borrowId, request));
        }

        // Preview trả sách
        @PostMapping("/{borrowId}/return/preview")
        public ResponseEntity<BorrowResponse> previewReturn(
                        @PathVariable String borrowId,
                        @RequestBody ReturnBorrowRequest request) {

                return ResponseEntity.ok(
                                borrowService.previewReturn(borrowId, request));
        }

        @PutMapping("/{borrowId}/pay")
        public ResponseEntity<BorrowResponse> payBorrow(
                        @PathVariable String borrowId,
                        @RequestBody ReturnBorrowRequest request) {

                return ResponseEntity.ok(
                                borrowService.payBorrow(borrowId, request));
        }

        // Tất cả lịch sử mượn
        @GetMapping
        public ResponseEntity<List<BorrowResponse>> getAllBorrows() {
                return ResponseEntity.ok(
                                borrowService.getAllBorrows());
        }

        // Đang mượn
        @GetMapping("/borrowing")
        public ResponseEntity<List<BorrowResponse>> getBorrowingBooks() {
                return ResponseEntity.ok(
                                borrowService.getBorrowingBooks());
        }

        // Đã trả
        @GetMapping("/returned")
        public ResponseEntity<List<BorrowResponse>> getReturnedBooks() {
                return ResponseEntity.ok(
                                borrowService.getReturnedBooks());
        }

        // Quá hạn
        @GetMapping("/overdue")
        public ResponseEntity<List<BorrowResponse>> getOverDueReturn() {
                return ResponseEntity.ok(
                                borrowService.getOverDueReturn());
        }

        // Xem chi tiết
        @GetMapping("/{borrowId}/detail")
        public ResponseEntity<BorrowResponse> viewDetail(
                        @PathVariable String borrowId) {

                return ResponseEntity.ok(
                                borrowService.getBorrowById(borrowId));
        }

        @GetMapping("/search")
        public ResponseEntity<List<BorrowResponse>> searchBorrows(
                        @RequestParam String id,
                        @RequestParam(required = false) String status) {

                return ResponseEntity.ok(
                                borrowService.searchBorrows(id, status));
        }

        @GetMapping("/filter")
        public ResponseEntity<List<BorrowResponse>> sortBorrows(
                        @RequestParam(required = false) String id,
                        @RequestParam(required = false) String status,
                        @RequestParam(required = false) String sortBy) {

                return ResponseEntity.ok(
                                borrowService.sortBorrow(id, status, sortBy));
        }

        @GetMapping("/book/{bookId}")
        public ResponseEntity<Book> getBookById(
                        @PathVariable String bookId) {

                return ResponseEntity.ok(
                                borrowService.getBookById(bookId));
        }

        @PutMapping("/update-overdue")
        public void updateOverdueStatus() {
                borrowService.updateOverdueStatus();
        }
}