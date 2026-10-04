package com.library.management.repository;

import com.library.management.enums.BorrowStatus;
import com.library.management.model.Borrow;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BorrowRepository extends JpaRepository<Borrow, Long> {

        Borrow findByBorrowId(String borrowId);

        List<Borrow> findByStatus(BorrowStatus status);

        List<Borrow> findByStatusAndDueDateBefore(
                        BorrowStatus status,
                        LocalDate date);

        boolean existsByBorrowId(String borrowId);

        @Query("""
                        SELECT DISTINCT b FROM Borrow b
                        LEFT JOIN b.details d
                        WHERE (b.borrowId = :id
                           OR b.reader.readerId = :id)
                           AND (:status IS NULL OR b.status = :status)
                        """)
        List<Borrow> searchBorrows(@Param("id") String id, @Param("status") BorrowStatus status);
}