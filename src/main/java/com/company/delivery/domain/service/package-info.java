package com.company.delivery.domain.service;

/**
 * Domain Services Package.
 *
 * <h2>Purpose</h2>
 * <p>This package contains domain services—stateless operations that don't naturally
 * belong to a single entity or value object. Domain services encapsulate business
 * logic that involves multiple aggregates or external domain concepts.</p>
 *
 * <h2>What Belongs Here</h2>
 * <ul>
 *   <li>Business operations spanning multiple aggregates</li>
 *   <li>Domain calculations requiring external data</li>
 *   <li>Policy implementations (pricing, discounts, routing)</li>
 *   <li>Domain validation requiring multiple entities</li>
 * </ul>
 *
 * <h2>What Does NOT Belong Here</h2>
 * <ul>
 *   <li>Application services/use cases (use application/usecase)</li>
 *   <li>Infrastructure concerns (persistence, HTTP, messaging)</li>
 *   <li>Logic that belongs in an entity</li>
 *   <li>Spring annotations</li>
 * </ul>
 *
 * <h2>Design Rules</h2>
 * <ul>
 *   <li>Domain services are stateless</li>
 *   <li>They operate on domain objects</li>
 *   <li>They express domain concepts in ubiquitous language</li>
 *   <li>They should be pure functions where possible</li>
 * </ul>
 *
 * <h2>When to Use Domain Services</h2>
 * <p>Use a domain service when:</p>
 * <ul>
 *   <li>The operation doesn't conceptually belong to any single entity</li>
 *   <li>The operation requires information from multiple aggregates</li>
 *   <li>The operation represents a domain concept (e.g., "calculate delivery fee")</li>
 * </ul>
 *
 * <h2>Example</h2>
 * <pre>{@code
 * public class DeliveryFeeCalculator {
 *     public Money calculate(Address from, Address to, Weight weight) {
 *         Distance distance = from.distanceTo(to);
 *         // Domain logic for fee calculation
 *     }
 * }
 * }</pre>
 *
 * @see com.company.delivery.domain.model Domain Models
 * @see com.company.delivery.application.usecase Application Use Cases
 */

