package com.company.delivery.application.port.input;

import com.company.delivery.application.dto.CreateOrderCommand;
import com.company.delivery.application.dto.OrderDto;

/**
 * Input port for creating a new order.
 *
 * <p>This interface defines the contract for the order creation use case.
 * It is implemented by a use case class and called by infrastructure adapters
 * (e.g., REST controllers).</p>
 *
 * <h2>Architectural Notes</h2>
 * <ul>
 *   <li>This is an INPUT PORT (driving port)</li>
 *   <li>Called by primary adapters (controllers, CLI, etc.)</li>
 *   <li>Implementation in {@code application.usecase} package</li>
 *   <li>No Spring dependencies allowed</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * // In a REST controller (infrastructure layer)
 * @RestController
 * public class OrderController {
 *     private final CreateOrderUseCase createOrderUseCase;
 *
 *     @PostMapping("/orders")
 *     public OrderResponse create(@RequestBody CreateOrderRequest request) {
 *         CreateOrderCommand command = mapper.toCommand(request);
 *         OrderDto result = createOrderUseCase.execute(command);
 *         return mapper.toResponse(result);
 *     }
 * }
 * }</pre>
 *
 * @see CreateOrderCommand
 * @see OrderDto
 */
public interface CreateOrderUseCase {

    /**
     * Creates a new order based on the provided command.
     *
     * @param command the order creation parameters
     * @return the created order as a DTO
     */
    OrderDto execute(CreateOrderCommand command);

}
