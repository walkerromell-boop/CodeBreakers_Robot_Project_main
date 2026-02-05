# Application Layer

## Purpose

The Application layer orchestrates the flow of data and coordinates domain objects to perform use cases. It acts as a **mediator** between the outside world (infrastructure) and the domain layer.

## What Belongs Here

✅ **Use Case Interfaces (Input Ports)** - Define what the application can do
✅ **Use Case Implementations** - Orchestrate domain objects to fulfill use cases
✅ **Output Ports** - Interfaces for external services (repositories, gateways)
✅ **DTOs** - Data transfer objects for input/output
✅ **Command/Query Objects** - Input parameters for use cases
✅ **Application Exceptions** - Use case-specific exceptions

## What Does NOT Belong Here

❌ Business logic (belongs in domain)
❌ HTTP/REST concepts (belongs in infrastructure/web)
❌ Database queries or SQL (belongs in infrastructure/persistence)
❌ Framework-heavy code (keep Spring to annotations only)
❌ Security implementation details (belongs in infrastructure/security)

## Package Structure

```
application/
├── usecase/           # Use case implementations
├── port/
│   ├── input/         # Input ports (use case interfaces)
│   └── output/        # Output ports (repository interfaces, external services)
└── dto/               # Data transfer objects, commands, queries
```

## Ownership

**Primary Owner:** Feature Teams

Changes to the application layer should be reviewed by:
- Feature team members (for use case correctness)
- Senior engineers (for architectural compliance)

## Key Principles

1. **Thin Orchestration** - Use cases coordinate, they don't contain business logic
2. **Dependency Inversion** - Depend on ports (interfaces), not implementations
3. **Single Responsibility** - One use case = one specific operation
4. **DTO Boundaries** - Never expose domain objects; always convert to DTOs

## Example

```java
// Input Port (interface)
public interface CreateOrderUseCase {
    OrderDto execute(CreateOrderCommand command);
}

// Use Case Implementation
@Service
@Transactional
public class CreateOrderUseCaseImpl implements CreateOrderUseCase {
    
    private final OrderRepository orderRepository;  // Output port
    
    @Override
    public OrderDto execute(CreateOrderCommand command) {
        // Orchestrate domain objects
        Order order = Order.create(command.customerId(), command.items());
        orderRepository.save(order);
        return OrderDto.from(order);
    }
}
```
