package com.company.delivery.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a registered user in the delivery system.
 *
 * Business rules:
 *   - Student IDs must be 5-10 digits
 *   - Passwords are BCrypt hashed - never plaintext
 *   - Staff accounts can only be created via registerStaff() - not self-service
 *   - Password reset tokens expire after 15 minutes
 *   - 2FA is optional - enabled when totpSecret is set and verified
 */
public class User {

    private final UUID     id;
    private final String   studentId;
    private final String   name;
    private       String   passwordHash;
    private final UserRole role;
    private final Instant  createdAt;

    // Account recovery
    private String  resetToken;
    private Instant resetTokenExpiresAt;

    // 2FA (TOTP - Google Authenticator style)
    private String  totpSecret;    // null = 2FA not set up
    private boolean totpVerified;  // true = user confirmed setup with first code

    private User(UUID id, String studentId, String name,
                 String passwordHash, UserRole role, Instant createdAt) {
        this.id           = Objects.requireNonNull(id);
        this.studentId    = validateStudentId(studentId);
        this.name         = validateName(name);
        this.passwordHash = Objects.requireNonNull(passwordHash, "Password hash required");
        this.role         = Objects.requireNonNull(role);
        this.createdAt    = Objects.requireNonNull(createdAt);
    }

    // -------------------------------------------------------------------------
    // Factory methods
    // -------------------------------------------------------------------------

    /** Self-service registration - always creates a STUDENT */
    public static User register(String studentId, String name, String passwordHash) {
        return new User(UUID.randomUUID(), studentId, name,
                        passwordHash, UserRole.STUDENT, Instant.now());
    }

    /** Admin-only - creates a STAFF account */
    public static User registerStaff(String studentId, String name, String passwordHash) {
        return new User(UUID.randomUUID(), studentId, name,
                        passwordHash, UserRole.STAFF, Instant.now());
    }

    /** Reconstitute from persistence (repository use only) */
    public static User reconstitute(UUID id, String studentId, String name,
                                    String passwordHash, UserRole role, Instant createdAt,
                                    String resetToken, Instant resetTokenExpiresAt,
                                    String totpSecret, boolean totpVerified) {
        User user = new User(id, studentId, name, passwordHash, role, createdAt);
        user.resetToken          = resetToken;
        user.resetTokenExpiresAt = resetTokenExpiresAt;
        user.totpSecret          = totpSecret;
        user.totpVerified        = totpVerified;
        return user;
    }

    // -------------------------------------------------------------------------
    // Password reset
    // -------------------------------------------------------------------------

    /** Store a hashed reset token that expires in 15 minutes */
    public void setResetToken(String token) {
        this.resetToken          = Objects.requireNonNull(token);
        this.resetTokenExpiresAt = Instant.now().plusSeconds(15 * 60);
    }

    /** Apply a new password if the token is valid and not expired */
    public void resetPassword(String token, String newPasswordHash) {
        if (resetToken == null || !resetToken.equals(token))
            throw new IllegalArgumentException("Invalid reset token");
        if (Instant.now().isAfter(resetTokenExpiresAt))
            throw new IllegalStateException("Reset token has expired - request a new one");
        this.passwordHash        = Objects.requireNonNull(newPasswordHash);
        this.resetToken          = null;   // single-use
        this.resetTokenExpiresAt = null;
    }

    public boolean hasValidResetToken(String token) {
        return resetToken != null
            && resetToken.equals(token)
            && Instant.now().isBefore(resetTokenExpiresAt);
    }

    // -------------------------------------------------------------------------
    // 2FA (TOTP)
    // -------------------------------------------------------------------------

    /** Begin 2FA setup - stores secret, marks as unverified until first code confirmed */
    public void enableTotp(String secret) {
        this.totpSecret  = Objects.requireNonNull(secret);
        this.totpVerified = false;
    }

    /** Call after user enters their first successful code - completes setup */
    public void markTotpVerified() {
        if (totpSecret == null)
            throw new IllegalStateException("TOTP secret not set - call enableTotp first");
        this.totpVerified = true;
    }

    public void disableTotp() {
        this.totpSecret   = null;
        this.totpVerified = false;
    }

    /** 2FA is active and confirmed */
    public boolean isTotpEnabled() { return totpSecret != null && totpVerified; }

    /** 2FA secret generated but user hasn't confirmed first code yet */
    public boolean isTotpPending() { return totpSecret != null && !totpVerified; }

    // -------------------------------------------------------------------------
    // Validation
    // -------------------------------------------------------------------------

    private static String validateStudentId(String studentId) {
        if (studentId == null || studentId.isBlank())
            throw new IllegalArgumentException("Student ID cannot be blank");
        String trimmed = studentId.trim();
        if (!trimmed.matches("\\d{5,10}"))
            throw new IllegalArgumentException(
                "Student ID must be 5-10 digits (got: '" + trimmed + "')");
        return trimmed;
    }

    private static String validateName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be blank");
        return name.trim();
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public UUID     getId()                  { return id; }
    public String   getStudentId()           { return studentId; }
    public String   getName()                { return name; }
    public String   getPasswordHash()        { return passwordHash; }
    public UserRole getRole()                { return role; }
    public Instant  getCreatedAt()           { return createdAt; }
    public String   getResetToken()          { return resetToken; }
    public Instant  getResetTokenExpiresAt() { return resetTokenExpiresAt; }
    public String   getTotpSecret()          { return totpSecret; }
    public boolean  isTotpVerified()         { return totpVerified; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User u)) return false;
        return Objects.equals(studentId, u.studentId);
    }

    @Override public int hashCode() { return Objects.hash(studentId); }

    @Override
    public String toString() {
        return "User{studentId='" + studentId + "', name='" + name
             + "', role=" + role + ", 2fa=" + isTotpEnabled() + "}";
    }
}
