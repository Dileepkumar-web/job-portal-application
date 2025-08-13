package com.telusko.jobportal.config;

// --- IMPORT STATEMENTS ---
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
// --- END OF IMPORT STATEMENTS ---

// Handles redirection after successful login based on user role
@Component // Spring manages this as a bean
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // Get the roles (authorities) assigned to the authenticated user
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // Check if the user has the ADMIN role.
        // The authority string should match what your UserDetails implementation provides (e.g., "ROLE_ADMIN").
        boolean isAdmin = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            // If the user IS an admin, send them to the admin dashboard URL
            response.sendRedirect("/admin/dashboard");
        } else {
            // If the user is NOT an admin (assuming they are a regular user), send them to the user dashboard URL
            // You could add more specific checks here if you had more roles
            response.sendRedirect("/user/dashboard");
        }
        // Ensure the response is committed and no further handling is needed by the filter chain
        // response.flushBuffer(); // Usually sendRedirect handles this
    }
}