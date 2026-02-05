# Hexagonal Architecture Guide

## Introduction

This guide provides practical instructions for implementing features in the Delivery Application using Hexagonal Architecture. It explains the distinction between ports and adapters, where new code should go, and common anti-patterns to avoid.

## Core Concepts

### What is a Port?

A **Port** is an interface that defines a contract for communication between the application and the outside world.

```
┌─────────────────────────────────────────────────────────────┐
│                                                             │
│   INPUT PORTS                        OUTPUT PORTS           │
│   (Driving)                          (Driven)               │
│                                                             │
│   "How the outside        ←─────→    "How the application   │
│    world uses us"                     uses the outside"     │
│                                                             │
│   Examples:                          Examples:              │
│   - CreateOrderUseCase               - OrderRepository      │
│   - CancelOrderUseCase               - PaymentGateway       │
│   - GetOrderStatusUseCase            - NotificationService  │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### What is an Adapter?

An **Adapter** is a concrete implementation that connects a port to an external technology.

| Port Type | Adapter Type | Examples |
|-----------|-------------|----------|
| Input Port | Primary/Driving Adapter | REST Controller, CLI, GraphQL Resolver |
| Output Port | Secondary/Driven Adapter | JPA Repository, HTTP Client, Message Producer |

## Ports vs Adapters

### Input (Driving) Side

```java
// PORT - Lives in application/port/input
public interface CreateOrderUseCase {
    OrderDto execute(CreateOrderCommand command);
}

// ADAPTER - Lives in infrastructure/web
@RestController
public class OrderController {
    private final CreateOrderUseCase createOrderUseCase;
    
    // Controller adapts HTTP to use case call
}
```

### Output (Driven) Side

```java
// PORT - Lives in application/port/output
public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(OrderId id);
}

// ADAPTER - Lives in infrastructure/persistence
@Repository
public class OrderPersistenceAdapter implements OrderRepository {
    // Implements the port using JPA
}
```

## Where Does New Code Go?

### Decision Flowchart

```
Is it business logic with no external dependencies?
├── YES → DOMAIN LAYER
│         ├── Is it an entity with identity? → domain/model
│         ├── Is it a value without identity? → domain/valueobject
│         ├── Is it stateless business logic? → domain/service
│         └── Is it a persistence contract? → domain/repository
│
└── NO → Does it orchestrate business operations?
         ├── YES → APPLICATION LAYER
         │         ├── Is it a use case interface? → application/port/input
         │         ├── Is it a use case implementation? → application/usecase
         │         ├── Is it an external service contract? → application/port/output
         │         └── Is it data for transfer? → application/dto
         │
         └── NO → INFRASTRUCTURE LAYER
                  ├── Is it HTTP-related? → infrastructure/web
                  ├── Is it database-related? → infrastructure/persistence
                  ├── Is it auth-related? → infrastructure/security
                  ├── Is it messaging-related? → infrastructure/messaging
                  └── Is it Spring config? → infrastructure/config
```

### Quick Reference Table

| Type of Code | Package | Can Use Spring? | Can Use JPA? |
|--------------|---------|-----------------|--------------|
| Entity | domain/model | ❌ No | ❌ No |
| Value Object | domain/valueobject | ❌ No | ❌ No |
| Domain Service | domain/service | ❌ No | ❌ No |
| Domain Repository Interface | domain/repository | ❌ No | ❌ No |
| Use Case Interface | application/port/input | ❌ No | ❌ No |
| Use Case Implementation | application/usecase | ⚠️ Annotations only | ❌ No |
| Output Port | application/port/output | ❌ No | ❌ No |
| DTO | application/dto | ⚠️ Validation only | ❌ No |
| REST Controller | infrastructure/web | ✅ Yes | ❌ No |
| Persistence Adapter | infrastructure/persistence | ✅ Yes | ✅ Yes |
| JPA Entity | infrastructure/persistence | ✅ Yes | ✅ Yes |
| Security Filter | infrastructure/security | ✅ Yes | ❌ No |
| Configuration | infrastructure/config | ✅ Yes | ✅ Yes |

## Common Anti-Patterns

### ❌ Anti-Pattern 1: Domain Depends on Framework

```java
// WRONG - Domain model with JPA annotations
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue
    private Long id;
}
```

```java
// CORRECT - Pure domain model
public class Order {
    private final OrderId id;
    private OrderStatus status;
    
