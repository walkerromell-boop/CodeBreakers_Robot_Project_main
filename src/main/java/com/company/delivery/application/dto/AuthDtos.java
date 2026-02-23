package com.company.delivery.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * All request/response DTOs for authentication.
 */
public final class AuthDtos {

    private AuthDtos() {}

    // =========================================================================
    // REQUESTS
    // =========================================================================

    /** POST /api/v1/auth/register */
    public record RegisterRequest(

        @NotBlank(message = "Student ID is required")
        @Pattern(regexp = "\\d{5,10}", message = "Student ID must be 5-10 digits")
        String studentId,

        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100)
        String name,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password

    ) {}

    /** POST /api/v1/auth/login */
    public record LoginRequest(

        @NotBlank(message = "Student ID is required")
        String studentId,

        @NotBlank(message = "Password is required")
        String password

    ) {}

    /**
     * POST /api/v1/auth/staff/register
     * Protected - requires existing STAFF JWT + a staff registration key.
     */
    public record StaffRegisterRequest(

        @NotBlank(message = "Student ID is required")
        @Pattern(regexp = "\\d{5,10}", message = "Student ID must be 5-10 digits")
        String studentId,

        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100)
        String name,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Staff password must be at least 8 characters")
        String password,

        /** Secret key that proves this is a legitimate staff registration */
        @NotBlank(message = "Staff registration key is required")
        String staffRegistrationKey

    ) {}

    /** POST /api/v1/auth/forgot-password */
    public record ForgotPasswordRequest(

        @NotBlank(message = "Student ID is required")
        String studentId

    ) {}

    /** POST /api/v1/auth/reset-password */
    public record ResetPasswordRequest(

        @NotBlank(message = "Reset token is required")
        String token,

        @NotBlank(message = "New password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String newPassword

    ) {}

    /** POST /api/v1/auth/2fa/verify - verify code during login OR complete setup */
    public record TotpVerifyRequest(

        @NotNull(message = "Code is required")
        Integer code

    ) {}

    // =========================================================================
    // RESPONSES
    // =========================================================================

    /**
     * Returned on successful register or login (when 2FA is NOT enabled).
     * Client stores the token and sends as: Authorization: Bearer <token>
     */
    public record AuthResponse(
        String token,
        String studentId,
        String name,
        String role
    ) {}

    /**
     * Returned when login succeeds but the account has 2FA enabled.
     * Client must POST the 6-digit code to /api/v1/auth/2fa/verify
     * using this pendingToken (valid for 5 minutes only).
     */
    public record TotpChallengeResponse(
        String pendingToken,   // short-lived JWT - only grants access to /2fa/verify
        String message
    ) {}

    /** Returned after POST /api/v1/auth/2fa/setup */
    public record TotpSetupResponse(
        String secret,    // user must save this as backup
        String qrUri,     // render this as a QR code in the frontend
        String message
    ) {}

    /** Generic message response for operations that don't return data */
    public record MessageResponse(String message) {}
}
