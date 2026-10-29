
package com.library.management.controller;

import com.library.management.model.Book;
import com.library.management.service.BookService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    private boolean isAdmin(HttpSession session) {
        return "ADMIN".equals(session.getAttribute("role"));
    }

    // LẤY DANH SÁCH / TÌM KIẾM
    @GetMapping
    public List<Book> getAll(
            @RequestParam(required = false) String keyword) {

        return bookService.getAll(keyword);
    }

    // XEM CHI TIẾT
    @GetMapping("/{id}")
    public Book getById(@PathVariable Long id) {
        return bookService.getById(id);
    }

    // THÊM SÁCH
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody Book book,
            HttpSession session) {

        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "message",
                            "Chỉ Admin được thêm sách"));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookService.create(book));
    }

    // SỬA SÁCH
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody Book book,
            HttpSession session) {

        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "message",
                            "Chỉ Admin được sửa sách"));
        }

        return ResponseEntity.ok(
                bookService.update(id, book));
    }

    // XÓA SÁCH
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            HttpSession session) {

        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "message",
                            "Chỉ Admin được xóa sách"));
        }

        bookService.delete(id);

        return ResponseEntity.ok(
                Map.of("message", "Xóa sách thành công"));
    }
}
