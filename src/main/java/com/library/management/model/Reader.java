package com.library.management.model;

import java.time.LocalDate;

import com.library.management.enums.Gender;

import jakarta.persistence.*;

@Entity
@Table(name = "readers")
public class Reader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reader_id", nullable = false, unique = true, length = 50)
    private String readerId; // ma ng doc

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName; // ten ng doc

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth; // ngay sinh format yyyy-mm-dd

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Gender gender; // gioi tinh

    @Column(name = "phone_number", length = 20)
    private String phoneNumber; // sdt

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false; // xoa mem

    public Reader() {
    }

    public Reader(String readerId,
            String fullName,
            LocalDate dateOfBirth,
            Gender gender,
            String phoneNumber) {
        this.readerId = readerId;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReaderId() {
        return readerId;
    }

    public void setReaderId(String readerId) {
        this.readerId = readerId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}