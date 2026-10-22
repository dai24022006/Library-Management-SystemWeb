package com.library.management.service;

import com.library.management.dto.auth.LoginRequest;
import com.library.management.dto.auth.LoginResponse;
import com.library.management.dto.auth.RegisterRequest;
import com.library.management.dto.auth.UserResponse;
import com.library.management.enums.Gender;
import com.library.management.enums.Role;
import com.library.management.enums.UserStatus;
import com.library.management.exception.ConflictException;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.model.Reader;
import com.library.management.model.User;
import com.library.management.repository.ReaderRepository;
import com.library.management.repository.UserRepository;
import com.library.management.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ReaderRepository readerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserService(UserRepository userRepository,
                       ReaderRepository readerRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.readerRepository = readerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 1. Nghiệp vụ Đăng nhập
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

    // 2. Độc giả tự đăng ký tài khoản (Trạng thái PENDING)
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
                UserStatus.PENDING
        );

        User savedUser = userRepository.save(reader);
        return toResponse(savedUser);
    }

    // 3. Admin tạo trực tiếp tài khoản Thủ thư (Duyệt luôn APPROVED)
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

    // 4. Lấy danh sách tài khoản chờ duyệt
    public List<UserResponse> getPendingUsers() {
        return userRepository.findAll().stream()
                .filter(u -> u.getStatus() == UserStatus.PENDING)
                .map(this::toResponse)
                .toList();
    }

    // 5. Admin Duyệt tài khoản ➔ TỰ ĐỘNG TẠO HỒ SƠ ĐỘC GIẢ VÀO BẢNG READERS
    public UserResponse approveUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));
        user.setStatus(UserStatus.APPROVED);
        User savedUser = userRepository.save(user);

        // Nếu là Độc giả thì tự sinh mã thẻ độc giả (Ví dụ R003, R004...) và lưu vào bảng readers
        if (savedUser.getRole() == Role.READER) {
            String readerCode = String.format("R%03d", savedUser.getId());
            if (!readerRepository.existsByReaderId(readerCode)) {
                Reader readerProfile = new Reader(
                        readerCode,
                        savedUser.getUsername(),
                        LocalDate.of(2000, 1, 1),
                        Gender.OTHER,
                        "0900000000"
                );
                readerRepository.save(readerProfile);
                System.out.println(">>> Đã tự động tạo hồ sơ thẻ độc giả: " + readerCode + " cho tài khoản: " + savedUser.getUsername());
            }
        }

        return toResponse(savedUser);
    }

    // 6. Admin Từ chối duyệt tài khoản
    public UserResponse rejectUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));
        user.setStatus(UserStatus.REJECTED);
        return toResponse(userRepository.save(user));
    }

    // 7. Lấy danh sách tất cả người dùng
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
        String readerId = user.getRole() == Role.READER ? String.format("R%03d", user.getId()) : null;
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getStatus() != null ? user.getStatus().name() : UserStatus.APPROVED.name(),
                readerId
        );
    }
}