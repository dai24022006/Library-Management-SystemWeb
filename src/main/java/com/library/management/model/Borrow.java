package com.library.management.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.library.management.enums.BorrowStatus;
import com.library.management.enums.PaymentStatus;

import jakarta.persistence.*;

@Entity
@Table(name = "borrows")
public class Borrow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "borrow_id", nullable = false, unique = true, length = 50)
    private String borrowId; // ma don

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reader_id", nullable = false)
    private Reader reader; // nguoi doc

    @Column(name = "borrow_date", nullable = false)
    private LocalDate borrowDate; // ngay muon

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate; // han tra

    @Column(name = "return_date")
    private LocalDate returnDate; // ngay tra

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BorrowStatus status; // trang thai

    @OneToMany(mappedBy = "borrow", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BorrowDetail> details = new ArrayList<>();

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalFee = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    public Borrow() {
    }

    public Borrow(String borrowId,
            Reader reader,
            LocalDate borrowDate,
            LocalDate dueDate,
            BorrowStatus status) {
        this.borrowId = borrowId;
        this.reader = reader;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(String borrowId) {
        this.borrowId = borrowId;
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public BorrowStatus getStatus() {
        return status;
    }

    public void setStatus(BorrowStatus status) {
        this.status = status;
    }

    public List<BorrowDetail> getDetails() {
        return details;
    }

    public void setDetails(List<BorrowDetail> details) {
        this.details = details;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public PaymentStatus getPayStatus() {
        return paymentStatus;
    }

    public void setPayStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}