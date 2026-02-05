package com.company.delivery.application.dto;

/**
 * Data Transfer Objects Package.
 *
 * <h2>Purpose</h2>
 * <p>This package contains DTOs used for communication between layers.
 * DTOs prevent domain model leakage and provide a stable API contract
 * independent of domain changes.</p>
 *
 * <h2>What Belongs Here</h2>
 * <ul>
 *   <li>Command objects (input for write operations)</li>
 *   <li>Query objects (input for read operations)</li>
 *   <li>Result DTOs (output from use cases)</li>
 *   <li>Validation annotations (jakarta.validation)</li>
 * </ul>
 *
 * <h2>What Does NOT Belong Here</h2>
 * <ul>
 *   <li>Domain objects</li>
 *   <li>JPA entities</li>
 *   <li>HTTP request/response objects (use infrastructure/web)</li>
 *   <li>Business logic</li>
 * </ul>
 *
 * <h2>Design Rules</h2>
 * <ul>
 *   <li>DTOs should be immutable (prefer records)</li>
 *   <li>Use validation annotations for input validation</li>
 *   <li>Provide factory methods for mapping from domain</li>
 *   <li>Keep DTOs flat—avoid deep nesting</li>
 * </ul>
 *
 * <h2>Naming Convention</h2>
 * <ul>
 *   <li>Commands: {@code <Action><Entity>Command} (e.g., CreateOrderCommand)</li>
 *   <li>Queries: {@code <Entity>Query} or {@code Get<Entity>Query}</li>
 *   <li>Results: {@code <Entity>Dto} (e.g., OrderDto)</li>
 * </ul>
 *
 * <h2>Example</h2>
 * <pre>{@code
 * public record CreateOrderCommand(
 *     @NotNull UUID customerId,
 *     @NotEmpty List<OrderItemDto> items
 * ) {}
 *
 * public record OrderDto(
 *     UUID id,
 *     String status,
 *     BigDecimal totalAmount,
 *     Instant createdAt
 * ) {
 *     public static OrderDto from(Order order) {
 *         return new OrderDto(
 *             order.getId().value(),
 *             order.getStatus().name(),
 *             order.getTotalAmount().value(),
 *             order.getCreatedAt()
 *         );
 *     }
 * }
 * }</pre>
 *
 * @see com.company.delivery.application.usecase Use Cases
 */

