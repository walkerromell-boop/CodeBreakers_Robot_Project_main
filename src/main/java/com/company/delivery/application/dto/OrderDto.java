package com.company.delivery.application.dto;

import com.company.delivery.domain.model.Order;
import com.company.delivery.domain.model.OrderStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Data transfer object representing an order.
 *
 * <p>This record is returned by order-related use cases. It provides a stable
 * API contract that is independent of the internal domain model structure.</p>
 *
 * <h2>Purpose</h2>
 * <ul>
 *   <li>Decouples API from domain model</li>
 *   <li>Prevents domain object leakage</li>
 *   <li>Provides a stable contract for clients</li>
 * </ul>
 */
public record OrderDto(
    UUID id,
    String customerName,
    String status,
    List<OrderItemDto> items,
    double totalAmount,
    int itemCount,
    Instant createdAt,
    Instant updatedAt
) {

    /**
     * Creates an OrderDto from a domain Order object.
     *
     * <p>This is the mapping layer between domain and application.</p>
     *
     * @param order the domain order
     * @return the DTO representation
     */
    public static OrderDto from(Order order) {
        List<OrderItemDto> itemDtos = order.getItems().stream()
            .map(item -> new OrderItemDto(
                item.getDescription(),
                item.getPrice(),
                item.getType().name()
            ))
            .collect(Collectors.toList());
        
        return new OrderDto(
            order.getId().value(),
            order.getCustomerName(),
            order.getStatus().name(),
            itemDtos,
            order.calculateTotal(),
            order.getItemCount(),
            order.getCreatedAt(),
            order.getUpdatedAt()
        );
    }

    /**
     * DTO for a single item within an order.
     */
    public record OrderItemDto(
        String description,
        double price,
        String type  // SANDWICH, CHIPS, or DRINK
    ) {}
}
