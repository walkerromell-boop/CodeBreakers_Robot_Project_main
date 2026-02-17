package com.company.delivery.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Runs once per request. Checks for a valid JWT in the Authorization header.
 *
 * <p>If a valid token is found, the user is set as authenticated in the
 * SecurityContext so the rest of the request proceeds normally.
 *
 * <p>If the token is missing or invalid, the filter just continues the chain -
 * Spring Security will then block access to protected endpoints with 401.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // No token - let the filter chain continue (unauthenticated)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7); // strip "Bearer "

        if (jwtService.isValid(token)) {
            String userId    = jwtService.extractUserId(token);
            String role      = jwtService.extractRole(token);
            String studentId = jwtService.extractStudentId(token);

            var authority = new SimpleGrantedAuthority("ROLE_" + role);
            var auth = new UsernamePasswordAuthenticationToken(
                userId,      // principal  = user UUID string
                studentId,   // credentials = student ID (read-only here)
                List.of(authority)
            );

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
