package com.company.delivery.domain.model;

/**
 * Domain Model Package - Entities and Aggregate Roots.
 *
 * <h2>Purpose</h2>
 * <p>This package contains all domain entities and aggregate roots. An entity is a domain
 * object that has a distinct identity that runs through time and different representations.</p>
 *
 * <h2>What Belongs Here</h2>
 * <ul>
 *   <li>Aggregate roots (e.g., Order, Customer, Delivery)</li>
 *   <li>Entities within aggregates (e.g., OrderItem within Order)</li>
 *   <li>Business behavior methods</li>
 *   <li>Invariant validation</li>
 * </ul>
 *
 * <h2>What Does NOT Belong Here</h2>
 * <ul>
 *   <li>JPA/Hibernate annotations</li>
 *   <li>Spring annotations</li>
 *   <li>Persistence logic</li>
 *   <li>DTOs or API contracts</li>
 * </ul>
 *
 * <h2>Design Rules</h2>
 * <ul>
 *   <li>Entities must have a unique identifier</li>
 *   <li>Business logic belongs in entities, not services</li>
 *   <li>Entities must validate their own invariants</li>
 *   <li>State changes happen through behavior methods, not setters</li>
 * </ul>
 *
 * @see com.company.delivery.domain.valueobject Value Objects
 * @see com.company.delivery.domain.repository Repository Ports
 */
