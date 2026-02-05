package com.company.delivery.application.port.output;

/**
 * Output port for sending notifications.
 *
 * <p>This interface defines the contract for sending notifications to users.
 * The actual implementation (email, push, SMS) is provided by an adapter
 * in the infrastructure layer.</p>
 *
 * <h2>Architectural Notes</h2>
 * <ul>
 *   <li>This is an OUTPUT PORT (driven port)</li>
 *   <li>Implemented by adapters in infrastructure/messaging</li>
 *   <li>Called by use cases in application layer</li>
 *   <li>No implementation details here</li>
 * </ul>
 *
 * <p><strong>TODO:</strong> Define notification methods when requirements are finalized.</p>
 *
 * @see com.company.delivery.infrastructure.messaging Messaging Adapters
 */
public interface NotificationService {

    // ==========================================================================
    // PLACEHOLDER - No methods yet
    // ==========================================================================
    //
    // Future methods may include:
    // - void sendOrderConfirmation(OrderId orderId, CustomerId customerId)
    // - void sendDeliveryUpdate(DeliveryId deliveryId, DeliveryStatus status)
    // - void sendPromotionalNotification(CustomerId customerId, Promotion promotion)
    // ==========================================================================

}
