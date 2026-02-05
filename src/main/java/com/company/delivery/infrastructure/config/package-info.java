package com.company.delivery.infrastructure.config;

/**
 * Configuration Package - Spring Configuration Classes.
 *
 * <h2>Purpose</h2>
 * <p>This package contains all Spring configuration classes including bean
 * definitions, security configuration, and infrastructure setup.</p>
 *
 * <h2>What Belongs Here</h2>
 * <ul>
 *   <li>Spring {@code @Configuration} classes</li>
 *   <li>Bean definitions</li>
 *   <li>Security configuration</li>
 *   <li>Database configuration</li>
 *   <li>Messaging configuration</li>
 *   <li>CORS configuration</li>
 *   <li>OpenAPI/Swagger configuration</li>
 * </ul>
 *
 * <h2>What Does NOT Belong Here</h2>
 * <ul>
 *   <li>Business logic</li>
 *   <li>Controllers</li>
 *   <li>Repository implementations</li>
 *   <li>Domain objects</li>
 * </ul>
 *
 * <h2>Configuration Classes</h2>
 * <pre>
 * config/
 * ├── SecurityConfig.java       # Spring Security setup
 * ├── PersistenceConfig.java    # JPA/Database setup
 * ├── WebConfig.java            # CORS, converters
 * ├── MessagingConfig.java      # Message queue setup
 * └── OpenApiConfig.java        # API documentation
 * </pre>
 *
 * <h2>Profile-Specific Configuration</h2>
 * <p>Use {@code @Profile} annotation for environment-specific beans:</p>
 * <pre>{@code
 * @Configuration
 * @Profile("dev")
 * public class DevSecurityConfig {
 *     // Development-only security settings
 * }
 * }</pre>
 *
 * @see org.springframework.context.annotation.Configuration
 */

