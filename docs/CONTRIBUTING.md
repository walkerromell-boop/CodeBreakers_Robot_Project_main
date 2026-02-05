# Contributing Guidelines

## Introduction

This document outlines how to contribute to the Delivery Application backend. Following these guidelines ensures consistency across the codebase and maintains our architectural integrity.

## Getting Started

### Prerequisites

1. Java 17 JDK installed
2. Maven 3.8+ installed
3. Docker Desktop running
4. IDE with Java support (IntelliJ IDEA recommended)

### Initial Setup

```bash
# Clone the repository
git clone <repository-url>

# Build the project
mvn clean install

# Run tests
mvn test
```

## How to Add a New Feature

### Step 1: Understand the Domain

Before writing code, ensure you understand:
- What business problem are you solving?
- What domain concepts are involved?
- What are the business rules?

### Step 2: Start from the Domain (Inside-Out)

Always start with the domain layer and work outward:

```
1. Domain Model/Value Object (if new concept)
      ↓
2. Domain Repository Interface (if persistence needed)
      ↓
3. Application Port (input interface)
      ↓
4. Application Use Case (implementation)
      ↓
5. Output Port (if external service needed)
      ↓
6. Infrastructure Adapter (REST, persistence, etc.)
```

### Step 3: Create Files in Correct Packages

| What You're Adding | Package Location |
|-------------------|------------------|
| New Entity | `domain/model/` |
| New Value Object | `domain/valueobject/` |
| Domain Repository Interface | `domain/repository/` |
| Input Port (Use Case Interface) | `application/port/input/` |
| Use Case Implementation | `application/usecase/` |
| Output Port | `application/port/output/` |
| Command/Query DTO | `application/dto/` |
| REST Controller | `infrastructure/web/` |
| Persistence Adapter | `infrastructure/persistence/` |
| JPA Entity | `infrastructure/persistence/entity/` |
| Mapper | Relevant infrastructure package |

### Step 4: Write Tests

| Layer | Test Type | Location |
|-------|-----------|----------|
| Domain | Unit Test | `src/test/java/.../domain/` |
| Application | Unit Test (mocked ports) | `src/test/java/.../application/` |
| Infrastructure | Integration Test | `src/test/java/.../infrastructure/` |

## Naming Conventions

### Classes

| Type | Convention | Example |
|------|-----------|---------|
| Entity | Noun | `Order`, `Customer`, `Delivery` |
| Value Object | Descriptive noun | `OrderId`, `Address`, `Money` |
| Use Case Interface | Verb phrase + UseCase | `CreateOrderUseCase`, `GetOrderByIdUseCase` |
| Use Case Implementation | Interface name + Impl | `CreateOrderUseCaseImpl` |
| Command | Action + Command | `CreateOrderCommand`, `UpdateAddressCommand` |
| Query | Question + Query | `GetOrderQuery`, `FindOrdersByCustomerQuery` |
| DTO | Entity + Dto | `OrderDto`, `CustomerDto` |
| Repository | Entity + Repository | `OrderRepository`, `CustomerRepository` |
| REST Controller | Entity + Controller | `OrderController`, `CustomerController` |
| Persistence Adapter | Entity + PersistenceAdapter | `OrderPersistenceAdapter` |
| JPA Entity | Entity + JpaEntity | `OrderJpaEntity` |

### Methods

| Type | Convention | Example |
|------|-----------|---------|
| Use Case | `execute` | `execute(CreateOrderCommand)` |
| Query | `get`, `find`, `search` | `findById`, `searchByStatus` |
| Command | `create`, `update`, `delete` | `createOrder`, `updateStatus` |
| Boolean | `is`, `has`, `should`, `can` | `isValid`, `hasItems`, `canCancel` |

### Packages

- All lowercase
- Singular nouns for concepts: `model`, `service`, `port`
- Plural where it represents a collection of types: `usecase`

## Code Style

### Java Style

```java
// Use final for immutable fields
private final OrderId id;

// Use Optional for nullable returns
public Optional<Order> findById(OrderId id);

// Prefer early returns (guard clauses)
public void validate() {
    if (items.isEmpty()) {
        throw new EmptyOrderException();
    }
    
    if (totalAmount.isNegative()) {
        throw new InvalidAmountException();
    }
    
    // Main logic here
}

// Use meaningful constants
private static final int MAX_ORDER_ITEMS = 50;
private static final Duration ORDER_EXPIRY = Duration.ofHours(24);
```

