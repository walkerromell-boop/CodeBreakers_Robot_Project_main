package com.company.delivery.application.service;

import com.company.delivery.application.dto.AuthDtos.AuthResponse;
import com.company.delivery.application.dto.AuthDtos.LoginRequest;
import com.company.delivery.application.dto.AuthDtos.RegisterRequest;
import com.company.delivery.domain.model.User;
import com.company.delivery.domain.repository.UserRepository;
import com.company.delivery.infrastructure.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Handles user registration and login.
 *
 * <p>Registration flow:
 * <ol>
 *   <li>Check student ID not already taken</li>
 *   <li>Hash the password with BCrypt</li>
 *   <li>Create User domain object</li>
 *   <li>Save to repository</li>
 *   <li>Return JWT token</li>
 * </ol>
 *
 * <p>Login flow:
 * <ol>
 *   <li>Find user by student ID</li>
 *   <li>Verify BCrypt password matches</li>
 *   <li>Return JWT token</li>
 * </ol>
 */
@Service
public class AuthService {

    private final UserRepository   userRepository;
    private final PasswordEncoder  passwordEncoder;
    private final JwtService       jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository  = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService      = jwtService;
    }

    // -------------------------------------------------------------------------
    // Register
    // -------------------------------------------------------------------------

    /**
     * Registers a new student and returns a JWT so they're immediately logged in.
     *
     * @throws StudentIdAlreadyExistsException if the student ID is taken
     */
    public AuthResponse register(RegisterRequest request) {

        // 1. Check student ID is not already registered
        if (userRepository.existsByStudentId(request.studentId())) {
            throw new StudentIdAlreadyExistsException(request.studentId());
        }

        // 2. Hash the password - never store plaintext
        String passwordHash = passwordEncoder.encode(request.password());

        // 3. Create the User - domain validates student ID format + name
        User user = User.register(
            request.studentId(),
            request.name(),
            passwordHash
        );

        // 4. Persist
        userRepository.save(user);

        // 5. Return a token so they're logged in straight away
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getStudentId(), user.getName(), user.getRole().name());
    }

    // -------------------------------------------------------------------------
    // Login
    // -------------------------------------------------------------------------

    /**
     * Authenticates a student and returns a JWT.
     *
     * @throws InvalidCredentialsException if student ID not found or password wrong
     */
    public AuthResponse login(LoginRequest request) {

        // 1. Look up by student ID
        User user = userRepository.findByStudentId(request.studentId())
            .orElseThrow(InvalidCredentialsException::new);

        // 2. Verify password matches the stored BCrypt hash
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        // 3. Issue JWT
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getStudentId(), user.getName(), user.getRole().name());
    }

    // -------------------------------------------------------------------------
    // Exceptions
    // -------------------------------------------------------------------------

    public static class StudentIdAlreadyExistsException extends RuntimeException {
        public StudentIdAlreadyExistsException(String studentId) {
            super("Student ID " + studentId + " is already registered");
        }
    }

    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException() {
            // Intentionally vague - don't reveal whether ID or password was wrong
            super("Invalid student ID or password");
        }
    }
}
