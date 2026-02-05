package com.company.delivery.infrastructure.security;

/**
 * Security Adapters Package - Authentication & Authorization.
 *
 * <h2>Purpose</h2>
 * <p>This package contains security-related adapters including authentication,
 * authorization, and token handling. All security implementation details
 * are isolated here.</p>
 *
 * <h2>What Belongs Here</h2>
 * <ul>
 *   <li>Authentication filters</li>
 *   <li>JWT token providers/validators</li>
 *   <li>OAuth2 configurations</li>
 *   <li>Security context utilities</li>
 *   <li>Authorization services</li>
 *   <li>User details service implementations</li>
 * </ul>
 *
 * <h2>What Does NOT Belong Here</h2>
 * <ul>
 *   <li>Business logic</li>
 *   <li>User domain models (use domain/model)</li>
 *   <li>User registration logic (use application layer)</li>
 *   <li>HTTP controllers</li>
 * </ul>
 *
 * <h2>Security Components</h2>
 * <pre>
 * security/
 * ├── jwt/                 # JWT token handling
 * │   ├── JwtTokenProvider.java
 * │   └── JwtAuthenticationFilter.java
 * ├── oauth/               # OAuth2 integration (future)
 * ├── AuthenticationPort.java    # Port for auth operations
 * └── SecurityConfig.java        # Spring Security config
 * </pre>
 *
 * <h2>Integration Pattern</h2>
 * <p>Security can be injected into use cases via an output port:</p>
 * <pre>{@code
 * // Port (in application/port/output)
 * public interface AuthenticationPort {
 *     AuthenticatedUser getCurrentUser();
 *     boolean hasPermission(String permission);
 * }
 *
 * // Adapter (in infrastructure/security)
 * @Component
 * public class SpringSecurityAdapter implements AuthenticationPort {
 *     @Override
 *     public AuthenticatedUser getCurrentUser() {
 *         // Get from SecurityContextHolder
 *     }
 * }
 * }</pre>
 *
 * @see com.company.delivery.infrastructure.config.SecurityConfig
 */

