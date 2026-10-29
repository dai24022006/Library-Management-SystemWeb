package com.library.management.repository;

import com.library.management.model.Reader;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReaderRepository extends JpaRepository<Reader, Long> {

    Reader findByReaderId(String readerId);

    List<Reader> findByFullNameContainingIgnoreCase(String fullName);
}