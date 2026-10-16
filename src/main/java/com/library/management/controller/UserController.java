package com.library.management.controller;

import com.library.management.dto.auth.RegisterRequest;
import com.library.management.dto.auth.UserResponse;
import com.library.management.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 1. Lấy tất cả người dùng trong hệ thống
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // 2. Lấy danh sách các tài khoản độc giả đang chờ duyệt: GET /api/users/pending
    @GetMapping("/pending")
    public ResponseEntity<List<UserResponse>> getPendingUsers() {
        return ResponseEntity.ok(userService.getPendingUsers());
    }

    // 3. Admin duyệt tài khoản: PUT /api/users/{id}/approve
    @PutMapping("/{id}/approve")
    public ResponseEntity<UserResponse> approveUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.approveUser(id));
    }

    // 4. Admin từ chối duyệt tài khoản: PUT /api/users/{id}/reject
    @PutMapping("/{id}/reject")
    public ResponseEntity<UserResponse> rejectUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.rejectUser(id));
    }

    // 5. Admin tạo tài khoản thủ thư mới
    @PostMapping
    public ResponseEntity<UserResponse> createLibrarian(@RequestBody RegisterRequest request) {
        UserResponse newUser = userService.createLibrarian(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    // 6. Xóa tài khoản theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Xóa tài khoản thành công!");
    }
}