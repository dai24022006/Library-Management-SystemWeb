package com.library.management.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

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

    // Cấu hình CORS cho phép mọi nguồn và phương thức truy cập API
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Kích hoạt CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // 2. Tắt CSRF vì dùng REST API + JWT
            .csrf(csrf -> csrf.disable())

            // 3. Cấu hình Session Stateless
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 4. Phân quyền truy cập
            .authorizeHttpRequests(auth -> auth
                // Cho phép các yêu cầu CORS OPTIONS đi qua
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Cho phép truy cập công khai vào API đăng nhập, đăng ký và tài nguyên tĩnh
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/", "/static/**", "/css/**", "/js/**", "/images/**", "/*.html", "/admin/dashboard").permitAll()

                // Chỉ riêng ADMIN mới có quyền gọi API quản lý người dùng
                .requestMatchers("/api/users/**").hasRole("ADMIN")

                // ADMIN, LIBRARIAN và READER đều được thao tác với Sách, Độc giả và Mượn trả
                .requestMatchers("/api/books/**", "/api/readers/**", "/api/borrows/**").hasAnyRole("ADMIN", "LIBRARIAN", "READER")

                // Mọi yêu cầu còn lại đều cần Token xác thực
                .anyRequest().authenticated()
            )

            // Gắn bộ lọc kiểm tra JWT
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}