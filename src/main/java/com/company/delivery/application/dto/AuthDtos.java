package com.company.delivery.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTOs for authentication (register + login).
 */
public final class AuthDtos {

    private AuthDtos() {}

    // -------------------------------------------------------------------------
    // Requests
    // -------------------------------------------------------------------------

    /**
     * POST /api/v1/auth/register
     *
     * Example:
     * {
     *   "studentId": "123456",
     *   "name": "Alex Johnson",
     *   "password": "mypassword"
     * }
     */
    public record RegisterRequest(

        @NotBlank(message = "Student ID is required")
        @Pattern(regexp = "\\d{5,10}", message = "Student ID must be 5-10 digits")
        String studentId,

        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password

    ) {}

    /**
     * POST /api/v1/auth/login
     *
     * Example:
     * {
     *   "studentId": "123456",
     *   "password": "mypassword"
     * }
     */
    public record LoginRequest(

        @NotBlank(message = "Student ID is required")
        String studentId,

        @NotBlank(message = "Password is required")
        String password

    ) {}

    // -------------------------------------------------------------------------
    // Responses
    // -------------------------------------------------------------------------

    /**
     * Returned on successful register or login.
     * The client stores the token and sends it as:
     *   Authorization: Bearer <token>
     */
    public record AuthResponse(
        String token,
        String studentId,
        String name,
        String role
    ) {}
}
