
package com.library.management.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SessionWebConfig implements WebMvcConfigurer {

    private final SessionInterceptor sessionInterceptor;

    public SessionWebConfig(SessionInterceptor sessionInterceptor) {
        this.sessionInterceptor = sessionInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(sessionInterceptor)
                .addPathPatterns(
                        "/dashboard",
                        "/dashboard.html",
                        "/books",
                        "/books.html",
                        "/readers",
                        "/readers.html",
                        "/borrows",
                        "/borrows.html",
                        "/borrow-form",
                        "/borrow-form.html");
    }
}
