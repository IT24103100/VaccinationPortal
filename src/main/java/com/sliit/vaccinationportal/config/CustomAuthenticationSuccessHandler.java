package com.sliit.vaccinationportal.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

/**
 * This component handles the logic to be executed after a user successfully logs in.
 * Its primary purpose is to redirect the user to the appropriate dashboard
 * based on their assigned role (e.g., ADMIN, NURSE, USER, etc.).
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * This method is triggered by Spring Security upon successful authentication.
     * It checks the user's role and redirects them to the correct page.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // Use Spring Security's AuthorityUtils to get a set of role strings (e.g., "ROLE_ADMIN").
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        // Check for roles and redirect accordingly, in order of highest privilege to lowest.
        if (roles.contains("ROLE_ADMIN")) {
            response.sendRedirect("/admin/dashboard");
        } else if (roles.contains("ROLE_MINISTRY_OF_HEALTH")) {
            response.sendRedirect("/moh/dashboard");
        } else if (roles.contains("ROLE_INVENTORY_MANAGER")) {
            response.sendRedirect("/inventory/dashboard");
        } else if (roles.contains("ROLE_PHI")) {
            response.sendRedirect("/phi/dashboard");
        } else if (roles.contains("ROLE_NURSE")) {
            response.sendRedirect("/nurse/dashboard");
        } else if (roles.contains("ROLE_USER")) {
            // Redirect normal users to the public homepage after login.
            response.sendRedirect("/");
        } else {
            // As a fallback for any other case, send them to the default homepage.
            response.sendRedirect("/");
        }
    }
}