    // Business methods
    public void confirm() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be confirmed");
        }
        this.status = OrderStatus.CONFIRMED;
    }
}
```

### ❌ Anti-Pattern 2: Use Case Returns Domain Object

```java
// WRONG - Exposes domain internals
public interface GetOrderUseCase {
    Order execute(OrderId id);  // Domain leakage!
}
```

```java
// CORRECT - Returns DTO
public interface GetOrderUseCase {
    OrderDto execute(OrderId id);
}
```

### ❌ Anti-Pattern 3: Controller Contains Business Logic

```java
// WRONG - Business logic in controller
@PostMapping("/orders")
public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest request) {
    // Validation
    if (request.getItems().isEmpty()) {
        return ResponseEntity.badRequest().build();
    }
    
    // Business calculation
    BigDecimal total = request.getItems().stream()
        .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    
    // Persistence
    orderRepository.save(new Order(total));
}
```

```java
// CORRECT - Controller only adapts
@PostMapping("/orders")
public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
    CreateOrderCommand command = requestMapper.toCommand(request);
    OrderDto result = createOrderUseCase.execute(command);
    return ResponseEntity.ok(responseMapper.toResponse(result));
}
```

### ❌ Anti-Pattern 4: Application Layer Depends on Infrastructure

```java
// WRONG - Use case depends on JPA repository directly
@Service
public class CreateOrderUseCaseImpl implements CreateOrderUseCase {
    private final JpaOrderRepository jpaRepository;  // Infrastructure dependency!
}
```

```java
// CORRECT - Use case depends on port
@Service
public class CreateOrderUseCaseImpl implements CreateOrderUseCase {
    private final OrderRepository orderRepository;  // Port interface
}
```

### ❌ Anti-Pattern 5: Anemic Domain Model

```java
// WRONG - No business logic, just data
public class Order {
    private OrderStatus status;
    
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
}

// Business logic elsewhere
public class OrderService {
    public void confirmOrder(Order order) {
        if (order.getStatus() == OrderStatus.PENDING) {
            order.setStatus(OrderStatus.CONFIRMED);
        }
    }
}
```

```java
// CORRECT - Rich domain model
public class Order {
    private OrderStatus status;
    
    public void confirm() {
        if (this.status != OrderStatus.PENDING) {
            throw new OrderNotPendingException(this.id);
        }
        this.status = OrderStatus.CONFIRMED;
    }
    
    public OrderStatus getStatus() { return status; }
    // No setter - state changes through business methods
}
```

## Mapping Between Layers

### The Mapping Strategy

```
HTTP Request → Request DTO → Command → Domain → Domain Event/Result → Response DTO → HTTP Response
     │              │           │          │              │                │              │
     └──────────────┴───────────┴──────────┴──────────────┴────────────────┴──────────────┘
                                        Mappers handle all transformations
```

### Where Mappers Live

| Mapper | Location | Responsibility |
|--------|----------|----------------|
| Request → Command | infrastructure/web | Convert HTTP input to use case input |
| Domain → DTO | application/usecase | Convert domain results to DTOs |
| Domain ↔ JPA Entity | infrastructure/persistence | Convert between domain and persistence |

## Testing Strategy

### Unit Tests by Layer

| Layer | Test Type | Dependencies |
|-------|-----------|--------------|
| Domain | Unit Test | None - pure Java |
| Application | Unit Test | Mocked ports |
| Infrastructure | Integration Test | Real infrastructure (testcontainers) |

### Example Test Structure

```
src/test/java/com/company/delivery
├── domain/
│   └── model/
│       └── OrderTest.java           # Pure unit tests
├── application/
│   └── usecase/
│       └── CreateOrderUseCaseTest.java  # Mocked repository
└── infrastructure/
    └── persistence/
        └── OrderPersistenceAdapterIT.java  # With testcontainers
```

## Checklist for New Features

Before merging any new feature, verify:

- [ ] Domain models have NO framework annotations
- [ ] Use cases depend ONLY on ports (interfaces)
- [ ] Controllers contain NO business logic
- [ ] All external dependencies are behind ports
- [ ] DTOs are used for all cross-layer communication
- [ ] Business rules are in domain models or domain services
- [ ] Persistence entities are separate from domain models
- [ ] Each use case has a single responsibility
- [ ] Tests exist for domain logic (unit) and adapters (integration)

## Further Reading

- [Original Hexagonal Architecture Article by Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)
- [Clean Architecture by Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Domain-Driven Design by Eric Evans](https://www.domainlanguage.com/ddd/)
