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
 * Validates JWT on every request.
 *
 * Only FULL tokens grant access to protected endpoints.
 * PENDING_2FA tokens are only accepted at /api/v1/auth/2fa/verify.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String TOTP_VERIFY_PATH = "/api/v1/auth/2fa/verify";

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

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        if (!jwtService.isValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        boolean isPending = jwtService.isPending2faToken(token);
        boolean isFull    = jwtService.isFullToken(token);
        String  path      = request.getServletPath();

        // Pending 2FA tokens only work at the verify endpoint
        if (isPending && !path.equals(TOTP_VERIFY_PATH)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Full tokens (or pending at the verify endpoint) - authenticate
        if (isFull || (isPending && path.equals(TOTP_VERIFY_PATH))) {
            String role      = jwtService.extractRole(token);
            String userId    = jwtService.extractUserId(token);
            String studentId = jwtService.extractStudentId(token);

            var authority = new SimpleGrantedAuthority("ROLE_" + role);
            var auth = new UsernamePasswordAuthenticationToken(
                userId, studentId, List.of(authority)
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
