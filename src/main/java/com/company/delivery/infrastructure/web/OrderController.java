package com.company.delivery.infrastructure.web;

import com.company.delivery.application.dto.CreateOrderCommand;
import com.company.delivery.application.dto.OrderDto;
import com.company.delivery.application.usecase.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for all order operations.
 *
 * <p>Endpoints:</p>
 * <ul>
 *   <li>POST   /api/v1/orders               - Place a new order</li>
 *   <li>GET    /api/v1/orders/active         - View all active orders</li>
 *   <li>GET    /api/v1/orders/history        - View order history</li>
 *   <li>GET    /api/v1/orders/{id}           - View a specific order</li>
 *   <li>POST   /api/v1/orders/{id}/confirm   - Confirm an order</li>
 *   <li>POST   /api/v1/orders/{id}/dispatch  - Dispatch an order</li>
 *   <li>POST   /api/v1/orders/{id}/deliver   - Mark as delivered</li>
 *   <li>POST   /api/v1/orders/{id}/cancel    - Cancel an order</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/orders")
@CrossOrigin(origins = "*") // For demo - restrict in production
public class OrderController {

    private final CreateOrderUseCaseImpl createOrderUseCase;
    private final ConfirmOrderUseCaseImpl confirmOrderUseCase;
    private final DispatchOrderUseCaseImpl dispatchOrderUseCase;
    private final DeliverOrderUseCaseImpl deliverOrderUseCase;
    private final CancelOrderUseCaseImpl cancelOrderUseCase;
    private final GetOrderUseCaseImpl getOrderUseCase;
    private final ListActiveOrdersUseCaseImpl listActiveOrdersUseCase;
    private final GetOrderHistoryUseCaseImpl getOrderHistoryUseCase;

    public OrderController(
            CreateOrderUseCaseImpl createOrderUseCase,
            ConfirmOrderUseCaseImpl confirmOrderUseCase,
            DispatchOrderUseCaseImpl dispatchOrderUseCase,
            DeliverOrderUseCaseImpl deliverOrderUseCase,
            CancelOrderUseCaseImpl cancelOrderUseCase,
            GetOrderUseCaseImpl getOrderUseCase,
            ListActiveOrdersUseCaseImpl listActiveOrdersUseCase,
            GetOrderHistoryUseCaseImpl getOrderHistoryUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.confirmOrderUseCase = confirmOrderUseCase;
        this.dispatchOrderUseCase = dispatchOrderUseCase;
        this.deliverOrderUseCase = deliverOrderUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
        this.listActiveOrdersUseCase = listActiveOrdersUseCase;
        this.getOrderHistoryUseCase = getOrderHistoryUseCase;
    }

    // =====================================================================
    // 1. PLACE AN ORDER
    // =====================================================================

    /**
     * POST /api/v1/orders
     * Place a new order.
     *
     * Example request body:
     * {
     *   "customerName": "John Doe",
     *   "sandwiches": [{
     *     "breadSize": 8,
     *     "breadType": "White",
     *     "toasted": true,
     *     "meats": [{"name": "Turkey", "extra": false}],
     *     "cheeses": [{"name": "American", "extra": false}],
     *     "toppings": [{"name": "Lettuce", "category": "vegetable"}],
     *     "sauces": ["Mayo"]
     *   }],
     *   "chips": [{"chipName": "BBQ"}],
     *   "drinks": [{"drinkName": "Coke", "drinkSize": "large"}]
     * }
     */
    @PostMapping
    public ResponseEntity<OrderDto> placeOrder(@Valid @RequestBody CreateOrderCommand command) {
        OrderDto order = createOrderUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    // =====================================================================
    // 2. VIEW ACTIVE ORDERS
    // =====================================================================

    /**
     * GET /api/v1/orders/active
     * Returns all orders that are PENDING, CONFIRMED, or DISPATCHED.
     */
    @GetMapping("/active")
    public ResponseEntity<List<OrderDto>> getActiveOrders() {
        List<OrderDto> orders = listActiveOrdersUseCase.execute();
        return ResponseEntity.ok(orders);
    }

    // =====================================================================
    // 3. VIEW ORDER HISTORY
    // =====================================================================

    /**
     * GET /api/v1/orders/history
     * Returns all DELIVERED and CANCELLED orders.
     */
    @GetMapping("/history")
    public ResponseEntity<List<OrderDto>> getOrderHistory() {
        List<OrderDto> orders = getOrderHistoryUseCase.execute();
        return ResponseEntity.ok(orders);
    }

    // =====================================================================
    // 4. VIEW A SPECIFIC ORDER
    // =====================================================================

    /**
     * GET /api/v1/orders/{id}
     * Returns a single order by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable UUID id) {
        OrderDto order = getOrderUseCase.execute(id);
        return ResponseEntity.ok(order);
    }

    // =====================================================================
    // 5. CONFIRM AN ORDER
    // =====================================================================

    /**
     * POST /api/v1/orders/{id}/confirm
     * Confirms an order (PENDING → CONFIRMED).
     * This is also where payment would be processed.
     */
    @PostMapping("/{id}/confirm")
    public ResponseEntity<OrderDto> confirmOrder(@PathVariable UUID id) {
        OrderDto order = confirmOrderUseCase.execute(id);
        return ResponseEntity.ok(order);
    }

    // =====================================================================
    // 6. DISPATCH AN ORDER
    // =====================================================================

    /**
     * POST /api/v1/orders/{id}/dispatch
     * Dispatches an order for delivery (CONFIRMED → DISPATCHED).
     */
    @PostMapping("/{id}/dispatch")
    public ResponseEntity<OrderDto> dispatchOrder(@PathVariable UUID id) {
        OrderDto order = dispatchOrderUseCase.execute(id);
        return ResponseEntity.ok(order);
    }

    // =====================================================================
    // 7. DELIVER AN ORDER (TRACK STATUS)
    // =====================================================================

    /**
     * POST /api/v1/orders/{id}/deliver
     * Marks an order as delivered (DISPATCHED → DELIVERED).
     */
    @PostMapping("/{id}/deliver")
    public ResponseEntity<OrderDto> deliverOrder(@PathVariable UUID id) {
        OrderDto order = deliverOrderUseCase.execute(id);
        return ResponseEntity.ok(order);
    }

    // =====================================================================
    // 8. CANCEL AN ORDER
    // =====================================================================

    /**
     * POST /api/v1/orders/{id}/cancel
     * Cancels an order (any status except DELIVERED).
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderDto> cancelOrder(@PathVariable UUID id) {
        OrderDto order = cancelOrderUseCase.execute(id);
        return ResponseEntity.ok(order);
    }

    // =====================================================================
    // 9. GLOBAL ERROR HANDLING
    // =====================================================================

    /**
     * Handles OrderNotFoundException from any use case.
     */
    @ExceptionHandler({
        ConfirmOrderUseCaseImpl.OrderNotFoundException.class,
        DispatchOrderUseCaseImpl.OrderNotFoundException.class,
        DeliverOrderUseCaseImpl.OrderNotFoundException.class,
        CancelOrderUseCaseImpl.OrderNotFoundException.class,
        GetOrderUseCaseImpl.OrderNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleOrderNotFound(RuntimeException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("ORDER_NOT_FOUND", ex.getMessage()));
    }

    /**
     * Handles business rule violations (invalid state transitions).
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new ErrorResponse("INVALID_ORDER_STATE", ex.getMessage()));
    }

    /**
     * Simple error response record.
     */
    public record ErrorResponse(String code, String message) {}
}
