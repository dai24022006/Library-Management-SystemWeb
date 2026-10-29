
package com.library.management.controller;

import java.net.URI;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

@RestController
public class PageController {

    // Đọc HTML thuần trong templates
    private ResponseEntity<ClassPathResource> page(String name) {

        ClassPathResource resource = new ClassPathResource("templates/" + name);

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .cacheControl(CacheControl.noStore())
                .body(resource);
    }

    // Mở localhost:8080
    @GetMapping("/")
    public ResponseEntity<Void> home(HttpSession session) {

        Object role = session.getAttribute("role");

        String target = "/login";

        if ("ADMIN".equals(role)) {
            target = "/dashboard";
        } else if ("LIBRARIAN".equals(role)) {
            target = "/borrows";
        }

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(target))
                .build();
    }

    @GetMapping({ "/login", "/login.html" })
    public ResponseEntity<ClassPathResource> login() {
        return page("login.html");
    }

    @GetMapping({ "/dashboard", "/dashboard.html" })
    public ResponseEntity<ClassPathResource> dashboard() {
        return page("dashboard.html");
    }

    @GetMapping({ "/books", "/books.html" })
    public ResponseEntity<ClassPathResource> books() {
        return page("book/books.html");
    }

    @GetMapping({ "/readers", "/readers.html" })
    public ResponseEntity<ClassPathResource> readers() {
        return page("readers.html");
    }

    @GetMapping({ "/borrows", "/borrows.html" })
    public ResponseEntity<ClassPathResource> borrows() {
        return page("borrow/borrows.html");
    }

    @GetMapping({ "/borrow-form", "/borrow-form.html" })
    public ResponseEntity<ClassPathResource> borrowForm() {
        return page("borrow/borrow-form.html");
    }
}
