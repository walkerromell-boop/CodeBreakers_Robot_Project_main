package com.company.delivery.application.usecase;

import com.company.delivery.application.dto.OrderDto;
import com.company.delivery.application.port.input.GetOrderUseCase;
import com.company.delivery.domain.model.Order;
import com.company.delivery.domain.repository.OrderRepository;
import com.company.delivery.domain.valueobject.OrderId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

/**
 * Use case for retrieving a single order by ID.
 *
 * <p>This is a <strong>query use case</strong> (read-only).
 * It's simpler than command use cases: Find → Return</p>
 */
@Service
@Transactional(readOnly = true)  // Read-only optimization
public class GetOrderUseCaseImpl implements GetOrderUseCase {

    private final OrderRepository orderRepository;

    public GetOrderUseCaseImpl(OrderRepository orderRepository) {
        this.orderRepository = Objects.requireNonNull(orderRepository,
            "OrderRepository cannot be null");
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param orderId the UUID of the order to retrieve
     * @return the order as DTO
     * @throws OrderNotFoundException if order doesn't exist
     */
    @Override
    public OrderDto execute(UUID orderId) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        
        OrderId orderIdVO = OrderId.of(orderId);
        Order order = orderRepository.findById(orderIdVO)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        return OrderDto.from(order);
    }

    public static class OrderNotFoundException extends RuntimeException {
        public OrderNotFoundException(UUID orderId) {
            super(String.format("Order not found with ID: %s", orderId));
        }
    }
}
