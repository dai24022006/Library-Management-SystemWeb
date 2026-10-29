
package com.library.management.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        HttpSession session = request.getSession(false);

        // Chưa đăng nhập
        if (session == null ||
                session.getAttribute("role") == null) {

            response.sendRedirect("/login");
            return false;
        }

        String role = session.getAttribute("role").toString();
        String path = request.getRequestURI();

        // Dashboard chỉ dành cho ADMIN
        if (path.equals("/dashboard")
                || path.equals("/dashboard.html")) {

            if (!role.equals("ADMIN")) {
                response.sendRedirect("/borrows");
                return false;
            }
        }

        // Chỉ cho phép ADMIN và LIBRARIAN
        if (!role.equals("ADMIN")
                && !role.equals("LIBRARIAN")) {

            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        return true;
    }
}
