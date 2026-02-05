package com.company.delivery.application.usecase;

import com.company.delivery.application.dto.OrderDto;
import com.company.delivery.application.port.input.DeliverOrderUseCase;
import com.company.delivery.domain.model.Order;
import com.company.delivery.domain.repository.OrderRepository;
import com.company.delivery.domain.valueobject.OrderId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

/**
 * Use case for marking an order as delivered.
 *
 * <p>This is the <strong>exact same coordination pattern</strong>:
 * Find → Ask → Save → Return</p>
 *
 * <p>Starting to see the pattern? Good! This is how professional backends work.
 * The application layer is thin and predictable. All complexity lives in the domain.</p>
 */
@Service
@Transactional
public class DeliverOrderUseCaseImpl implements DeliverOrderUseCase {

    private final OrderRepository orderRepository;

    public DeliverOrderUseCaseImpl(OrderRepository orderRepository) {
        this.orderRepository = Objects.requireNonNull(orderRepository,
            "OrderRepository cannot be null");
    }

    /**
     * Marks an order as delivered.
     *
     * @param orderId the UUID of the order to deliver
     * @return the delivered order as DTO
     * @throws OrderNotFoundException if order doesn't exist
     * @throws IllegalStateException if order is not dispatched (enforced by domain)
     */
    @Override
    public OrderDto execute(UUID orderId) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        
        // The pattern you know and love:
        OrderId orderIdVO = OrderId.of(orderId);
        Order order = orderRepository.findById(orderIdVO)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        order.deliver();  // Domain enforces: must be DISPATCHED
        
        orderRepository.save(order);
        
        return OrderDto.from(order);
    }

    public static class OrderNotFoundException extends RuntimeException {
        public OrderNotFoundException(UUID orderId) {
            super(String.format("Order not found with ID: %s", orderId));
        }
    }
}
