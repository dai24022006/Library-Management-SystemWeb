package com.library.management.security;

import com.library.management.enums.Role;
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
        // Tự động tạo tài khoản Quản trị viên nếu chưa tồn tại
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User(
                    "admin",
                    passwordEncoder.encode("admin123"), // Mật khẩu mặc định: admin123
                    "admin@library.com",
                    Role.ADMIN
            );
            userRepository.save(admin);
            System.out.println(">>> Đã khởi tạo tài khoản ADMIN mặc định: admin / admin123");
        }
    }
}