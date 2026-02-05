# Domain Layer

## Purpose

The Domain layer is the **heart of the application**. It contains all business logic, business rules, and domain concepts. This layer is **completely independent** of any framework, database, or external technology.

## What Belongs Here

✅ **Entities (Aggregate Roots)** - Objects with identity that persist over time
✅ **Value Objects** - Immutable objects defined by their attributes
✅ **Domain Services** - Stateless business logic that doesn't belong to a single entity
✅ **Domain Repository Interfaces** - Contracts for persistence (NOT implementations)
✅ **Domain Events** - Events that represent something significant that happened
✅ **Domain Exceptions** - Business rule violation exceptions

## What Does NOT Belong Here

❌ Spring annotations (`@Service`, `@Component`, `@Transactional`)
❌ JPA/Hibernate annotations (`@Entity`, `@Table`, `@Column`)
❌ HTTP concepts (requests, responses, status codes)
❌ Database concepts (SQL, transactions, connection pools)
❌ External service implementations
❌ DTOs for API communication
❌ Framework-specific validation (`@Valid`, `@NotNull` from javax/jakarta)

## Package Structure

```
domain/
├── model/           # Entities and aggregate roots
├── valueobject/     # Immutable value objects
├── repository/      # Repository interfaces (domain ports)
├── service/         # Domain services
├── event/           # Domain events (future)
└── exception/       # Domain-specific exceptions (future)
```

## Ownership

**Primary Owner:** Domain Experts / Senior Engineers

Changes to the domain layer should be reviewed by:
- Product/Domain experts (for business rule accuracy)
- Senior engineers (for design patterns and DDD principles)

## Key Principles

1. **No Framework Dependencies** - Domain code must compile without Spring on the classpath
2. **Rich Domain Model** - Entities contain behavior, not just data (avoid anemic models)
3. **Immutability Where Possible** - Value objects must be immutable
4. **Ubiquitous Language** - Use business terminology, not technical jargon
5. **Self-Validating** - Domain objects validate their own invariants

## Example

```java
// Good: Rich domain model with business behavior
public class Order {
    private final OrderId id;
    private OrderStatus status;
    private final List<OrderItem> items;
    
    public void confirm() {
        if (this.status != OrderStatus.PENDING) {
            throw new OrderCannotBeConfirmedException(this.id);
        }
        this.status = OrderStatus.CONFIRMED;
    }
    
    public Money calculateTotal() {
        return items.stream()
            .map(OrderItem::getSubtotal)
            .reduce(Money.ZERO, Money::add);
    }
}
```
