package com.company.delivery.infrastructure.persistence;

/**
 * Persistence Adapters Package - Database Access.
 *
 * <h2>Purpose</h2>
 * <p>This package contains persistence adapters that implement repository ports
 * defined in the domain layer. Adapters handle all database-specific logic
 * including JPA entities, queries, and mapping.</p>
 *
 * <h2>What Belongs Here</h2>
 * <ul>
 *   <li>Repository adapter implementations</li>
 *   <li>JPA entity classes</li>
 *   <li>Spring Data JPA repository interfaces</li>
 *   <li>Entity ↔ Domain mappers</li>
 *   <li>Custom query implementations</li>
 * </ul>
 *
 * <h2>What Does NOT Belong Here</h2>
 * <ul>
 *   <li>Domain models (use domain/model)</li>
 *   <li>Business logic</li>
 *   <li>Use case orchestration</li>
 *   <li>HTTP handling</li>
 * </ul>
 *
 * <h2>Sub-Package Organization</h2>
 * <pre>
 * persistence/
 * ├── entity/          # JPA entities
 * ├── repository/      # Spring Data JPA interfaces
 * ├── mapper/          # Entity ↔ Domain mappers
 * └── *Adapter.java    # Port implementations
 * </pre>
 *
 * <h2>Key Principle: Separate JPA from Domain</h2>
 * <p>JPA entities are NOT the same as domain models. The adapter maps between them:</p>
 * <pre>{@code
 * // Domain model (domain/model)
 * public class Order {
 *     private final OrderId id;
 *     // Pure domain, no JPA
 * }
 *
 * // JPA entity (infrastructure/persistence/entity)
 * @Entity
 * @Table(name = "orders")
 * public class OrderJpaEntity {
 *     @Id
 *     private UUID id;
 *     // JPA annotations allowed
 * }
 * }</pre>
 *
 * <h2>Example Adapter</h2>
 * <pre>{@code
 * @Repository
 * public class OrderPersistenceAdapter implements OrderRepository {
 *
 *     private final OrderJpaRepository jpaRepository;
 *     private final OrderMapper mapper;
 *
 *     @Override
 *     public void save(Order order) {
 *         OrderJpaEntity entity = mapper.toJpaEntity(order);
 *         jpaRepository.save(entity);
 *     }
 *
 *     @Override
 *     public Optional<Order> findById(OrderId id) {
 *         return jpaRepository.findById(id.value())
 *             .map(mapper::toDomain);
 *     }
 * }
 * }</pre>
 *
 * @see com.company.delivery.domain.repository Domain Repository Ports
 */

