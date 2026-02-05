package com.company.delivery.domain.model;

/**
 * Represents the lifecycle status of an order.
 *
 * <p>This enum defines all possible states an order can be in and enforces
 * valid state transitions through the Order aggregate's business methods.</p>
 *
 * <h2>Status Flow</h2>
 * <pre>
 * PENDING → CONFIRMED → DISPATCHED → DELIVERED
 *    ↓          ↓           ↓
 * CANCELLED  CANCELLED  CANCELLED
 * </pre>
 *
 * <h2>Business Rules</h2>
 * <ul>
 *   <li>All orders start as PENDING</li>
 *   <li>Orders can be cancelled at any time before DELIVERED</li>
 *   <li>DELIVERED orders cannot be cancelled</li>
 *   <li>CANCELLED orders cannot transition to any other state</li>
 * </ul>
 */
public enum OrderStatus {
    
    /**
     * Order has been created but not yet confirmed.
     * Items can still be added or removed.
     */
    PENDING("Pending", "Order is being prepared"),
    
    /**
     * Order has been confirmed and is ready for preparation.
     * Items can no longer be modified.
     */
    CONFIRMED("Confirmed", "Order confirmed and being prepared"),
    
    /**
     * Order has been dispatched for delivery.
     */
    DISPATCHED("Dispatched", "Order is out for delivery"),
    
    /**
     * Order has been successfully delivered to the customer.
     */
    DELIVERED("Delivered", "Order has been delivered"),
    
    /**
     * Order has been cancelled and will not be processed.
     */
    CANCELLED("Cancelled", "Order has been cancelled");

    private final String displayName;
    private final String description;

    OrderStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Returns the user-friendly display name for this status.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns a description of what this status means.
     *
     * @return the status description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks if an order in this status can be modified.
     *
     * @return true if items can be added/removed, false otherwise
     */
    public boolean canBeModified() {
        return this == PENDING;
    }

    /**
     * Checks if an order in this status can be cancelled.
     *
     * @return true if the order can be cancelled, false otherwise
     */
    public boolean canBeCancelled() {
        return this != DELIVERED && this != CANCELLED;
    }

    /**
     * Checks if this is a terminal status (no further transitions possible).
     *
     * @return true if this is a final state, false otherwise
     */
    public boolean isTerminal() {
        return this == DELIVERED || this == CANCELLED;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
