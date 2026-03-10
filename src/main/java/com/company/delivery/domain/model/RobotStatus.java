package com.company.delivery.domain.model;

/**
 * Possible states for a delivery robot.
 */
public enum RobotStatus {
    IDLE,              // Ready for deployment
    EN_ROUTE,          // Traveling to delivery location
    DELIVERING,        // At destination, handing off order
    RETURNING,         // Returning to base
    CHARGING,          // At charging station
    OUT_OF_BATTERY,    // Battery depleted, needs charging
    MAINTENANCE        // Under repair/maintenance
}
