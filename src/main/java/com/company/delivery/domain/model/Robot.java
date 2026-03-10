package com.company.delivery.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a delivery robot in the system.
 *
 * <p>Business Rules:
 * <ul>
 *   <li>Battery level must be between 0-100%</li>
 *   <li>Cannot deploy if battery is below 20%</li>
 *   <li>Robot must be IDLE to be deployed</li>
 *   <li>Robot returns to IDLE after delivery is completed</li>
 *   <li>Battery drains during deployment</li>
 * </ul>
 */
public class Robot {

    private final UUID id;
    private final String name;           // e.g., "Robot-001", "Delivery Bot Alpha"
    private int batteryPercentage;       // 0-100
    private RobotStatus status;
    private UUID assignedOrderId;        // null when IDLE
    private String currentLocation;      // Current GPS/address
    private String targetLocation;       // Delivery destination
    private Instant lastUpdated;

    private Robot(UUID id, String name, int batteryPercentage, RobotStatus status,
                  UUID assignedOrderId, String currentLocation, String targetLocation,
                  Instant lastUpdated) {
        this.id                = Objects.requireNonNull(id);
        this.name              = validateName(name);
        this.batteryPercentage = validateBattery(batteryPercentage);
        this.status            = Objects.requireNonNull(status);
        this.assignedOrderId   = assignedOrderId;
        this.currentLocation   = currentLocation;
        this.targetLocation    = targetLocation;
        this.lastUpdated       = Objects.requireNonNull(lastUpdated);
    }

    // -------------------------------------------------------------------------
    // Factory methods
    // -------------------------------------------------------------------------

    /** Create a new robot at full charge */
    public static Robot create(String name, String initialLocation) {
        return new Robot(
            UUID.randomUUID(),
            name,
            100,  // Start at full battery
            RobotStatus.IDLE,
            null,
            initialLocation,
            null,
            Instant.now()
        );
    }

    /** Reconstitute from persistence */
    public static Robot reconstitute(UUID id, String name, int batteryPercentage,
                                     RobotStatus status, UUID assignedOrderId,
                                     String currentLocation, String targetLocation,
                                     Instant lastUpdated) {
        return new Robot(id, name, batteryPercentage, status, assignedOrderId,
                        currentLocation, targetLocation, lastUpdated);
    }

    // -------------------------------------------------------------------------
    // Business Methods
    // -------------------------------------------------------------------------

    /**
     * Deploy the robot to deliver an order.
     *
     * @param orderId the order to deliver
     * @param deliveryAddress where to deliver
     * @throws IllegalStateException if robot cannot be deployed
     */
    public void deploy(UUID orderId, String deliveryAddress) {
        if (status != RobotStatus.IDLE) {
            throw new IllegalStateException(
                String.format("Robot %s is not idle - current status: %s", name, status)
            );
        }

        if (batteryPercentage < 20) {
            throw new IllegalStateException(
                String.format("Robot %s battery too low: %d%% (minimum 20%% required)",
                    name, batteryPercentage)
            );
        }

        this.assignedOrderId  = Objects.requireNonNull(orderId, "Order ID required");
        this.targetLocation   = Objects.requireNonNull(deliveryAddress, "Delivery address required");
        this.status           = RobotStatus.EN_ROUTE;
        this.lastUpdated      = Instant.now();
    }

    /**
     * Mark the robot as having arrived at the delivery location.
     */
    public void arriveAtDestination() {
        if (status != RobotStatus.EN_ROUTE) {
            throw new IllegalStateException(
                String.format("Robot %s is not en route - current status: %s", name, status)
            );
        }

        this.status       = RobotStatus.DELIVERING;
        this.lastUpdated  = Instant.now();
        drainBattery(5);  // Delivery process uses 5% battery
    }

    /**
     * Complete the delivery and return robot to base.
     */
    public void completeDelivery() {
        if (status != RobotStatus.DELIVERING) {
            throw new IllegalStateException(
                String.format("Robot %s is not delivering - current status: %s", name, status)
            );
        }

        this.status          = RobotStatus.RETURNING;
        this.targetLocation  = currentLocation;  // Return to base
        this.lastUpdated     = Instant.now();
    }

    /**
     * Mark robot as returned to base and ready for next delivery.
     */
    public void returnToBase(String baseLocation) {
        if (status != RobotStatus.RETURNING) {
            throw new IllegalStateException(
                String.format("Robot %s is not returning - current status: %s", name, status)
            );
        }

        this.status           = RobotStatus.IDLE;
        this.currentLocation  = baseLocation;
        this.targetLocation   = null;
        this.assignedOrderId  = null;
        this.lastUpdated      = Instant.now();
        drainBattery(10);  // Return trip uses 10% battery
    }

    /**
     * Send robot to charging station.
     */
    public void charge() {
        if (status != RobotStatus.IDLE) {
            throw new IllegalStateException(
                String.format("Robot %s must be idle to charge - current status: %s", name, status)
            );
        }

        this.status      = RobotStatus.CHARGING;
        this.lastUpdated = Instant.now();
    }

    /**
     * Update battery level during charging.
     * @param amount percentage to add (0-100)
     */
    public void addCharge(int amount) {
        if (status != RobotStatus.CHARGING) {
            throw new IllegalStateException("Robot must be in CHARGING status");
        }

        this.batteryPercentage = Math.min(100, batteryPercentage + amount);
        this.lastUpdated       = Instant.now();

        // Automatically return to IDLE when fully charged
        if (batteryPercentage == 100) {
            this.status = RobotStatus.IDLE;
        }
    }

    /**
     * Drain battery during operation.
     * @param amount percentage to drain
     */
    private void drainBattery(int amount) {
        this.batteryPercentage = Math.max(0, batteryPercentage - amount);
        this.lastUpdated       = Instant.now();

        if (batteryPercentage == 0) {
            this.status = RobotStatus.OUT_OF_BATTERY;
        }
    }

    /**
     * Update robot's current GPS location.
     */
    public void updateLocation(String newLocation) {
        this.currentLocation = newLocation;
        this.lastUpdated     = Instant.now();
    }

    /**
     * Check if robot is available for deployment.
     */
    public boolean isAvailableForDeployment() {
        return status == RobotStatus.IDLE && batteryPercentage >= 20;
    }

    // -------------------------------------------------------------------------
    // Validation
    // -------------------------------------------------------------------------

    private String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Robot name cannot be blank");
        }
        return name.trim();
    }

    private int validateBattery(int battery) {
        if (battery < 0 || battery > 100) {
            throw new IllegalArgumentException(
                String.format("Battery must be 0-100, got: %d", battery)
            );
        }
        return battery;
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public UUID getId()                 { return id; }
    public String getName()             { return name; }
    public int getBatteryPercentage()   { return batteryPercentage; }
    public RobotStatus getStatus()      { return status; }
    public UUID getAssignedOrderId()    { return assignedOrderId; }
    public String getCurrentLocation()  { return currentLocation; }
    public String getTargetLocation()   { return targetLocation; }
    public Instant getLastUpdated()     { return lastUpdated; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Robot r)) return false;
        return Objects.equals(id, r.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return String.format("Robot{name='%s', battery=%d%%, status=%s, order=%s}",
            name, batteryPercentage, status, assignedOrderId);
    }
}
