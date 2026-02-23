package com.company.delivery.infrastructure.web;

import com.company.delivery.application.dto.AuthDtos.*;
import com.company.delivery.application.service.AuthService;
import com.company.delivery.application.service.AuthService.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoints for authentication.
 *
 * PUBLIC (no token needed):
 *   POST /api/v1/auth/register           - Student sign up
 *   POST /api/v1/auth/staff/register     - Staff sign up (requires staff key)
 *   POST /api/v1/auth/login              - Login with student ID + password
 *   POST /api/v1/auth/2fa/verify         - Submit 2FA code after login
 *   POST /api/v1/auth/forgot-password    - Request a password reset token
 *   POST /api/v1/auth/reset-password     - Apply new password with token
 *
 * PROTECTED (JWT required):
 *   POST /api/v1/auth/2fa/setup          - Begin 2FA enrollment
 *   POST /api/v1/auth/2fa/confirm        - Complete 2FA enrollment
 *   DELETE /api/v1/auth/2fa              - Disable 2FA
 */
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // =========================================================================
    // STUDENT REGISTER
    // =========================================================================

    /**
     * POST /api/v1/auth/register
     * Body: { "studentId": "123456", "name": "Alex", "password": "pass123" }
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    // =========================================================================
    // STAFF REGISTER
    // =========================================================================

    /**
     * POST /api/v1/auth/staff/register
     * Body: { "studentId": "99999", "name": "Staff Name", "password": "staffpass",
     *         "staffRegistrationKey": "your-secret-key" }
     *
     * The staffRegistrationKey must match app.staff-registration-key in application.yml
     */
    @PostMapping("/staff/register")
    public ResponseEntity<AuthResponse> registerStaff(
            @Valid @RequestBody StaffRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerStaff(request));
    }

    // =========================================================================
    // LOGIN
    // =========================================================================

    /**
     * POST /api/v1/auth/login
     * Body: { "studentId": "123456", "password": "pass123" }
     *
     * Returns AuthResponse if no 2FA, or TotpChallengeResponse if 2FA is enabled.
     */
    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // =========================================================================
    // 2FA - VERIFY CODE (second step of login)
    // =========================================================================

    /**
     * POST /api/v1/auth/2fa/verify
     * Header: Authorization: Bearer <pendingToken from TotpChallengeResponse>
     * Body: { "code": 123456 }
     *
     * Returns a full AuthResponse token on success.
     */
    @PostMapping("/2fa/verify")
    public ResponseEntity<AuthResponse> verifyTotp(
            Authentication auth,
            @Valid @RequestBody TotpVerifyRequest request) {
        String userId = (String) auth.getPrincipal();
        return ResponseEntity.ok(authService.verifyTotp(userId, request));
    }

    // =========================================================================
    // 2FA - SETUP (authenticated)
    // =========================================================================

    /**
     * POST /api/v1/auth/2fa/setup
     * Header: Authorization: Bearer <full token>
     *
     * Returns a QR code URI and secret. Render the qrUri as a QR code in your frontend.
     */
    @PostMapping("/2fa/setup")
    public ResponseEntity<TotpSetupResponse> setupTotp(Authentication auth) {
        String userId = (String) auth.getPrincipal();
        return ResponseEntity.ok(authService.setupTotp(userId));
    }

    // =========================================================================
    // 2FA - CONFIRM SETUP
    // =========================================================================

    /**
     * POST /api/v1/auth/2fa/confirm
     * Header: Authorization: Bearer <full token>
     * Body: { "code": 123456 }
     *
     * Completes 2FA enrollment. All future logins will require a code.
     */
    @PostMapping("/2fa/confirm")
    public ResponseEntity<AuthResponse> confirmTotpSetup(
            Authentication auth,
            @Valid @RequestBody TotpVerifyRequest request) {
        String userId = (String) auth.getPrincipal();
        return ResponseEntity.ok(authService.confirmTotpSetup(userId, request));
    }

    // =========================================================================
    // 2FA - DISABLE
    // =========================================================================

    /**
     * DELETE /api/v1/auth/2fa
     * Header: Authorization: Bearer <full token>
     */
    @DeleteMapping("/2fa")
    public ResponseEntity<MessageResponse> disableTotp(Authentication auth) {
        String userId = (String) auth.getPrincipal();
        return ResponseEntity.ok(authService.disableTotp(userId));
    }

    // =========================================================================
    // FORGOT PASSWORD
    // =========================================================================

    /**
     * POST /api/v1/auth/forgot-password
     * Body: { "studentId": "123456" }
     *
     * Prints reset token to server console (demo mode).
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.forgotPassword(request));
    }

    // =========================================================================
    // RESET PASSWORD
    // =========================================================================

    /**
     * POST /api/v1/auth/reset-password
     * Body: { "token": "abc123...", "newPassword": "newpass123" }
     */
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }

    // =========================================================================
    // Error handlers
    // =========================================================================

    @ExceptionHandler(StudentIdAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(StudentIdAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse("STUDENT_ID_TAKEN", ex.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse("INVALID_CREDENTIALS", ex.getMessage()));
    }

    @ExceptionHandler(InvalidStaffKeyException.class)
    public ResponseEntity<ErrorResponse> handleBadStaffKey(InvalidStaffKeyException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new ErrorResponse("INVALID_STAFF_KEY", ex.getMessage()));
    }

    @ExceptionHandler(InvalidTotpCodeException.class)
    public ResponseEntity<ErrorResponse> handleBadTotp(InvalidTotpCodeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse("INVALID_2FA_CODE", ex.getMessage()));
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("BAD_REQUEST", ex.getMessage()));
    }

    public record ErrorResponse(String code, String message) {}
}
