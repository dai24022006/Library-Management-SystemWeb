
package com.library.management.service;

import com.library.management.model.User;
import com.library.management.enums.Role;
import com.library.management.repository.UserRepository;
import com.library.management.dto.auth.RegisterRequest;
import com.library.management.dto.auth.LoginRequest;
import com.library.management.dto.auth.UserResponse;

import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Đăng ký tài khoản
    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        // Lưu mật khẩu trực tiếp - chỉ dùng cho bản demo
        user.setPassword(request.getPassword());

        // Mặc định là thủ thư
        user.setRole(Role.LIBRARIAN);

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole());
    }

    // Kiểm tra thông tin đăng nhập
    public UserResponse login(LoginRequest request) {

        User user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Tên đăng nhập hoặc mật khẩu không đúng"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException(
                    "Tên đăng nhập hoặc mật khẩu không đúng");
        }

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole());
    }
}
