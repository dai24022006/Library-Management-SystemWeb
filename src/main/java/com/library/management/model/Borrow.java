package com.library.management.model;

import java.time.LocalDate;

import com.library.management.enums.BorrowStatus;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book; // sach

    @Column(name = "borrow_date", nullable = false)
    private LocalDate borrowDate; // ngay muon

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate; // han tra

    @Column(name = "return_date")
    private LocalDate returnDate; // ngay tra

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BorrowStatus status; // trang thai

    public Borrow() {
    }

    public Borrow(String borrowId,
            Reader reader,
            Book book,
            LocalDate borrowDate,
            LocalDate dueDate,
            LocalDate returnDate,
            BorrowStatus status) {
        this.borrowId = borrowId;
        this.reader = reader;
        this.book = book;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
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

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
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
}