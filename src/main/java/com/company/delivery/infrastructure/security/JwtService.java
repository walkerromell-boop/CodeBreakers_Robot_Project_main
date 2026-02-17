package com.company.delivery.infrastructure.security;

import com.company.delivery.domain.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

/**
 * Creates and validates JWT tokens.
 *
 * <p>Tokens contain:
 * <ul>
 *   <li>subject  = user's UUID</li>
 *   <li>studentId claim = school student ID</li>
 *   <li>role claim = STUDENT or STAFF</li>
 * </ul>
 *
 * <p>Configure in application.yml:
 * <pre>
 * app:
 *   jwt:
 *     secret: "your-256-bit-secret-key-here-make-it-long"
 *     expiration-ms: 86400000   # 24 hours
 * </pre>
 */
@Service
public class JwtService {

    private final Key signingKey;
    private final long expirationMs;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.signingKey  = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
    }

    // -------------------------------------------------------------------------
    // Token generation
    // -------------------------------------------------------------------------

    /** Generate a signed JWT for a successfully authenticated user. */
    public String generateToken(User user) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
            .setSubject(user.getId().toString())
            .claim("studentId", user.getStudentId())
            .claim("name",      user.getName())
            .claim("role",      user.getRole().name())
            .setIssuedAt(new Date(now))
            .setExpiration(new Date(now + expirationMs))
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact();
    }

    // -------------------------------------------------------------------------
    // Token validation & extraction
    // -------------------------------------------------------------------------

    /** Returns true if the token is valid (correctly signed + not expired). */
    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractUserId(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractStudentId(String token) {
        return parseClaims(token).get("studentId", String.class);
    }

    public String extractRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(signingKey)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}
