package com.library.management.dto.borrow;

public class BookReturnRequest {

    private String bookId;

    private Integer returnedQuantity;

    private Integer damagedQuantity;

    private Integer lostQuantity;

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public Integer getReturnedQuantity() {
        return returnedQuantity;
    }

    public void setReturnedQuantity(Integer returnedQuantity) {
        this.returnedQuantity = returnedQuantity;
    }

    public Integer getDamagedQuantity() {
        return damagedQuantity;
    }

    public void setDamagedQuantity(Integer damagedQuantity) {
        this.damagedQuantity = damagedQuantity;
    }

    public Integer getLostQuantity() {
        return lostQuantity;
    }

    public void setLostQuantity(Integer lostQuantity) {
        this.lostQuantity = lostQuantity;
    }
}