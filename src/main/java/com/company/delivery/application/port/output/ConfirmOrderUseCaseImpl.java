package com.company.delivery.application.usecase;

import com.company.delivery.application.dto.OrderDto;
import com.company.delivery.application.port.input.ConfirmOrderUseCase;
import com.company.delivery.domain.model.Order;
import com.company.delivery.domain.repository.OrderRepository;
import com.company.delivery.domain.valueobject.OrderId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

/**
 * Use case for confirming an order.
 *
 * <p><strong>Coordination Pattern:</strong></p>
 * <ol>
 *   <li><strong>Find</strong> the order by ID</li>
 *   <li><strong>Ask</strong> the order to change state (domain handles rules)</li>
 *   <li><strong>Save</strong> the updated order</li>
 *   <li><strong>Return</strong> the result as DTO</li>
 * </ol>
 *
 * <p>Notice: This service has <strong>NO business logic</strong>. All validation
 * and rules are in the Order domain class. This service just orchestrates!</p>
 */
@Service
@Transactional
public class ConfirmOrderUseCaseImpl implements ConfirmOrderUseCase {

    private final OrderRepository orderRepository;

    public ConfirmOrderUseCaseImpl(OrderRepository orderRepository) {
        this.orderRepository = Objects.requireNonNull(orderRepository,
            "OrderRepository cannot be null");
    }

    /**
     * Confirms an order by transitioning it from PENDING to CONFIRMED.
     *
     * <p><strong>Orchestration Steps:</strong></p>
     * <ol>
     *   <li>Find order by ID (throws if not found)</li>
     *   <li>Call order.confirm() - domain enforces rules</li>
     *   <li>Save the updated order</li>
     *   <li>Return DTO</li>
     * </ol>
     *
     * @param orderId the UUID of the order to confirm
     * @return the confirmed order as DTO
     * @throws OrderNotFoundException if order doesn't exist
     * @throws IllegalStateException if order cannot be confirmed (from domain)
     */
    @Override
    public OrderDto execute(UUID orderId) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        
        // Step 1: Find the order
        OrderId orderIdVO = OrderId.of(orderId);
        Order order = orderRepository.findById(orderIdVO)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        // Step 2: Ask the order to change its state
        // (Order domain class enforces all business rules)
        order.confirm();
        
        // Step 3: Save the updated order
        orderRepository.save(order);
        
        // Step 4: Return DTO
        return OrderDto.from(order);
    }

    /**
     * Exception thrown when an order is not found.
     */
    public static class OrderNotFoundException extends RuntimeException {
        public OrderNotFoundException(UUID orderId) {
            super(String.format("Order not found with ID: %s", orderId));
        }
    }
}
