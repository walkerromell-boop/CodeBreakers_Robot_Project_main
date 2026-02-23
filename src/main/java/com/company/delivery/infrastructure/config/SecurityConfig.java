package com.company.delivery.infrastructure.config;

import com.company.delivery.infrastructure.security.JwtAuthFilter;
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

/**
 * Security configuration with JWT + role-based access control.
 *
 * STUDENT can:
 *   POST   /api/v1/orders               - place an order
 *   GET    /api/v1/orders/active        - view active orders
 *   GET    /api/v1/orders/history       - view order history
 *   GET    /api/v1/orders/{id}          - view specific order
 *   POST   /api/v1/orders/{id}/cancel   - cancel their order
 *
 * STAFF only:
 *   POST   /api/v1/orders/{id}/confirm  - confirm an order
 *   POST   /api/v1/orders/{id}/dispatch - dispatch an order
 *   POST   /api/v1/orders/{id}/deliver  - mark as delivered
 *
 * PUBLIC (no token):
 *   /api/v1/auth/**
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .cors(cors -> cors.disable())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // ── Public endpoints ────────────────────────────────────────
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**",
                                 "/api/swagger-ui/**", "/api/v3/api-docs/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()

                // ── Staff-only order operations ──────────────────────────────
                .requestMatchers(HttpMethod.POST, "/api/v1/orders/*/confirm").hasRole("STAFF")
                .requestMatchers(HttpMethod.POST, "/api/v1/orders/*/dispatch").hasRole("STAFF")
                .requestMatchers(HttpMethod.POST, "/api/v1/orders/*/deliver").hasRole("STAFF")

                // ── Student + Staff can do the rest ──────────────────────────
                .requestMatchers("/api/v1/orders/**")
                    .hasAnyRole("STUDENT", "STAFF")

                // ── Everything else requires authentication ──────────────────
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
