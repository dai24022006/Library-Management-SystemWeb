package com.library.management.dto.borrow;

import java.util.List;

public class ReturnBorrowRequest {

    private List<BookReturnRequest> books;

    public List<BookReturnRequest> getBooks() {
        return books;
    }

    public void setBooks(List<BookReturnRequest> books) {
        this.books = books;
    }
}