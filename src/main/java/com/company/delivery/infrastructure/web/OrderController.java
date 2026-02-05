package com.company.delivery.infrastructure.web;

import com.company.delivery.application.port.input.CreateOrderUseCase;

/**
 * REST controller for order-related operations.
 *
 * <p>This controller serves as a primary adapter, translating HTTP requests
 * into use case calls and formatting responses for HTTP clients.</p>
 *
 * <h2>Endpoints (Placeholder)</h2>
 * <ul>
 *   <li>POST /api/v1/orders - Create a new order</li>
 *   <li>GET /api/v1/orders/{id} - Get order by ID</li>
 *   <li>GET /api/v1/orders - List orders (with filters)</li>
 *   <li>PUT /api/v1/orders/{id}/status - Update order status</li>
 *   <li>DELETE /api/v1/orders/{id} - Cancel an order</li>
 * </ul>
 *
 * <h2>Architectural Notes</h2>
 * <ul>
 *   <li>This is a PRIMARY ADAPTER (driving adapter)</li>
 *   <li>Depends on input ports (use case interfaces)</li>
 *   <li>Contains NO business logic</li>
 *   <li>Handles HTTP concerns only</li>
 * </ul>
 *
 * <p><strong>TODO:</strong> Add endpoint mappings when API design is finalized.</p>
 *
 * @see CreateOrderUseCase
 */
// @RestController
// @RequestMapping("/api/v1/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;

    /**
     * Constructs the controller with required use cases.
     *
     * @param createOrderUseCase the create order use case
     */
    public OrderController(CreateOrderUseCase createOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
    }

    // ==========================================================================
    // PLACEHOLDER - No endpoint mappings yet
    // ==========================================================================
    //
    // Future endpoints:
    //
    // @PostMapping
    // public ResponseEntity<OrderResponse> createOrder(
    //         @Valid @RequestBody CreateOrderRequest request) {
    //     CreateOrderCommand command = mapToCommand(request);
    //     OrderDto result = createOrderUseCase.execute(command);
    //     return ResponseEntity.status(HttpStatus.CREATED)
    //         .body(mapToResponse(result));
    // }
    //
    // @GetMapping("/{id}")
    // public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id) {
    //     // Call GetOrderUseCase
    // }
    //
    // @GetMapping
    // public ResponseEntity<Page<OrderSummaryResponse>> listOrders(
    //         @RequestParam(required = false) String status,
    //         Pageable pageable) {
    //     // Call ListOrdersUseCase
    // }
    // ==========================================================================

}
