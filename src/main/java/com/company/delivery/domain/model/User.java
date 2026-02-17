package com.company.delivery.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a registered user in the delivery system.
 *
 * <p>A User authenticates with their Student ID. The domain enforces:
 * <ul>
 *   <li>Student IDs must be numeric and 5-10 digits</li>
 *   <li>Names cannot be blank</li>
 *   <li>Passwords are stored as BCrypt hashes - never plaintext</li>
 * </ul>
 */
public class User {

    private final UUID id;
    private final String studentId;   // School credential - used to log in
    private final String name;
    private String passwordHash;       // BCrypt hash only - never plaintext
    private final UserRole role;
    private final Instant createdAt;

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

    /** Register a brand-new student user. Password must already be hashed. */
    public static User register(String studentId, String name, String passwordHash) {
        return new User(
            UUID.randomUUID(),
            studentId,
            name,
            passwordHash,
            UserRole.STUDENT,
            Instant.now()
        );
    }

    /** Reconstitute a User from persistence (repository use only). */
    public static User reconstitute(UUID id, String studentId, String name,
                                    String passwordHash, UserRole role, Instant createdAt) {
        return new User(id, studentId, name, passwordHash, role, createdAt);
    }

    // -------------------------------------------------------------------------
    // Validation
    // -------------------------------------------------------------------------

    private static String validateStudentId(String studentId) {
        if (studentId == null || studentId.isBlank()) {
            throw new IllegalArgumentException("Student ID cannot be blank");
        }
        String trimmed = studentId.trim();
        if (!trimmed.matches("\\d{5,10}")) {
            throw new IllegalArgumentException(
                "Student ID must be 5-10 digits (got: '" + trimmed + "')");
        }
        return trimmed;
    }

    private static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
        return name.trim();
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public UUID getId()             { return id; }
    public String getStudentId()    { return studentId; }
    public String getName()         { return name; }
    public String getPasswordHash() { return passwordHash; }
    public UserRole getRole()       { return role; }
    public Instant getCreatedAt()   { return createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User u)) return false;
        return Objects.equals(studentId, u.studentId);
    }

    @Override
    public int hashCode() { return Objects.hash(studentId); }

    @Override
    public String toString() {
        return "User{studentId='" + studentId + "', name='" + name + "', role=" + role + "}";
    }
}
