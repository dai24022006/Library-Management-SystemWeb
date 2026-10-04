package com.library.management.dto.borrow;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.library.management.enums.BorrowStatus;
import com.library.management.enums.PaymentStatus;

public class BorrowResponse {
    private String borrowId;

    private String readerId;
    private String readerName;

    private List<BorrowDetailResponse> books;

    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    private BorrowStatus status;
    private BigDecimal totalFee;
    private PaymentStatus payStatus;

    public void setBorrowId(String borrowId) {
        this.borrowId = borrowId;
    }

    public String getBorrowId() {
        return borrowId;
    }

    public void setReaderId(String readerId) {
        this.readerId = readerId;
    }

    public String getReaderId() {
        return readerId;
    }

    public void setReaderName(String readerName) {
        this.readerName = readerName;
    }

    public String getReaderName() {
        return readerName;
    }

    public List<BorrowDetailResponse> getBooks() {
        return books;
    }

    public void setBooks(List<BorrowDetailResponse> books) {
        this.books = books;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setStatus(BorrowStatus status) {
        this.status = status;
    }

    public BorrowStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public PaymentStatus getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(PaymentStatus payStatus) {
        this.payStatus = payStatus;
    }
}
