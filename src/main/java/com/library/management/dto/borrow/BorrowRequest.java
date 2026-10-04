package com.library.management.dto.borrow;

import java.util.List;

public class BorrowRequest {
    private String readerId;
    private List<BookBorrowRequest> books;

    public BorrowRequest() {
    }

    public BorrowRequest(
            String readerId,
            List<BookBorrowRequest> books) {
        this.readerId = readerId;
        this.books = books;
    }

    public String getReaderId() {
        return readerId;
    }

    public void setReaderId(String readerId) {
        this.readerId = readerId;
    }

    public List<BookBorrowRequest> getBooks() {
        return books;
    }

    public void setBooks(List<BookBorrowRequest> books) {
        this.books = books;
    }
}
