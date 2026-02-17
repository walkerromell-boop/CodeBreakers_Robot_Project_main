package com.company.delivery.infrastructure.web;

import com.company.delivery.application.dto.AuthDtos.AuthResponse;
import com.company.delivery.application.dto.AuthDtos.LoginRequest;
import com.company.delivery.application.dto.AuthDtos.RegisterRequest;
import com.company.delivery.application.service.AuthService;
import com.company.delivery.application.service.AuthService.InvalidCredentialsException;
import com.company.delivery.application.service.AuthService.StudentIdAlreadyExistsException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoints for authentication.
 *
 * <p>Both endpoints are public (no token required):
 * <ul>
 *   <li>POST /api/v1/auth/register  - Create account with school credentials</li>
 *   <li>POST /api/v1/auth/login     - Log in with school credentials</li>
 * </ul>
 *
 * <p>On success, both return a JWT token. The client stores this token
 * and sends it with every subsequent request as:
 * <pre>Authorization: Bearer &lt;token&gt;</pre>
 */
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // -------------------------------------------------------------------------
    // POST /api/v1/auth/register
    // -------------------------------------------------------------------------

    /**
     * Register a new student account.
     *
     * Request body:
     * {
     *   "studentId": "123456",
     *   "name":      "Alex Johnson",
     *   "password":  "mypassword"
     * }
     *
     * Success (201):
     * {
     *   "token":     "eyJhbGci...",
     *   "studentId": "123456",
     *   "name":      "Alex Johnson",
     *   "role":      "STUDENT"
     * }
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // -------------------------------------------------------------------------
    // POST /api/v1/auth/login
    // -------------------------------------------------------------------------

    /**
     * Log in with school credentials.
     *
     * Request body:
     * {
     *   "studentId": "123456",
     *   "password":  "mypassword"
     * }
     *
     * Success (200):
     * {
     *   "token":     "eyJhbGci...",
     *   "studentId": "123456",
     *   "name":      "Alex Johnson",
     *   "role":      "STUDENT"
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // -------------------------------------------------------------------------
    // Error handlers
    // -------------------------------------------------------------------------

    @ExceptionHandler(StudentIdAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(StudentIdAlreadyExistsException ex) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new ErrorResponse("STUDENT_ID_TAKEN", ex.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(InvalidCredentialsException ex) {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse("INVALID_CREDENTIALS", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleValidation(IllegalArgumentException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("VALIDATION_ERROR", ex.getMessage()));
    }

    public record ErrorResponse(String code, String message) {}
}
