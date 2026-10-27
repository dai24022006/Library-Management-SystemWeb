
package com.library.management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BookPageController {

    @GetMapping("/books")
    public String booksPage() {
        return "book/books";
    }
}
