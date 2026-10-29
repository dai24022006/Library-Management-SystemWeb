package com.library.management.controller;

import com.library.management.model.Reader;
import com.library.management.service.ReaderService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/readers")
public class ReaderController {

    private final ReaderService readerService;

    public ReaderController(ReaderService readerService) {
        this.readerService = readerService;
    }

    private boolean isAdmin(HttpSession session) {
        return "ADMIN".equals(session.getAttribute("role"));
    }

    @GetMapping
    public List<Reader> getAll(
            @RequestParam(required = false) String keyword) {
        return readerService.getAll(keyword);
    }

    @GetMapping("/{id}")
    public Reader getById(@PathVariable Long id) {
        return readerService.getById(id);
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody Reader reader,
            HttpSession session) {

        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Chỉ Admin được thêm độc giả"));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(readerService.create(reader));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody Reader reader,
            HttpSession session) {

        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Chỉ Admin được sửa độc giả"));
        }

        return ResponseEntity.ok(readerService.update(id, reader));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            HttpSession session) {

        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Chỉ Admin được xóa độc giả"));
        }

        readerService.delete(id);

        return ResponseEntity.ok(
                Map.of("message", "Xóa độc giả thành công"));
    }
}