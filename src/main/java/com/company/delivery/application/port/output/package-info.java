package com.company.delivery.application.port.output;

/**
 * Output Ports Package - External Service Interfaces.
 *
 * <h2>Purpose</h2>
 * <p>This package contains output port interfaces that define contracts for
 * external services and infrastructure. These are the "driven" ports in hexagonal
 * architecture—they are implemented by secondary adapters (like persistence adapters).</p>
 *
 * <h2>What Belongs Here</h2>
 * <ul>
 *   <li>External service gateway interfaces</li>
 *   <li>Notification service interfaces</li>
 *   <li>Payment gateway interfaces</li>
 *   <li>Any interface for external system communication</li>
 * </ul>
 *
 * <h2>What Does NOT Belong Here</h2>
 * <ul>
 *   <li>Repository interfaces (use domain/repository for aggregate persistence)</li>
 *   <li>Implementation classes (use infrastructure layer)</li>
 *   <li>Spring annotations</li>
 *   <li>Technology-specific code</li>
 * </ul>
 *
 * <h2>Note on Repository Interfaces</h2>
 * <p>Repository interfaces for domain aggregates are located in {@code domain/repository}
 * because they are part of the domain model's persistence contract. This package is for
 * external service integrations that are not directly tied to domain persistence.</p>
 *
 * <h2>Example</h2>
 * <pre>{@code
 * public interface PaymentGateway {
 *     PaymentResult processPayment(PaymentRequest request);
 *     PaymentStatus checkStatus(PaymentId paymentId);
 * }
 * }</pre>
 *
 * @see com.company.delivery.domain.repository Domain Repositories
 * @see com.company.delivery.infrastructure Infrastructure Adapters
 */

