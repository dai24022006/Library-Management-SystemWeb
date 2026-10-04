package com.library.management.dto.borrow;

public class BookBorrowRequest {

    private String bookId;
    private Integer quantity;

    public BookBorrowRequest() {
    }

    public BookBorrowRequest(String bookId, Integer quantity) {
        this.bookId = bookId;
        this.quantity = quantity;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}