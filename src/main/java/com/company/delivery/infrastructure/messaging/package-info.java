package com.company.delivery.infrastructure.messaging;

/**
 * Messaging Adapters Package - Events & Queues.
 *
 * <h2>Purpose</h2>
 * <p>This package contains adapters for asynchronous messaging, including
 * event publishing, message queue integration, and notification services.</p>
 *
 * <h2>What Belongs Here</h2>
 * <ul>
 *   <li>Event publishers</li>
 *   <li>Message queue producers</li>
 *   <li>Message consumers/listeners</li>
 *   <li>Notification service implementations</li>
 *   <li>Webhook handlers</li>
 * </ul>
 *
 * <h2>What Does NOT Belong Here</h2>
 * <ul>
 *   <li>Domain events definition (use domain/event)</li>
 *   <li>Business logic</li>
 *   <li>Synchronous service calls</li>
 *   <li>HTTP controllers</li>
 * </ul>
 *
 * <h2>Messaging Patterns</h2>
 *
 * <h3>Event Publishing</h3>
 * <pre>{@code
 * // Port (in application/port/output)
 * public interface EventPublisher {
 *     void publish(DomainEvent event);
 * }
 *
 * // Adapter
 * @Component
 * public class RabbitMQEventPublisher implements EventPublisher {
 *     @Override
 *     public void publish(DomainEvent event) {
 *         // Publish to RabbitMQ
 *     }
 * }
 * }</pre>
 *
 * <h3>Message Consumption</h3>
 * <pre>{@code
 * @Component
 * public class OrderEventListener {
 *     @RabbitListener(queues = "order-events")
 *     public void handleOrderEvent(OrderCreatedEvent event) {
 *         // Process event - call appropriate use case
 *     }
 * }
 * }</pre>
 *
 * <h2>Technology Options</h2>
 * <ul>
 *   <li>RabbitMQ (recommended for most cases)</li>
 *   <li>Apache Kafka (for high-throughput event streaming)</li>
 *   <li>AWS SQS/SNS (for cloud-native deployment)</li>
 *   <li>Spring Events (for in-process events)</li>
 * </ul>
 *
 * @see com.company.delivery.application.port.output.NotificationService
 */
