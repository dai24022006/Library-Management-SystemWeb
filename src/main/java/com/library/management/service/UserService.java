package com.library.management.service;

import com.library.management.dto.auth.LoginRequest;
import com.library.management.dto.auth.LoginResponse;
import com.library.management.dto.auth.RegisterRequest;
import com.library.management.dto.auth.UserResponse;
import com.library.management.enums.Role;
import com.library.management.enums.UserStatus;
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

    // 1. Nghiệp vụ Đăng nhập (Có kiểm tra tài khoản đã được Admin duyệt chưa)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Tên đăng nhập hoặc mật khẩu không chính xác"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ConflictException("Tên đăng nhập hoặc mật khẩu không chính xác");
        }

        // Kiểm tra trạng thái duyệt tài khoản
        if (user.getStatus() == UserStatus.PENDING) {
            throw new ConflictException("Tài khoản của bạn đang chờ Quản trị viên duyệt, vui lòng thử lại sau!");
        }
        if (user.getStatus() == UserStatus.REJECTED) {
            throw new ConflictException("Tài khoản của bạn đã bị từ chối hoặc bị khóa!");
        }

        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRole().name());
        return new LoginResponse(token, toResponse(user));
    }

    // 2. Nghiệp vụ Độc giả tự đăng ký tài khoản (Mặc định trạng thái là PENDING chờ duyệt)
    public UserResponse registerReader(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Tên đăng nhập đã tồn tại trong hệ thống");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email đã được sử dụng bởi tài khoản khác");
        }

        User reader = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getEmail(),
                Role.READER,
                UserStatus.PENDING // Chờ duyệt
        );

        User savedUser = userRepository.save(reader);
        return toResponse(savedUser);
    }

    // 3. Nghi vụ Admin tạo trực tiếp tài khoản Thủ thư (Mặc định duyệt luôn APPROVED)
    public UserResponse createLibrarian(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Tên đăng nhập đã tồn tại trong hệ thống");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email đã được sử dụng bởi tài khoản khác");
        }

        User librarian = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getEmail(),
                Role.LIBRARIAN,
                UserStatus.APPROVED
        );

        User savedUser = userRepository.save(librarian);
        return toResponse(savedUser);
    }

    // 4. Admin lấy danh sách các tài khoản đang chờ duyệt
    public List<UserResponse> getPendingUsers() {
        return userRepository.findAll().stream()
                .filter(u -> u.getStatus() == UserStatus.PENDING)
                .map(this::toResponse)
                .toList();
    }

    // 5. Admin Duyệt tài khoản độc giả
    public UserResponse approveUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));
        user.setStatus(UserStatus.APPROVED);
        return toResponse(userRepository.save(user));
    }

    // 6. Admin Từ chối duyệt tài khoản
    public UserResponse rejectUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));
        user.setStatus(UserStatus.REJECTED);
        return toResponse(userRepository.save(user));
    }

    // 7. Lấy danh sách tất cả người dùng trong hệ thống
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    // 8. Xóa tài khoản
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));

        if (user.getRole() == Role.ADMIN) {
            throw new ConflictException("Không được phép xóa tài khoản Quản trị viên (ADMIN)");
        }

        userRepository.delete(user);
    }

    // Hàm phụ trợ chuyển đổi sang DTO
    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getStatus() != null ? user.getStatus().name() : UserStatus.APPROVED.name()
        );
    }
}