package com.company.delivery.domain.model;

/**
 * Roles a user can hold in the system.
 */
public enum UserRole {
    STUDENT,  // Can place and manage their own orders
    STAFF     // Can view all orders, dispatch, deliver
}
