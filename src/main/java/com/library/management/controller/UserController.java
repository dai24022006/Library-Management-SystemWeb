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

    // 1. API lấy danh sách tất cả tài khoản: GET http://localhost:8080/api/users
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // 2. API Admin tạo tài khoản thủ thư mới: POST http://localhost:8080/api/users
    @PostMapping
    public ResponseEntity<UserResponse> createLibrarian(@RequestBody RegisterRequest request) {
        UserResponse newUser = userService.createLibrarian(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    // 3. API xóa tài khoản thủ thư: DELETE http://localhost:8080/api/users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Xóa tài khoản thành công!");
    }
}