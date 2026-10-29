
package com.library.management.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http) throws Exception {

                http
                                // Tạm tắt CSRF cho bản demo localhost
                                .csrf(csrf -> csrf.disable())

                                // Không chặn các API cũ bằng Spring Security
                                .authorizeHttpRequests(auth -> auth
                                                .anyRequest().permitAll())

                                // Đăng nhập tự xử lý bằng HttpSession
                                .formLogin(form -> form.disable())
                                .httpBasic(basic -> basic.disable())
                                .logout(logout -> logout.disable());

                return http.build();
        }
}
