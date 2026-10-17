
package com.library.management.repository;

import com.library.management.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByDeletedFalse();

    Optional<Book> findByIdAndDeletedFalse(Long id);

    Book findByBookId(String bookId);

    Optional<Book> findByBookIdAndDeletedFalse(String bookId);
}
