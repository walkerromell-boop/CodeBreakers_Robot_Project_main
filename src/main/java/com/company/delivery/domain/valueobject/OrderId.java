package com.company.delivery.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a unique Order identifier.
 */
public record OrderId(UUID value) {

    public OrderId {
        Objects.requireNonNull(value, "OrderId value cannot be null");
    }

    /** Generate a brand-new random OrderId */
    public static OrderId generate() {
        return new OrderId(UUID.randomUUID());
    }

    /** Wrap an existing UUID (used by use cases when looking up by ID) */
    public static OrderId of(UUID uuid) {
        return new OrderId(uuid);
    }

    /** Wrap a UUID string */
    public static OrderId of(String uuid) {
        return new OrderId(UUID.fromString(uuid));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
