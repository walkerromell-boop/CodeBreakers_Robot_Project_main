package com.company.delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Delivery Application.
 *
 * <p>This is the Spring Boot bootstrap class. It should contain NO business logic.
 * All application behavior is delegated to the appropriate architectural layers:</p>
 *
 * <ul>
 *   <li><strong>Domain</strong> - Pure business logic and rules</li>
 *   <li><strong>Application</strong> - Use case orchestration</li>
 *   <li><strong>Infrastructure</strong> - External adapters (web, persistence, security)</li>
 * </ul>
 *
 * @see <a href="../../docs/ARCHITECTURE.md">Architecture Documentation</a>
 */
@SpringBootApplication
public class DeliveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeliveryApplication.class, args);
    }

}
