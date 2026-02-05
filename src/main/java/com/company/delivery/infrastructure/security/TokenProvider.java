package com.company.delivery.infrastructure.security;

/**
 * Interface for JWT token operations.
 *
 * <p>This interface defines the contract for JWT token generation and validation.
 * The implementation handles all JWT library specifics.</p>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *   <li>Generate access tokens</li>
 *   <li>Generate refresh tokens</li>
 *   <li>Validate token signatures</li>
 *   <li>Extract claims from tokens</li>
 * </ul>
 *
 * <h2>Security Considerations</h2>
 * <ul>
 *   <li>Secret keys must come from environment variables</li>
 *   <li>Tokens should have appropriate expiration times</li>
 *   <li>Refresh tokens should be stored securely</li>
 * </ul>
 *
 * <p><strong>TODO:</strong> Implement when JWT requirements are defined.</p>
 */
public interface TokenProvider {

    // ==========================================================================
    // PLACEHOLDER - No methods yet
    // ==========================================================================
    //
    // Future methods may include:
    //
    // /**
    //  * Generates an access token for the given user.
    //  * @param userId the user identifier
    //  * @param roles the user's roles
    //  * @return the generated JWT access token
    //  */
    // String generateAccessToken(String userId, List<String> roles);
    //
    // /**
    //  * Generates a refresh token for the given user.
    //  * @param userId the user identifier
    //  * @return the generated refresh token
    //  */
    // String generateRefreshToken(String userId);
    //
    // /**
    //  * Validates a token and returns the claims if valid.
    //  * @param token the JWT token to validate
    //  * @return the token claims
    //  * @throws InvalidTokenException if the token is invalid
    //  */
    // TokenClaims validateToken(String token);
    //
    // /**
    //  * Extracts the user ID from a token without full validation.
    //  * @param token the JWT token
    //  * @return the user ID
    //  */
    // String extractUserId(String token);
    // ==========================================================================

}
