package com.company.delivery.infrastructure.security;

/**
 * Interface for authentication operations.
 *
 * <p>This port defines the contract for authentication-related operations.
 * It allows the application layer to interact with security concerns without
 * depending on Spring Security directly.</p>
 *
 * <h2>Architectural Notes</h2>
 * <ul>
 *   <li>This interface can be considered an output port</li>
 *   <li>Implemented by security adapters in this package</li>
 *   <li>Allows use cases to check authentication/authorization</li>
 * </ul>
 *
 * <p><strong>TODO:</strong> Define authentication methods when security requirements are finalized.</p>
 */
public interface AuthenticationPort {

    // ==========================================================================
    // PLACEHOLDER - No methods yet
    // ==========================================================================
    //
    // Future methods may include:
    //
    // /**
    //  * Returns the currently authenticated user.
    //  * @return the authenticated user details
    //  * @throws NotAuthenticatedException if no user is authenticated
    //  */
    // AuthenticatedUser getCurrentUser();
    //
    // /**
    //  * Checks if the current user has a specific permission.
    //  * @param permission the permission to check
    //  * @return true if the user has the permission
    //  */
    // boolean hasPermission(String permission);
    //
    // /**
    //  * Checks if the current user has a specific role.
    //  * @param role the role to check
    //  * @return true if the user has the role
    //  */
    // boolean hasRole(String role);
    // ==========================================================================

}
