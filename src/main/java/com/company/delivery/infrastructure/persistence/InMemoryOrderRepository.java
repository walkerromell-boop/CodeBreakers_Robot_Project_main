package com.company.delivery.infrastructure.persistence;

import com.company.delivery.domain.model.Order;
import com.company.delivery.domain.model.OrderStatus;
import com.company.delivery.domain.repository.OrderRepository;
import com.company.delivery.domain.valueobject.OrderId;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of OrderRepository.
 *
 * <p>Uses a ConcurrentHashMap to store orders in memory.
 * This is suitable for development and demos.
 * Replace with a real JPA implementation for production.</p>
 */
@Repository
public class InMemoryOrderRepository implements OrderRepository {

    private final Map<OrderId, Order> store = new ConcurrentHashMap<>();

    @Override
    public void save(Order order) {
        store.put(order.getId(), order);
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void delete(Order order) {
        store.remove(order.getId());
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return store.values().stream()
            .filter(order -> order.getStatus() == status)
            .collect(Collectors.toList());
    }

    @Override
    public List<Order> findActive() {
        return store.values().stream()
            .filter(order -> order.getStatus() != OrderStatus.DELIVERED
                         && order.getStatus() != OrderStatus.CANCELLED)
            .collect(Collectors.toList());
    }

    @Override
    public List<Order> findHistory() {
        return store.values().stream()
            .filter(order -> order.getStatus() == OrderStatus.DELIVERED
                         || order.getStatus() == OrderStatus.CANCELLED)
            .collect(Collectors.toList());
    }
}
