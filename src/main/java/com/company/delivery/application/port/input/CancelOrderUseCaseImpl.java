package com.company.delivery.application.usecase;

import com.company.delivery.application.dto.OrderDto;
import com.company.delivery.application.port.input.CancelOrderUseCase;
import com.company.delivery.domain.model.Order;
import com.company.delivery.domain.repository.OrderRepository;
import com.company.delivery.domain.valueobject.OrderId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

/**
 * Use case for cancelling an order.
 *
 * <p>Same pattern. Different domain method. That's the beauty of separation of concerns!</p>
 */
@Service
@Transactional
public class CancelOrderUseCaseImpl implements CancelOrderUseCase {

    private final OrderRepository orderRepository;

    public CancelOrderUseCaseImpl(OrderRepository orderRepository) {
        this.orderRepository = Objects.requireNonNull(orderRepository,
            "OrderRepository cannot be null");
    }

    /**
     * Cancels an order.
     *
     * @param orderId the UUID of the order to cancel
     * @return the cancelled order as DTO
     * @throws OrderNotFoundException if order doesn't exist
     * @throws IllegalStateException if order cannot be cancelled (e.g., already delivered)
     */
    @Override
    public OrderDto execute(UUID orderId) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        
        OrderId orderIdVO = OrderId.of(orderId);
        Order order = orderRepository.findById(orderIdVO)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        order.cancel();  // Domain enforces: cannot cancel DELIVERED orders
        
        orderRepository.save(order);
        
        return OrderDto.from(order);
    }

    public static class OrderNotFoundException extends RuntimeException {
        public OrderNotFoundException(UUID orderId) {
            super(String.format("Order not found with ID: %s", orderId));
        }
    }
}
