package com.company.delivery.application.usecase;

import com.company.delivery.application.dto.OrderDto;
import com.company.delivery.application.port.input.DispatchOrderUseCase;
import com.company.delivery.domain.model.Order;
import com.company.delivery.domain.repository.OrderRepository;
import com.company.delivery.domain.valueobject.OrderId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

/**
 * Use case for dispatching an order for delivery.
 *
 * <p><strong>The Coordination Pattern (again!):</strong></p>
 * <ol>
 *   <li>Find → order by ID</li>
 *   <li>Ask → order to dispatch (domain validates)</li>
 *   <li>Save → updated order</li>
 *   <li>Return → result DTO</li>
 * </ol>
 *
 * <p>This is the <strong>same pattern</strong> as ConfirmOrderUseCase.
 * That's the point - application services follow consistent patterns!</p>
 */
@Service
@Transactional
public class DispatchOrderUseCaseImpl implements DispatchOrderUseCase {

    private final OrderRepository orderRepository;

    public DispatchOrderUseCaseImpl(OrderRepository orderRepository) {
        this.orderRepository = Objects.requireNonNull(orderRepository,
            "OrderRepository cannot be null");
    }

    /**
     * Dispatches an order for delivery.
     *
     * @param orderId the UUID of the order to dispatch
     * @return the dispatched order as DTO
     * @throws OrderNotFoundException if order doesn't exist
     * @throws IllegalStateException if order is not confirmed (enforced by domain)
     */
    @Override
    public OrderDto execute(UUID orderId) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        
        // 1. Find
        OrderId orderIdVO = OrderId.of(orderId);
        Order order = orderRepository.findById(orderIdVO)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        // 2. Ask (domain enforces: must be CONFIRMED)
        order.dispatch();
        
        // 3. Save
        orderRepository.save(order);
        
        // 4. Return
        return OrderDto.from(order);
    }

    public static class OrderNotFoundException extends RuntimeException {
        public OrderNotFoundException(UUID orderId) {
            super(String.format("Order not found with ID: %s", orderId));
        }
    }
}
