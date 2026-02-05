package com.company.delivery.infrastructure.config;

/**
 * Spring Security configuration.
 *
 * <p>This class configures Spring Security for the application, including
 * authentication, authorization, CORS, and CSRF settings.</p>
 *
 * <h2>Security Features (Placeholder)</h2>
 * <ul>
 *   <li>JWT-based authentication</li>
 *   <li>Role-based authorization</li>
 *   <li>CORS configuration for frontend</li>
 *   <li>CSRF protection settings</li>
 *   <li>Endpoint security rules</li>
 * </ul>
 *
 * <h2>Endpoint Security (Placeholder)</h2>
 * <pre>
 * Public endpoints:
 *   - POST /api/v1/auth/login
 *   - POST /api/v1/auth/register
 *   - GET /actuator/health
 *
 * Authenticated endpoints:
 *   - /api/v1/** (requires valid JWT)
 *
 * Admin endpoints:
 *   - /api/v1/admin/** (requires ADMIN role)
 * </pre>
 *
 * <p><strong>TODO:</strong> Implement security configuration when requirements are finalized.</p>
 *
 * @see com.company.delivery.infrastructure.security Security Adapters
 */
// @Configuration
// @EnableWebSecurity
public class SecurityConfig {

    // ==========================================================================
    // PLACEHOLDER - No implementation yet
    // ==========================================================================
    //
    // Future configuration may include:
    //
    // @Bean
    // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    //     return http
    //         .csrf(csrf -> csrf.disable())  // Stateless API
    //         .sessionManagement(session ->
    //             session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    //         .authorizeHttpRequests(auth -> auth
    //             .requestMatchers("/api/v1/auth/**").permitAll()
    //             .requestMatchers("/actuator/health").permitAll()
    //             .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
    //             .anyRequest().authenticated()
    //         )
    //         .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
    //         .build();
    // }
    //
    // @Bean
    // public PasswordEncoder passwordEncoder() {
    //     return new BCryptPasswordEncoder();
    // }
    // ==========================================================================

}
