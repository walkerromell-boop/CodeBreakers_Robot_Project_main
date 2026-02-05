package com.company.delivery.infrastructure.web;

/**
 * Web Adapters Package - REST Controllers.
 *
 * <h2>Purpose</h2>
 * <p>This package contains REST controllers that serve as primary (driving) adapters.
 * Controllers adapt HTTP requests to use case calls and transform results back to
 * HTTP responses.</p>
 *
 * <h2>What Belongs Here</h2>
 * <ul>
 *   <li>REST controllers</li>
 *   <li>Request/Response DTOs (HTTP-specific)</li>
 *   <li>Request mappers (HTTP DTO → Command)</li>
 *   <li>Response mappers (DTO → HTTP response)</li>
 *   <li>Exception handlers for HTTP errors</li>
 * </ul>
 *
 * <h2>What Does NOT Belong Here</h2>
 * <ul>
 *   <li>Business logic (delegate to use cases)</li>
 *   <li>Direct database access</li>
 *   <li>Domain objects (use DTOs)</li>
 *   <li>Complex validation logic (use domain)</li>
 * </ul>
 *
 * <h2>Controller Responsibilities</h2>
 * <ol>
 *   <li>Parse and validate HTTP request</li>
 *   <li>Map request to command/query</li>
 *   <li>Call use case</li>
 *   <li>Map result to HTTP response</li>
 *   <li>Handle errors appropriately</li>
 * </ol>
 *
 * <h2>Example</h2>
 * <pre>{@code
 * @RestController
 * @RequestMapping("/api/v1/orders")
 * public class OrderController {
 *
 *     private final CreateOrderUseCase createOrderUseCase;
 *
 *     @PostMapping
 *     public ResponseEntity<OrderResponse> create(@RequestBody CreateOrderRequest request) {
 *         CreateOrderCommand command = requestMapper.toCommand(request);
 *         OrderDto result = createOrderUseCase.execute(command);
 *         return ResponseEntity.status(HttpStatus.CREATED)
 *             .body(responseMapper.toResponse(result));
 *     }
 * }
 * }</pre>
 *
 * @see com.company.delivery.application.port.input Input Ports
 */
