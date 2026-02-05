package com.company.delivery.application.usecase;

/**
 * Use Case Implementations Package.
 *
 * <h2>Purpose</h2>
 * <p>This package contains the concrete implementations of use cases defined
 * in the input ports. Use cases orchestrate domain objects and coordinate
 * with output ports to fulfill business operations.</p>
 *
 * <h2>What Belongs Here</h2>
 * <ul>
 *   <li>Use case implementation classes</li>
 *   <li>Orchestration logic (coordinate domain + ports)</li>
 *   <li>Transaction boundaries</li>
 *   <li>DTO mapping (domain to DTO)</li>
 * </ul>
 *
 * <h2>What Does NOT Belong Here</h2>
 * <ul>
 *   <li>Business rules (belongs in domain)</li>
 *   <li>Persistence logic (belongs in infrastructure)</li>
 *   <li>HTTP handling (belongs in infrastructure/web)</li>
 *   <li>Direct database access</li>
 * </ul>
 *
 * <h2>Spring Annotations</h2>
 * <p>Use cases may use minimal Spring annotations:</p>
 * <ul>
 *   <li>{@code @Service} - For component scanning</li>
 *   <li>{@code @Transactional} - For transaction boundaries</li>
 * </ul>
 * <p>Avoid all other Spring dependencies.</p>
 *
 * <h2>Naming Convention</h2>
 * <p>Implementation class name = Interface name + "Impl"</p>
 * <ul>
 *   <li>Interface: {@code CreateOrderUseCase}</li>
 *   <li>Implementation: {@code CreateOrderUseCaseImpl}</li>
 * </ul>
 *
 * <h2>Example</h2>
 * <pre>{@code
 * @Service
 * @Transactional
 * public class CreateOrderUseCaseImpl implements CreateOrderUseCase {
 *
 *     private final OrderRepository orderRepository;
 *
 *     public CreateOrderUseCaseImpl(OrderRepository orderRepository) {
 *         this.orderRepository = orderRepository;
 *     }
 *
 *     @Override
 *     public OrderDto execute(CreateOrderCommand command) {
 *         Order order = Order.create(command.customerId(), command.items());
 *         orderRepository.save(order);
 *         return OrderDto.from(order);
 *     }
 * }
 * }</pre>
 *
 * @see com.company.delivery.application.port.input Input Ports
 * @see com.company.delivery.domain.repository Domain Repositories
 */

