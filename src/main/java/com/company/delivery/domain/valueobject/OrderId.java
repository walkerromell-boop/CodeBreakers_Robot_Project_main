package com.company.delivery.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for an Order aggregate.
 *
 * <p>This value object wraps the underlying identifier type, providing type safety
 * and preventing primitive obsession. Using a dedicated type prevents accidentally
 * passing a CustomerId where an OrderId is expected.</p>
 *
 * <h2>Design Decisions</h2>
 * <ul>
 *   <li>Uses UUID for global uniqueness without coordination</li>
 *   <li>Immutable - once created, cannot be changed</li>
 *   <li>Self-validating - rejects null values</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * OrderId orderId = OrderId.generate();
 * OrderId existing = OrderId.of(uuid);
 * }</pre>
 *
 * @see com.company.delivery.domain.model.Order
 */
public record OrderId(UUID value) {

    /**
     * Creates an OrderId with validation.
     *
     * @param value the UUID value
     * @throws NullPointerException if value is null
     */
    public OrderId {
        Objects.requireNonNull(value, "OrderId value cannot be null");
    }

    /**
     * Generates a new random OrderId.
     *
     * @return a new OrderId with a random UUID
     */
    public static OrderId generate() {
        return new OrderId(UUID.randomUUID());
    }

    /**
     * Creates an OrderId from an existing UUID.
     *
     * @param uuid the UUID value
     * @return an OrderId wrapping the UUID
     * @throws NullPointerException if uuid is null
     */
    public static OrderId of(UUID uuid) {
        return new OrderId(uuid);
    }

    /**
     * Creates an OrderId from a string representation.
     *
     * @param value the string UUID value
     * @return an OrderId parsed from the string
     * @throws IllegalArgumentException if the string is not a valid UUID
     * @throws NullPointerException if value is null
     */
    public static OrderId fromString(String value) {
        Objects.requireNonNull(value, "OrderId string value cannot be null");
        return new OrderId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
