package com.library.management.controller;

import com.library.management.model.Reader;
import com.library.management.service.ReaderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/readers")
public class ReaderController {
    private final ReaderService readerService;

    public ReaderController(ReaderService readerService) {
        this.readerService = readerService;
    }

    @GetMapping
    public ResponseEntity<List<Reader>> getAllReaders() {
        return ResponseEntity.ok(readerService.getAllReaders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reader> getReaderByID(@PathVariable Long id) {
        Reader reader = readerService.getReaderById(id);
        if (reader == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(reader);
    }

    @PostMapping
    public ResponseEntity<Reader> addReader(@Valid @RequestBody Reader reader) {
        Reader newReader = readerService.addReader(reader);
        if (newReader == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(newReader);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reader> updateReader(
            @PathVariable Long id,
            @Valid @RequestBody Reader reader) {
        if (readerService.getReaderById(id) == null) {
            return ResponseEntity.notFound().build();
        }

        Reader updatedReader = readerService.updateReader(id, reader);
        if (updatedReader == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(updatedReader);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReader(@PathVariable Long id) {
        boolean deleted = readerService.deleteReader(id);
        if (deleted) {
            return ResponseEntity.ok("Reader deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
