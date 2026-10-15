package com.library.management.controller;

import com.library.management.dto.auth.LoginRequest;
import com.library.management.dto.auth.LoginResponse;
import com.library.management.dto.auth.RegisterRequest;
import com.library.management.dto.auth.UserResponse;
import com.library.management.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // 1. API Đăng nhập: POST http://localhost:8080/api/auth/login
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    // 2. API Độc giả tự đăng ký: POST http://localhost:8080/api/auth/register
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        UserResponse response = userService.registerReader(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}