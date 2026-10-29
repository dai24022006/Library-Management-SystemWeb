
package com.library.management.controller;

import com.library.management.enums.Role;
import com.library.management.model.User;
import com.library.management.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/librarians")
public class AdminLibrarianController {

    private final UserRepository userRepository;

    public AdminLibrarianController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public record LibrarianRequest(
            String username,
            String email,
            String password) {
    }

    public record LibrarianResponse(
            Long id,
            String username,
            String email,
            Role role) {
    }

    private boolean isAdmin(HttpSession session) {
        return "ADMIN".equals(session.getAttribute("role"));
    }

    private LibrarianResponse toResponse(User user) {
        return new LibrarianResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole());
    }

    // DANH SÁCH THỦ THƯ
    @GetMapping
    public ResponseEntity<?> getAll(HttpSession session) {

        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Không có quyền truy cập"));
        }

        List<LibrarianResponse> result = userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() == Role.LIBRARIAN)
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(result);
    }

    // THÊM THỦ THƯ
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody LibrarianRequest request,
            HttpSession session) {

        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Không có quyền truy cập"));
        }

        if (request.username() == null
                || request.username().isBlank()
                || request.email() == null
                || request.email().isBlank()
                || request.password() == null
                || request.password().isBlank()) {

            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Thiếu thông tin tài khoản"));
        }

        String username = request.username().trim();
        String email = request.email().trim();

        if (userRepository.existsByUsername(username)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Tên đăng nhập đã tồn tại"));
        }

        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email đã tồn tại"));
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);

        // Mật khẩu thường theo bản demo hiện tại
        user.setPassword(request.password());

        user.setRole(Role.LIBRARIAN);

        try {
            User saved = userRepository.saveAndFlush(user);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(toResponse(saved));

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Dữ liệu tài khoản không hợp lệ hoặc bị trùng"));
        }
    }

    // SỬA THÔNG TIN THỦ THƯ
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody LibrarianRequest request,
            HttpSession session) {

        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Không có quyền truy cập"));
        }

        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Không tìm thấy tài khoản"));
        }

        if (user.getRole() != Role.LIBRARIAN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Chỉ được sửa tài khoản thủ thư"));
        }

        if (request.username() == null
                || request.username().isBlank()
                || request.email() == null
                || request.email().isBlank()) {

            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Thiếu thông tin tài khoản"));
        }

        String username = request.username().trim();
        String email = request.email().trim();

        // Kiểm tra trùng tên đăng nhập
        if (!user.getUsername().equals(username)
                && userRepository.existsByUsername(username)) {

            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Tên đăng nhập đã tồn tại"));
        }

        // Kiểm tra trùng email
        if (!user.getEmail().equals(email)
                && userRepository.existsByEmail(email)) {

            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email đã tồn tại"));
        }

        user.setUsername(username);
        user.setEmail(email);

        // Để trống mật khẩu sẽ giữ mật khẩu cũ
        if (request.password() != null
                && !request.password().isBlank()) {

            user.setPassword(request.password());
        }

        try {
            User updated = userRepository.saveAndFlush(user);

            return ResponseEntity.ok(toResponse(updated));

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Thông tin tài khoản không hợp lệ hoặc bị trùng"));
        }
    }

    // XÓA THỦ THƯ
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            HttpSession session) {

        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Không có quyền truy cập"));
        }

        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Không tìm thấy tài khoản"));
        }

        if (user.getRole() != Role.LIBRARIAN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Chỉ được xóa tài khoản thủ thư"));
        }

        try {
            userRepository.delete(user);
            userRepository.flush();

            return ResponseEntity.ok(
                    Map.of("message", "Xóa tài khoản thành công"));

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "message",
                            "Không thể xóa tài khoản đang được dữ liệu khác sử dụng"));
        }
    }
}
