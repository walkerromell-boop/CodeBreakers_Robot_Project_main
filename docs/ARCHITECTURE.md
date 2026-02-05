# Architecture Overview

## Introduction

This document describes the high-level architecture of the Delivery Application backend. The system follows **Hexagonal Architecture** (also known as Ports & Adapters), which ensures clean separation of concerns and enables the application to be equally driven by users, programs, automated tests, or batch scripts.

## Why Hexagonal Architecture?

### The Problem with Traditional Layered Architecture

Traditional layered architectures often lead to:
- Business logic leaking into controllers or persistence layers
- Tight coupling between frameworks and business rules
- Difficulty testing business logic in isolation
- High cost of changing infrastructure components

### Hexagonal Architecture Benefits

1. **Framework Independence**: Business logic knows nothing about Spring, databases, or HTTP
2. **Testability**: Domain and application layers can be unit tested without infrastructure
3. **Replaceability**: Swap databases, message queues, or web frameworks without touching business logic
4. **Parallel Development**: Teams can work on different layers independently

## System Layers

```
┌─────────────────────────────────────────────────────────────────┐
│                     INFRASTRUCTURE LAYER                         │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────┐ │
│  │    REST     │  │ Persistence │  │  Security   │  │Messaging│ │
│  │  Adapters   │  │  Adapters   │  │  Adapters   │  │ Adapters│ │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └────┬────┘ │
└─────────┼────────────────┼────────────────┼──────────────┼──────┘
          │                │                │              │
          ▼                ▼                ▼              ▼
┌─────────────────────────────────────────────────────────────────┐
│                     APPLICATION LAYER                            │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │                      INPUT PORTS                             ││
│  │              (Use Case Interfaces)                           ││
│  └─────────────────────────────────────────────────────────────┘│
│  ┌─────────────────────────────────────────────────────────────┐│
│  │                      USE CASES                               ││
│  │           (Application Services / Orchestration)             ││
│  └─────────────────────────────────────────────────────────────┘│
│  ┌─────────────────────────────────────────────────────────────┐│
│  │                      OUTPUT PORTS                            ││
│  │           (Repository/External Service Interfaces)           ││
│  └─────────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                       DOMAIN LAYER                               │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐              │
│  │   Entities  │  │    Value    │  │   Domain    │              │
│  │   (Models)  │  │   Objects   │  │  Services   │              │
│  └─────────────┘  └─────────────┘  └─────────────┘              │
│  ┌─────────────┐                                                 │
│  │  Repository │  (Domain Port Interfaces - NOT implementations) │
│  │  Interfaces │                                                 │
│  └─────────────┘                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## Dependency Direction Rules

### The Golden Rule

> **Dependencies ALWAYS point inward toward the domain.**

```
Infrastructure ──depends on──► Application ──depends on──► Domain
      │                              │                        │
      │                              │                        │
      ▼                              ▼                        ▼
  Knows about              Knows about Domain         Knows about
  Application              only                       NOTHING external
  and Domain
```

### What Each Layer May Reference

| Layer | May Depend On | Must NOT Depend On |
|-------|--------------|-------------------|
| **Domain** | Nothing | Application, Infrastructure, Spring, JPA, HTTP |
| **Application** | Domain | Infrastructure, Spring (except annotations), HTTP |
| **Infrastructure** | Application, Domain | N/A (outermost layer) |

### Enforcing the Rules

These dependency rules are enforced through:

1. **Package Structure**: Clear package boundaries
2. **Code Reviews**: Mandatory architectural review for cross-layer dependencies
3. **Future**: ArchUnit tests (to be implemented)

## Package Structure

```
com.company.delivery
├── domain/                    # Pure Java - NO framework dependencies
│   ├── model/                 # Aggregate roots, entities
│   ├── valueobject/           # Immutable value objects
│   ├── repository/            # Repository interfaces (domain ports)
│   └── service/               # Domain services (stateless business logic)
│
├── application/               # Orchestration layer
│   ├── usecase/               # Use case implementations
│   ├── port/                  # Port interfaces
│   │   ├── input/             # Driving ports (use case interfaces)
│   │   └── output/            # Driven ports (repository, external services)
│   └── dto/                   # Data transfer objects
│
├── infrastructure/            # Framework-dependent implementations
│   ├── web/                   # REST controllers (primary adapters)
│   ├── persistence/           # Database adapters (secondary adapters)
│   ├── security/              # Authentication/authorization adapters
│   ├── messaging/             # Event/queue adapters
│   └── config/                # Spring configuration
│
└── DeliveryApplication.java   # Spring Boot entry point
```

## Communication Flow

### Inbound Request Flow (e.g., HTTP Request)

```
HTTP Request
     │
     ▼
┌────────────────┐
│ REST Controller│  (Infrastructure - Web Adapter)
│ (Input Adapter)│
└───────┬────────┘
        │ calls
        ▼
┌────────────────┐
│   Input Port   │  (Application - Use Case Interface)
│  (Interface)   │
└───────┬────────┘
        │ implemented by
        ▼
┌────────────────┐
│   Use Case     │  (Application - Orchestration)
│(Implementation)│
└───────┬────────┘
        │ calls
        ▼
┌────────────────┐
│  Output Port   │  (Application - Repository Interface)
│  (Interface)   │
└───────┬────────┘
        │ implemented by
        ▼
┌────────────────┐
│  Persistence   │  (Infrastructure - DB Adapter)
│    Adapter     │
└────────────────┘
```

## Key Architectural Decisions

### Decision 1: Domain Models are NOT JPA Entities

**Rationale**: Domain models must remain pure Java objects. Persistence-specific annotations belong in the infrastructure layer's mapper/entity classes.

### Decision 2: Use Cases Return DTOs, Not Domain Objects

**Rationale**: Prevents domain model leakage and allows the application layer to control what data is exposed.

### Decision 3: One Use Case = One Responsibility

**Rationale**: Each use case class handles one specific business operation, following the Single Responsibility Principle.

### Decision 4: Ports are Interfaces, Adapters are Implementations

**Rationale**: This allows multiple implementations (e.g., different databases) and easy testing with mocks.

## Future Considerations

- **Event Sourcing**: The architecture supports migration to event sourcing if needed
- **CQRS**: Read and write operations can be separated at the port level
- **GraphQL**: Additional adapters can be added without touching business logic
- **Microservices**: Each bounded context can be extracted into a separate service

## Related Documents

- [Hexagonal Guide](./HEXAGONAL_GUIDE.md) - Detailed implementation guidance
- [Contributing](./CONTRIBUTING.md) - How to add features following this architecture
