package com.library.management.service;

import com.library.management.dto.auth.LoginRequest;
import com.library.management.dto.auth.LoginResponse;
import com.library.management.dto.auth.RegisterRequest;
import com.library.management.dto.auth.UserResponse;
import com.library.management.enums.Role;
import com.library.management.exception.ConflictException;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.model.User;
import com.library.management.repository.UserRepository;
import com.library.management.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 1. Nghiệp vụ Đăng nhập
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Tên đăng nhập hoặc mật khẩu không chính xác"));

        // Kiểm tra mật khẩu đã mã hóa
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ConflictException("Tên đăng nhập hoặc mật khẩu không chính xác");
        }

        // Tạo chuỗi token chứa username và role
        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRole().name());

        UserResponse userResponse = toResponse(user);
        return new LoginResponse(token, userResponse);
    }

    // 2. Nghiệp vụ Admin tạo tài khoản Thủ thư (LIBRARIAN)
    public UserResponse createLibrarian(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Tên đăng nhập đã tồn tại trong hệ thống");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email đã được sử dụng bởi tài khoản khác");
        }

        User librarian = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()), // Mã hóa mật khẩu trước khi lưu DB
                request.getEmail(),
                Role.LIBRARIAN
        );

        User savedUser = userRepository.save(librarian);
        return toResponse(savedUser);
    }

    // 3. Lấy danh sách tất cả người dùng (Dành cho Admin)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    // 4. Xóa tài khoản thủ thư (Dành cho Admin)
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với mã ID: " + id));

        if (user.getRole() == Role.ADMIN) {
            throw new ConflictException("Không được phép xóa tài khoản Quản trị viên (ADMIN)");
        }

        userRepository.delete(user);
    }

    // Hàm phụ trợ chuyển đổi User Entity sang UserResponse DTO
    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}