package com.company.delivery.application.service;

import com.company.delivery.application.dto.AuthDtos.*;
import com.company.delivery.domain.model.User;
import com.company.delivery.domain.repository.UserRepository;
import com.company.delivery.infrastructure.security.JwtService;
import com.company.delivery.infrastructure.security.TotpService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Handles all authentication operations:
 *
 *   register()          - Student self-service sign up
 *   registerStaff()     - Create a staff account (requires staff key)
 *   login()             - Authenticate with student ID + password
 *   verifyTotp()        - Complete login when 2FA is enabled
 *   setupTotp()         - Begin 2FA setup for current user
 *   confirmTotpSetup()  - Complete 2FA setup by confirming first code
 *   disableTotp()       - Remove 2FA from account
 *   forgotPassword()    - Generate a reset token (printed to console - no email server needed for demo)
 *   resetPassword()     - Apply new password using reset token
 */
@Service
public class AuthService {

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService      jwtService;
    private final TotpService     totpService;
    private final String          staffRegistrationKey;
    private final String          appIssuer;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            TotpService totpService,
            @Value("${app.staff-registration-key}") String staffRegistrationKey,
            @Value("${app.name:Campus Delivery}") String appIssuer) {
        this.userRepository       = userRepository;
        this.passwordEncoder      = passwordEncoder;
        this.jwtService           = jwtService;
        this.totpService          = totpService;
        this.staffRegistrationKey = staffRegistrationKey;
        this.appIssuer            = appIssuer;
    }

    // =========================================================================
    // 1. STUDENT REGISTER
    // =========================================================================

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByStudentId(request.studentId()))
            throw new StudentIdAlreadyExistsException(request.studentId());

        User user = User.register(
            request.studentId(),
            request.name(),
            passwordEncoder.encode(request.password())
        );
        userRepository.save(user);

        return new AuthResponse(
            jwtService.generateToken(user),
            user.getStudentId(), user.getName(), user.getRole().name()
        );
    }

    // =========================================================================
    // 2. STAFF REGISTER
    // =========================================================================

    public AuthResponse registerStaff(StaffRegisterRequest request) {
        // Verify the secret staff registration key
        if (!staffRegistrationKey.equals(request.staffRegistrationKey()))
            throw new InvalidStaffKeyException();

        if (userRepository.existsByStudentId(request.studentId()))
            throw new StudentIdAlreadyExistsException(request.studentId());

        User user = User.registerStaff(
            request.studentId(),
            request.name(),
            passwordEncoder.encode(request.password())
        );
        userRepository.save(user);

        return new AuthResponse(
            jwtService.generateToken(user),
            user.getStudentId(), user.getName(), user.getRole().name()
        );
    }

    // =========================================================================
    // 3. LOGIN
    // =========================================================================

    /**
     * Returns either:
     *   AuthResponse        - if 2FA is not enabled (login complete)
     *   TotpChallengeResponse - if 2FA is enabled (client must call verifyTotp next)
     *
     * Returns Object so the controller can handle both cases.
     */
    public Object login(LoginRequest request) {
        User user = userRepository.findByStudentId(request.studentId())
            .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash()))
            throw new InvalidCredentialsException();

        // If 2FA is fully enabled, issue a short-lived pending token
        if (user.isTotpEnabled()) {
            return new TotpChallengeResponse(
                jwtService.generatePending2faToken(user),
                "Enter the 6-digit code from your authenticator app"
            );
        }

        // No 2FA - issue full token immediately
        return new AuthResponse(
            jwtService.generateToken(user),
            user.getStudentId(), user.getName(), user.getRole().name()
        );
    }

    // =========================================================================
    // 4. VERIFY 2FA CODE (second step of login)
    // =========================================================================

    /**
     * Called with the pendingToken from TotpChallengeResponse.
     * userId comes from the JWT filter (already validated the pending token).
     */
    public AuthResponse verifyTotp(String userId, TotpVerifyRequest request) {
        User user = userRepository.findById(UUID.fromString(userId))
            .orElseThrow(InvalidCredentialsException::new);

        if (!user.isTotpEnabled())
            throw new IllegalStateException("2FA is not enabled on this account");

        if (!totpService.verify(user.getTotpSecret(), request.code()))
            throw new InvalidTotpCodeException();

        return new AuthResponse(
            jwtService.generateToken(user),
            user.getStudentId(), user.getName(), user.getRole().name()
        );
    }

    // =========================================================================
    // 5. SETUP 2FA (authenticated - generates secret + QR code)
    // =========================================================================

    public TotpSetupResponse setupTotp(String userId) {
        User user = userRepository.findById(UUID.fromString(userId))
            .orElseThrow(() -> new UserNotFoundException(userId));

        String secret = totpService.generateSecret();
        String qrUri  = totpService.buildQrUri(secret, user.getStudentId(), appIssuer);

        user.enableTotp(secret);
        userRepository.save(user);

        return new TotpSetupResponse(
            secret,
            qrUri,
            "Scan the QR code with your authenticator app, then call /2fa/confirm with your first code"
        );
    }

    // =========================================================================
    // 6. CONFIRM 2FA SETUP (verify first code to complete enrollment)
    // =========================================================================

    public AuthResponse confirmTotpSetup(String userId, TotpVerifyRequest request) {
        User user = userRepository.findById(UUID.fromString(userId))
            .orElseThrow(() -> new UserNotFoundException(userId));

        if (!user.isTotpPending())
            throw new IllegalStateException("No pending 2FA setup found - call /2fa/setup first");

        if (!totpService.verify(user.getTotpSecret(), request.code()))
            throw new InvalidTotpCodeException();

        user.markTotpVerified();
        userRepository.save(user);

        return new AuthResponse(
            jwtService.generateToken(user),
            user.getStudentId(), user.getName(), user.getRole().name()
        );
    }

    // =========================================================================
    // 7. DISABLE 2FA
    // =========================================================================

    public MessageResponse disableTotp(String userId) {
        User user = userRepository.findById(UUID.fromString(userId))
            .orElseThrow(() -> new UserNotFoundException(userId));

        user.disableTotp();
        userRepository.save(user);

        return new MessageResponse("2FA has been disabled");
    }

    // =========================================================================
    // 8. FORGOT PASSWORD
    // =========================================================================

    /**
     * Generates a reset token and prints it to the console.
     * In production: send via email. For demo: read it from the terminal.
     *
     * Always returns success - never reveals whether a student ID exists.
     */
    public MessageResponse forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByStudentId(request.studentId()).ifPresent(user -> {
            String token = UUID.randomUUID().toString().replace("-", "");
            user.setResetToken(token);
            userRepository.save(user);

            // ⚠️ DEMO: print to console instead of sending email
            System.out.println("=".repeat(60));
            System.out.println("PASSWORD RESET TOKEN for student " + request.studentId());
            System.out.println("Token: " + token);
            System.out.println("Expires in: 15 minutes");
            System.out.println("Use at: POST /api/v1/auth/reset-password");
            System.out.println("=".repeat(60));
        });

        // Vague response - don't reveal whether the student ID exists
        return new MessageResponse(
            "If that student ID is registered, a reset token has been printed to the server console"
        );
    }

    // =========================================================================
    // 9. RESET PASSWORD
    // =========================================================================

    public MessageResponse resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetToken(request.token())
            .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));

        user.resetPassword(
            request.token(),
            passwordEncoder.encode(request.newPassword())
        );
        userRepository.save(user);

        return new MessageResponse("Password reset successfully - you can now log in");
    }

    // =========================================================================
    // Exceptions
    // =========================================================================

    public static class StudentIdAlreadyExistsException extends RuntimeException {
        public StudentIdAlreadyExistsException(String id) {
            super("Student ID " + id + " is already registered");
        }
    }

    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException() {
            super("Invalid student ID or password"); // deliberately vague
        }
    }

    public static class InvalidStaffKeyException extends RuntimeException {
        public InvalidStaffKeyException() {
            super("Invalid staff registration key");
        }
    }

    public static class InvalidTotpCodeException extends RuntimeException {
        public InvalidTotpCodeException() {
            super("Invalid or expired 2FA code");
        }
    }

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String id) {
            super("User not found: " + id);
        }
    }
}
