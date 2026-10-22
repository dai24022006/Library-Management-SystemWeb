package com.library.management.repository;

import com.library.management.model.Reader;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReaderRepository extends JpaRepository<Reader, Long> {
    List<Reader> findByDeletedFalse();

    Optional<Reader> findByIdAndDeletedFalse(Long id);

    Reader findByReaderId(String readerId);

    Optional<Reader> findByReaderIdAndDeletedFalse(String readerId);

    boolean existsByReaderId(String readerId);

    boolean existsByReaderIdAndDeletedFalse(String readerId);
}
