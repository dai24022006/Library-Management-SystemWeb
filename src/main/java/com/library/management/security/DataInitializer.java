package com.library.management.security;

import com.library.management.enums.Role;
import com.library.management.enums.UserStatus;
import com.library.management.model.User;
import com.library.management.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 1. Tài khoản Quản trị viên (ADMIN)
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User(
                    "admin",
                    passwordEncoder.encode("admin123"),
                    "admin@library.com",
                    Role.ADMIN,
                    UserStatus.APPROVED
            );
            userRepository.save(admin);
            System.out.println(">>> [SEED] Đã tạo ADMIN: admin / admin123");
        }

        // 2. Tài khoản Thủ thư (LIBRARIAN)
        if (!userRepository.existsByUsername("thuthu1")) {
            User librarian = new User(
                    "thuthu1",
                    passwordEncoder.encode("123456"),
                    "thuthu1@library.com",
                    Role.LIBRARIAN,
                    UserStatus.APPROVED
            );
            userRepository.save(librarian);
            System.out.println(">>> [SEED] Đã tạo THỦ THƯ: thuthu1 / 123456");
        }

        // 3. Tài khoản Độc giả (READER - Đã duyệt sẵn)
        if (!userRepository.existsByUsername("docgia1")) {
            User reader = new User(
                    "docgia1",
                    passwordEncoder.encode("123456"),
                    "docgia1@library.com",
                    Role.READER,
                    UserStatus.APPROVED
            );
            userRepository.save(reader);
            System.out.println(">>> [SEED] Đã tạo ĐỘC GIẢ: docgia1 / 123456");
        }
    }
}