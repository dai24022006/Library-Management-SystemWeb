package com.library.management.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Tắt CSRF vì ứng dụng dùng kiến trúc REST API + JWT
            .csrf(csrf -> csrf.disable())

            // Cấu hình Session không lưu trạng thái (Stateless)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Thiết lập quy tắc cấp phép cho các đường dẫn
            .authorizeHttpRequests(auth -> auth
                // 1. Cho phép truy cập công khai vào API đăng nhập và tài nguyên tĩnh (HTML, CSS, JS, ảnh)
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/", "/templates/**", "/static/**", "/css/**", "/js/**", "/images/**", "/*.html").permitAll()

                // 2. Chỉ riêng ADMIN mới có quyền gọi API quản lý người dùng
                .requestMatchers("/api/users/**").hasRole("ADMIN")

                // 3. Cả ADMIN và LIBRARIAN đều được thao tác với Sách, Độc giả và Mượn trả
                .requestMatchers("/api/books/**", "/api/readers/**", "/api/borrows/**").hasAnyRole("ADMIN", "LIBRARIAN", "READER")                                                                                                                                                                                                                                                                                                                                                                                                                                   

                // 4. Mọi yêu cầu còn lại đều phải có Token xác thực
                .anyRequest().authenticated()
            )

            // Gắn bộ lọc kiểm tra JWT vào trước bộ lọc mặc định của Spring
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}