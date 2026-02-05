package com.company.delivery.domain.repository;

import com.company.delivery.domain.model.Order;
import com.company.delivery.domain.valueobject.OrderId;

import java.util.Optional;

/**
 * Repository interface for Order aggregate persistence.
 *
 * <p>This is a <strong>domain port</strong> that defines the contract for order
 * persistence. The actual implementation (adapter) lives in the infrastructure
 * layer and may use JPA, JDBC, or any other persistence technology.</p>
 *
 * <h2>Architectural Notes</h2>
 * <ul>
 *   <li>This interface has NO Spring or JPA dependencies</li>
 *   <li>Implementation is provided by infrastructure layer</li>
 *   <li>Methods use domain types (Order, OrderId), not primitives</li>
 * </ul>
 *
 * <h2>Implementation</h2>
 * <p>The adapter implementing this interface is located at:
 * {@code infrastructure.persistence.OrderPersistenceAdapter}</p>
 *
 * <p><strong>TODO:</strong> Add additional query methods as business requirements emerge.</p>
 *
 * @see com.company.delivery.domain.model.Order
 * @see com.company.delivery.infrastructure.persistence.OrderPersistenceAdapter
 */
public interface OrderRepository {

    /**
     * Persists an order to the data store.
     *
     * <p>If the order already exists, it will be updated.
     * If it's new, it will be created.</p>
     *
     * @param order the order to save
     */
    void save(Order order);

    /**
     * Retrieves an order by its unique identifier.
     *
     * @param id the order identifier
     * @return an Optional containing the order if found, empty otherwise
     */
    Optional<Order> findById(OrderId id);

    /**
     * Removes an order from the data store.
     *
     * @param order the order to delete
     */
    void delete(Order order);

    // ==========================================================================
    // PLACEHOLDER - Additional methods to be added as needed
    // ==========================================================================
    //
    // Future methods may include:
    // - List<Order> findByCustomerId(CustomerId customerId)
    // - List<Order> findByStatus(OrderStatus status)
    // - List<Order> findPendingOrdersOlderThan(Instant cutoff)
    // - boolean existsById(OrderId id)
    // ==========================================================================
}
// add implementations