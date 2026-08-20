package com.library.management.model;

import java.math.BigDecimal;

import com.library.management.enums.BookStatus;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "book_id", nullable = false, unique = true, length = 50)
    private String bookId;// ma sach

    @Column(nullable = false, length = 255)
    private String title; // ten sach

    @Column(nullable = false, length = 255)
    private String author; // tac gia

    @Column(length = 255)
    private String publisher; // nha xuat ban

    @Column(name = "publication_year")
    private Integer publicationYear; // nam xuat ban

    @Column(length = 100)
    private String category; // danh muc

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price; // gia

    @Column(nullable = false)
    private Integer quantity; // so luong

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BookStatus status; // trang thai

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false; // xoa sach dạng xoa mem(kh xoá hẳn mà ẩn đi thôi)

    public Book() {
    }

    public Book(String bookId,
            String title,
            String author,
            String publisher,
            Integer publicationYear,
            String category,
            BigDecimal price,
            Integer quantity,
            BookStatus status) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public BookStatus getStatus() {
        return status;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
