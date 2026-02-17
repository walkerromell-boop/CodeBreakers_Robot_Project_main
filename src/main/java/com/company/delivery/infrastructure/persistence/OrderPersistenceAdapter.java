package com.company.delivery.infrastructure.persistence;

import com.company.delivery.domain.model.Order;
import com.company.delivery.domain.model.OrderStatus;
import com.company.delivery.domain.repository.OrderRepository;
import com.company.delivery.domain.valueobject.OrderId;

import java.util.List;
import java.util.Optional;

/**
 * Persistence adapter for the Order aggregate.
 *
 * <p>This adapter implements the {@link OrderRepository} domain port using JPA
 * as the underlying persistence technology. It maps between domain models and
 * JPA entities.</p>
 *
 * <p><strong>TODO:</strong> Implement persistence logic when JPA entities are defined.</p>
 *
 * @see OrderRepository
 */
// @Repository
public class OrderPersistenceAdapter implements OrderRepository {

    // ==========================================================================
    // PLACEHOLDER - Dependencies to be injected
    // ==========================================================================
    //
    // private final OrderJpaRepository jpaRepository;
    // private final OrderMapper mapper;
    //
    // public OrderPersistenceAdapter(OrderJpaRepository jpaRepository,
    //                                 OrderMapper mapper) {
    //     this.jpaRepository = jpaRepository;
    //     this.mapper = mapper;
    // }
    // ==========================================================================

    @Override
    public void save(Order order) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void delete(Order order) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public List<Order> findAll() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public List<Order> findActive() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public List<Order> findHistory() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
