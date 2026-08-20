package com.library.management.repository;

import com.library.management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);// tim theo ten

    boolean existsByUsername(String username); // ktra co trung ten kh

    boolean existsByEmail(String email); // ktra co mail
}