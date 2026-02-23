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
 * Two token types:
 *   1. Full auth token  - grants access to all endpoints the role allows
 *   2. Pending 2FA token - only grants access to POST /auth/2fa/verify (5 min)
 *
 * Configure in application.yml:
 *   app.jwt.secret          - at least 32 characters
 *   app.jwt.expiration-ms   - full token lifetime (e.g. 86400000 = 24 hours)
 */
@Service
public class JwtService {

    private static final String CLAIM_STUDENT_ID = "studentId";
    private static final String CLAIM_ROLE       = "role";
    private static final String CLAIM_TYPE       = "type";
    private static final String TYPE_FULL        = "FULL";
    private static final String TYPE_PENDING_2FA = "PENDING_2FA";

    private final Key  signingKey;
    private final long expirationMs;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.signingKey   = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
    }

    // -------------------------------------------------------------------------
    // Token generation
    // -------------------------------------------------------------------------

    /** Full access token - issued after successful login (and 2FA if enabled) */
    public String generateToken(User user) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
            .setSubject(user.getId().toString())
            .claim(CLAIM_STUDENT_ID, user.getStudentId())
            .claim(CLAIM_ROLE,       user.getRole().name())
            .claim(CLAIM_TYPE,       TYPE_FULL)
            .setIssuedAt(new Date(now))
            .setExpiration(new Date(now + expirationMs))
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact();
    }

    /**
     * Short-lived "pending 2FA" token.
     * Issued when login credentials are correct but 2FA code still needed.
     * Only valid for 5 minutes and only usable at the /2fa/verify endpoint.
     */
    public String generatePending2faToken(User user) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
            .setSubject(user.getId().toString())
            .claim(CLAIM_STUDENT_ID, user.getStudentId())
            .claim(CLAIM_ROLE,       user.getRole().name())
            .claim(CLAIM_TYPE,       TYPE_PENDING_2FA)
            .setIssuedAt(new Date(now))
            .setExpiration(new Date(now + 5 * 60 * 1000L)) // 5 minutes
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact();
    }

    // -------------------------------------------------------------------------
    // Validation & extraction
    // -------------------------------------------------------------------------

    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /** A full token is valid AND has type FULL (not pending 2FA) */
    public boolean isFullToken(String token) {
        try {
            return TYPE_FULL.equals(parseClaims(token).get(CLAIM_TYPE, String.class));
        } catch (Exception e) {
            return false;
        }
    }

    /** A pending 2FA token - only usable at the verify endpoint */
    public boolean isPending2faToken(String token) {
        try {
            return TYPE_PENDING_2FA.equals(parseClaims(token).get(CLAIM_TYPE, String.class));
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUserId(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractStudentId(String token) {
        return parseClaims(token).get(CLAIM_STUDENT_ID, String.class);
    }

    public String extractRole(String token) {
        return parseClaims(token).get(CLAIM_ROLE, String.class);
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
