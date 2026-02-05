package com.company.delivery.domain.valueobject;

/**
 * Domain Value Objects Package.
 *
 * <h2>Purpose</h2>
 * <p>This package contains immutable value objects that describe characteristics
 * of domain concepts. Value objects have no identity—they are defined entirely
 * by their attributes.</p>
 *
 * <h2>What Belongs Here</h2>
 * <ul>
 *   <li>Identifiers (OrderId, CustomerId, DeliveryId)</li>
 *   <li>Measurements (Money, Distance, Weight)</li>
 *   <li>Descriptors (Address, GeoLocation, DateRange)</li>
 *   <li>Enumerations (OrderStatus, DeliveryStatus)</li>
 * </ul>
 *
 * <h2>What Does NOT Belong Here</h2>
 * <ul>
 *   <li>Objects with identity (use domain/model instead)</li>
 *   <li>Mutable objects</li>
 *   <li>Framework annotations</li>
 *   <li>Persistence logic</li>
 * </ul>
 *
 * <h2>Design Rules</h2>
 * <ul>
 *   <li>Value objects MUST be immutable</li>
 *   <li>Equality is based on all attributes</li>
 *   <li>Should be self-validating (throw on invalid state)</li>
 *   <li>Prefer records for simple value objects (Java 17+)</li>
 * </ul>
 *
 * <h2>Example</h2>
 * <pre>{@code
 * public record OrderId(UUID value) {
 *     public OrderId {
 *         Objects.requireNonNull(value, "OrderId cannot be null");
 *     }
 *
 *     public static OrderId generate() {
 *         return new OrderId(UUID.randomUUID());
 *     }
 * }
 * }</pre>
 *
 * @see com.company.delivery.domain.model Domain Models
 */

