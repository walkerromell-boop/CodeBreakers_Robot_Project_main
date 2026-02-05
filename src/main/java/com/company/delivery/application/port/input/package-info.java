package com.company.delivery.application.port.input;

/**
 * Input Ports Package - Use Case Interfaces.
 *
 * <h2>Purpose</h2>
 * <p>This package contains input port interfaces that define what operations
 * the application supports. These are the "driving" ports in hexagonal architecture—
 * they are called by primary adapters (like REST controllers).</p>
 *
 * <h2>What Belongs Here</h2>
 * <ul>
 *   <li>Use case interfaces (one per business operation)</li>
 *   <li>Command interfaces for write operations</li>
 *   <li>Query interfaces for read operations</li>
 * </ul>
 *
 * <h2>What Does NOT Belong Here</h2>
 * <ul>
 *   <li>Implementation classes (use application/usecase)</li>
 *   <li>Spring annotations</li>
 *   <li>HTTP/REST concepts</li>
 *   <li>Domain logic</li>
 * </ul>
 *
 * <h2>Naming Convention</h2>
 * <ul>
 *   <li>Commands: {@code <Verb><Noun>UseCase} (e.g., CreateOrderUseCase)</li>
 *   <li>Queries: {@code Get<Noun>UseCase} or {@code Find<Noun>UseCase}</li>
 * </ul>
 *
 * <h2>Example</h2>
 * <pre>{@code
 * public interface CreateOrderUseCase {
 *     OrderDto execute(CreateOrderCommand command);
 * }
 * }</pre>
 *
 * @see com.company.delivery.application.usecase Use Case Implementations
 * @see com.company.delivery.infrastructure.web Web Adapters
 */