### Documentation

```java
/**
 * Represents an order in the delivery system.
 * 
 * <p>An order is the aggregate root for the ordering bounded context.
 * It maintains consistency of order items and tracks the order lifecycle.</p>
 * 
 * <p>Business Rules:</p>
 * <ul>
 *   <li>An order must have at least one item</li>
 *   <li>Only pending orders can be cancelled</li>
 *   <li>Order total is calculated from item prices</li>
 * </ul>
 */
public class Order {
    // ...
}
```

## Pull Request Process

### Before Opening a PR

1. ✅ All tests pass locally (`mvn test`)
2. ✅ Code compiles without warnings (`mvn compile`)
3. ✅ No architectural violations (domain has no framework deps)
4. ✅ README updated if adding new packages
5. ✅ JavaDoc added for public classes and methods

### PR Title Convention

```
<type>(<scope>): <description>

Examples:
feat(order): add create order use case
fix(persistence): correct order status mapping
refactor(domain): extract money value object
docs(contributing): add naming conventions
test(order): add unit tests for order validation
```

### PR Checklist

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] New feature
- [ ] Bug fix
- [ ] Refactoring
- [ ] Documentation

## Architecture Compliance
- [ ] Domain layer has no framework dependencies
- [ ] Use cases depend only on ports
- [ ] Controllers contain no business logic
- [ ] New ports have corresponding adapters
- [ ] DTOs used for cross-layer communication

## Testing
- [ ] Unit tests added for domain logic
- [ ] Unit tests added for use cases (mocked ports)
- [ ] Integration tests added for adapters

## Documentation
- [ ] JavaDoc added for public APIs
- [ ] README updated if needed
```

## Code Review Guidelines

### What Reviewers Check

1. **Architecture Compliance**
   - No framework dependencies in domain
   - Correct package placement
   - Proper use of ports and adapters

2. **Code Quality**
   - Single responsibility per class
   - Meaningful names
   - No magic numbers/strings
   - Guard clauses over nested conditions

3. **Testing**
   - Adequate test coverage
   - Tests at appropriate level (unit vs integration)
   - Tests verify behavior, not implementation

4. **Documentation**
   - Public APIs have JavaDoc
   - Complex business rules explained
   - README updated if needed

### Review Response

- Address all comments before requesting re-review
- Explain decisions that differ from suggestions
- Mark conversations as resolved when addressed

## Common Mistakes to Avoid

| Mistake | Why It's Wrong | What to Do Instead |
|---------|---------------|-------------------|
| JPA annotations in domain | Framework dependency | Create separate JPA entities in infrastructure |
| Business logic in controller | Wrong layer | Move to use case or domain service |
| Returning domain objects from use cases | Domain leakage | Return DTOs |
| Large use case classes | Violates SRP | Split into smaller, focused use cases |
| Skipping ports | Tight coupling | Always define interface before implementation |
| Testing with real database in unit tests | Slow, brittle | Use mocks, save DB for integration tests |

## Getting Help

- **Architecture Questions**: Tag `@architecture-team` in PR
- **Domain Questions**: Consult with domain experts
- **Technical Issues**: Check existing issues or create new one

## Appendix: File Templates

### Domain Entity Template

```java
package com.company.delivery.domain.model;

/**
 * Brief description of what this entity represents.
 * 
 * <p>Business Rules:</p>
 * <ul>
 *   <li>Rule 1</li>
 *   <li>Rule 2</li>
 * </ul>
 */
public class EntityName {
    
    private final EntityNameId id;
    
    // Constructor, business methods, getters
}
```

### Use Case Template

```java
package com.company.delivery.application.usecase;

/**
 * Implements the [action] use case.
 * 
 * <p>This use case is responsible for [responsibility].</p>
 */
public class ActionEntityUseCaseImpl implements ActionEntityUseCase {
    
    private final EntityRepository entityRepository;
    
    public ActionEntityUseCaseImpl(EntityRepository entityRepository) {
        this.entityRepository = entityRepository;
    }
    
    @Override
    public EntityDto execute(ActionEntityCommand command) {
        // Implementation
    }
}
```
