
package com.library.management.controller;

import com.library.management.model.User;
import com.library.management.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

@RestController
public class SessionAuthController {

    private final UserRepository userRepository;

    public SessionAuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public record LoginRequest(
            String username,
            String password) {
    }

    // ĐĂNG NHẬP
    @PostMapping("/api/session/login")
    public ResponseEntity<Map<String, String>> login(
            @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        if (request.username() == null
                || request.password() == null
                || request.username().isBlank()
                || request.password().isBlank()) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Vui lòng nhập đầy đủ thông tin"));
        }

        Optional<User> result = userRepository
                .findByUsername(request.username().trim());

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message",
                            "Sai tài khoản hoặc mật khẩu"));
        }

        User user = result.get();

        // So sánh mật khẩu thường - chỉ dùng cho demo
        if (user.getPassword() == null ||
                !user.getPassword().equals(request.password())) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message",
                            "Sai tài khoản hoặc mật khẩu"));
        }

        if (user.getRole() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "message",
                            "Tài khoản chưa có vai trò"));
        }

        String role = user.getRole().name();

        if (!role.equals("ADMIN")
                && !role.equals("LIBRARIAN")) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "message",
                            "Tài khoản không có quyền truy cập"));
        }

        // Hủy phiên cũ
        HttpSession oldSession = httpRequest.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }

        // Tạo phiên mới
        HttpSession session = httpRequest.getSession(true);

        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", role);

        session.setMaxInactiveInterval(30 * 60);

        String redirect = role.equals("ADMIN")
                ? "/dashboard"
                : "/borrows";

        return ResponseEntity.ok(Map.of(
                "message", "Đăng nhập thành công",
                "username", user.getUsername(),
                "role", role,
                "redirect", redirect));
    }

    // KIỂM TRA ĐĂNG NHẬP
    @GetMapping("/api/session/me")
    public ResponseEntity<Map<String, String>> me(
            HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute("username") == null
                || session.getAttribute("role") == null) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Chưa đăng nhập"));
        }

        return ResponseEntity.ok(Map.of(
                "username",
                session.getAttribute("username").toString(),
                "role",
                session.getAttribute("role").toString()));
    }

    // ĐĂNG XUẤT
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .location(URI.create("/login?logout"))
                .build();
    }
}
