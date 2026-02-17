package com.company.delivery.domain.repository;

import com.company.delivery.domain.model.Order;
import com.company.delivery.domain.model.OrderStatus;
import com.company.delivery.domain.valueobject.OrderId;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Order aggregate persistence.
 */
public interface OrderRepository {

    void save(Order order);

    Optional<Order> findById(OrderId id);

    void delete(Order order);

    /** Returns ALL orders */
    List<Order> findAll();

    /** Returns orders with a specific status */
    List<Order> findByStatus(OrderStatus status);

    /** Returns orders that are not yet DELIVERED or CANCELLED */
    List<Order> findActive();

    /** Returns DELIVERED and CANCELLED orders */
    List<Order> findHistory();
}
