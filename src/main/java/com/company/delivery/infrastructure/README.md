# Infrastructure Layer

## Purpose

The Infrastructure layer contains all **technical implementations** and **framework-specific code**. It implements the ports defined by the application and domain layers, connecting the application to external systems (databases, web, messaging, etc.).

## What Belongs Here

✅ **Web Adapters** - REST controllers, GraphQL resolvers
✅ **Persistence Adapters** - JPA repositories, database mappers
✅ **Security Adapters** - Authentication, authorization implementations
✅ **Messaging Adapters** - Event publishers, message consumers
✅ **Configuration** - Spring configuration, beans, properties
✅ **External Service Clients** - HTTP clients, SDK integrations

## What Does NOT Belong Here

❌ Business logic (belongs in domain)
❌ Use case orchestration (belongs in application)
❌ Domain models (belongs in domain)
❌ Port interfaces (belongs in application or domain)

## Package Structure

```
infrastructure/
├── web/               # REST controllers, request/response DTOs
├── persistence/       # JPA entities, repository adapters, mappers
├── security/          # Auth filters, JWT handling, OAuth
├── messaging/         # Event publishers, message consumers
└── config/            # Spring configuration classes
```

## Ownership

**Primary Owner:** Platform Team / Infrastructure Team

Changes to the infrastructure layer should be reviewed by:
- Platform team (for infrastructure patterns)
- Security team (for security-related changes)
- Feature teams (for adapter behavior)

## Key Principles

1. **Implements Ports** - Adapters implement interfaces from application/domain layers
2. **Framework Home** - All Spring, JPA, and library code lives here
3. **Inward Dependencies** - May depend on application and domain, never the reverse
4. **Separation of JPA** - JPA entities are separate from domain models

## Example

```java
// Persistence Adapter implementing domain port
@Repository
public class OrderPersistenceAdapter implements OrderRepository {
    
    private final OrderJpaRepository jpaRepository;
    private final OrderMapper mapper;
    
    @Override
    public void save(Order order) {
        OrderJpaEntity entity = mapper.toJpaEntity(order);
        jpaRepository.save(entity);
    }
    
    @Override
    public Optional<Order> findById(OrderId id) {
        return jpaRepository.findById(id.value())
            .map(mapper::toDomain);
    }
}
```
