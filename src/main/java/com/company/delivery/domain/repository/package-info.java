package com.company.delivery.domain.repository;

/**
 * Domain Repository Interfaces Package - Domain Ports.
 *
 * <h2>Purpose</h2>
 * <p>This package contains repository interfaces that define contracts for
 * persisting and retrieving domain objects. These are <strong>domain ports</strong>
 * in hexagonal architecture terminology.</p>
 *
 * <h2>What Belongs Here</h2>
 * <ul>
 *   <li>Repository interfaces for aggregate roots</li>
 *   <li>Query method signatures</li>
 *   <li>Domain-centric method names (not SQL-centric)</li>
 * </ul>
 *
 * <h2>What Does NOT Belong Here</h2>
 * <ul>
 *   <li>Implementation classes (use infrastructure/persistence)</li>
 *   <li>Spring Data annotations (@Repository)</li>
 *   <li>JPA-specific methods</li>
 *   <li>SQL or database concepts</li>
 * </ul>
 *
 * <h2>Design Rules</h2>
 * <ul>
 *   <li>One repository per aggregate root</li>
 *   <li>Methods use domain types, not primitives</li>
 *   <li>Use Optional for queries that may return nothing</li>
 *   <li>Method names reflect domain language</li>
 * </ul>
 *
 * <h2>Example</h2>
 * <pre>{@code
 * public interface OrderRepository {
 *     void save(Order order);
 *     Optional<Order> findById(OrderId id);
 *     List<Order> findByCustomer(CustomerId customerId);
 *     void delete(Order order);
 * }
 * }</pre>
 *
 * @see com.company.delivery.infrastructure.persistence Persistence Adapters
 */

