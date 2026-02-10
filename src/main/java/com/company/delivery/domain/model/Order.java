package com.company.delivery.domain.model;

import com.company.delivery.domain.valueobject.OrderId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents an order in the delivery system.
 *
 * <p>This is an aggregate root for the Order bounded context. It encapsulates
 * all business rules related to order lifecycle management.</p>
 *
 * <h2>Business Rules</h2>
 * <ul>
 *   <li>An order must have at least one item</li>
 *   <li>Only pending orders can be confirmed</li>
 *   <li>Only confirmed orders can be dispatched</li>
 *   <li>Cancelled orders cannot be modified</li>
 *   <li>Order total is calculated from all items</li>
 * </ul>
 *
 * <h2>Architectural Notes</h2>
 * <p>This class is a pure Java object with NO framework dependencies.
 * Persistence is handled by a separate JPA entity in the infrastructure layer.</p>
 */
public class Order {

    private final OrderId id;
    private final String customerName;
    private final List<OrderItem> items;
    private OrderStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    /**
     * Private constructor - use factory methods to create orders.
     */
    private Order(OrderId id, String customerName, List<OrderItem> items, 
                  OrderStatus status, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "Order ID cannot be null");
        this.customerName = validateCustomerName(customerName);
        this.items = new ArrayList<>(Objects.requireNonNull(items, "Items cannot be null"));
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created timestamp cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated timestamp cannot be null");
        

    }

    /**
     * Creates a new order with pending status.
     *
     * @param customerName the name of the customer placing the order
     * @return a new Order instance
     */
    public static Order create(String customerName) {
        Instant now = Instant.now();
        return new Order(
            OrderId.generate(),
            customerName,
            new ArrayList<>(),
            OrderStatus.PENDING,
            now,
            now
        );
    }

    /**
     * Reconstructs an order from persistence (used by repository adapters).
     *
     * @param id the order ID
     * @param customerName the customer name
     * @param items the order items
     * @param status the order status
     * @param createdAt the creation timestamp
     * @param updatedAt the last update timestamp
     * @return the reconstructed Order
     */
    public static Order reconstitute(OrderId id, String customerName, List<OrderItem> items,
                                     OrderStatus status, Instant createdAt, Instant updatedAt) {
        return new Order(id, customerName, items, status, createdAt, updatedAt);
    }

    // ========================================================================
    // Business Behavior Methods
    // ========================================================================

    /**
     * Adds a sandwich to the order.
     *
     * @param sandwich the sandwich to add
     * @throws IllegalArgumentException if sandwich is null
     */
    public void addSandwich(Sandwich sandwich) {
        Objects.requireNonNull(sandwich, "Sandwich cannot be null");
        ensureOrderCanBeModified();
        
        OrderItem item = OrderItem.forSandwich(sandwich);
        items.add(item);
        updateTimestamp();
    }

    /**
     * Adds chips to the order.
     *
     * @param chips the chips to add
     * @throws IllegalArgumentException if chips is null
     */
    public void addChips(Chips chips) {
        Objects.requireNonNull(chips, "Chips cannot be null");
        ensureOrderCanBeModified();
        
        OrderItem item = OrderItem.forChips(chips);
        items.add(item);
        updateTimestamp();
    }

    /**
     * Adds a drink to the order.
     *
     * @param drink the drink to add
     * @throws IllegalArgumentException if drink is null
     */
    public void addDrink(Drink drink) {
        Objects.requireNonNull(drink, "Drink cannot be null");
        ensureOrderCanBeModified();
        
        OrderItem item = OrderItem.forDrink(drink);
        items.add(item);
        updateTimestamp();
    }

    /**
     * Removes an item from the order by index.
     *
     * @param index the index of the item to remove
     * @throws IllegalStateException if order cannot be modified
     * @throws IndexOutOfBoundsException if index is invalid
     */
    public void removeItem(int index) {
        ensureOrderCanBeModified();
        items.remove(index);
        updateTimestamp();
    }

    /**
     * Confirms the order, transitioning it from PENDING to CONFIRMED.
     *
     * @throws IllegalStateException if order is not pending or has no items
     */
    public void confirm() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException(
                String.format("Cannot confirm order %s - current status is %s", id, status)
            );
        }
        
        validateOrderHasItems();
        
        status = OrderStatus.CONFIRMED;
        updateTimestamp();
    }

    /**
     * Marks the order as dispatched for delivery.
     *
     * @throws IllegalStateException if order is not confirmed
     */
    public void dispatch() {
        if (status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException(
                String.format("Cannot dispatch order %s - current status is %s", id, status)
            );
        }
        
        status = OrderStatus.DISPATCHED;
        updateTimestamp();
    }

    /**
     * Marks the order as delivered.
     *
     * @throws IllegalStateException if order is not dispatched
     */
    public void deliver() {
        if (status != OrderStatus.DISPATCHED) {
            throw new IllegalStateException(
                String.format("Cannot deliver order %s - current status is %s", id, status)
            );
        }
        
        status = OrderStatus.DELIVERED;
        updateTimestamp();
    }

    /**
     * Cancels the order.
     *
     * @throws IllegalStateException if order is already delivered
     */
    public void cancel() {
        if (status == OrderStatus.DELIVERED) {
            throw new IllegalStateException(
                String.format("Cannot cancel delivered order %s", id)
            );
        }
        
        status = OrderStatus.CANCELLED;
        updateTimestamp();
    }

    /**
     * Calculates the total price of the order.
     *
     * @return the total price as a double
     */
    public double calculateTotal() {
        return items.stream()
            .mapToDouble(OrderItem::getPrice)
            .sum();
    }

    // ========================================================================
    // Validation Methods
    // ========================================================================

    private String validateCustomerName(String customerName) {
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be null or empty");
        }
        return customerName.trim();
    }

    private void validateOrderHasItems() {
        if (items.isEmpty()) {
            throw new IllegalStateException("Order must have at least one item");
        }
    }

    private void ensureOrderCanBeModified() {
        if (status == OrderStatus.CANCELLED) {
            throw new IllegalStateException(
                String.format("Cannot modify cancelled order %s", id)
            );
        }
        if (status == OrderStatus.DELIVERED) {
            throw new IllegalStateException(
                String.format("Cannot modify delivered order %s", id)
            );
        }
    }

    private void updateTimestamp() {
        this.updatedAt = Instant.now();
    }

    // ========================================================================
    // Getters (No setters - state changes through behavior methods)
    // ========================================================================

    public OrderId getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    /**
     * Returns an unmodifiable view of the order items.
     *
     * @return immutable list of items
     */
    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public int getItemCount() {
        return items.size();
    }

    // ========================================================================
    // Display Methods
    // ========================================================================

    /**
     * Generates a formatted receipt for the order.
     *
     * @return formatted receipt string
     */
    public String generateReceipt() {
        StringBuilder receipt = new StringBuilder();
        receipt.append("=".repeat(50)).append("\n");
        receipt.append(String.format("ORDER #%s\n", id.value().toString().substring(0, 8).toUpperCase()));
        receipt.append("=".repeat(50)).append("\n");
        receipt.append(String.format("Customer: %s\n", customerName));
        receipt.append(String.format("Status: %s\n", status));
        receipt.append(String.format("Date: %s\n", createdAt));
        receipt.append("-".repeat(50)).append("\n");
        receipt.append("ITEMS:\n");
        receipt.append("-".repeat(50)).append("\n");
        
        for (int i = 0; i < items.size(); i++) {
            OrderItem item = items.get(i);
            receipt.append(String.format("%d. %s - $%.2f\n", 
                i + 1, item.getDescription(), item.getPrice()));
        }
        
        receipt.append("-".repeat(50)).append("\n");
        receipt.append(String.format("TOTAL: $%.2f\n", calculateTotal()));
        receipt.append("=".repeat(50)).append("\n");
        
        return receipt.toString();
    }

    @Override
    public String toString() {
        return String.format("Order{id=%s, customer='%s', items=%d, total=$%.2f, status=%s}",
            id, customerName, items.size(), calculateTotal(), status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // ========================================================================
    // Inner Class: OrderItem
    // ========================================================================

    /**
     * Represents a single item in an order.
     * This is an entity within the Order aggregate.
     */
    public static class OrderItem {
        private final String description;
        private final double price;
        private final OrderItemType type;
        private final Object item; // Reference to the actual item (Sandwich, Chips, or Drink)

        private OrderItem(String description, double price, OrderItemType type, Object item) {
            this.description = Objects.requireNonNull(description, "Description cannot be null");
            this.price = price;
            this.type = Objects.requireNonNull(type, "Type cannot be null");
            this.item = Objects.requireNonNull(item, "Item cannot be null");
            
            if (price < 0) {
                throw new IllegalArgumentException("Price cannot be negative");
            }
        }

        public static OrderItem forSandwich(Sandwich sandwich) {
            return new OrderItem(
                sandwich.getSummary(),
                sandwich.getValue(),
                OrderItemType.SANDWICH,
                sandwich
            );
        }

        public static OrderItem forChips(Chips chips) {
            return new OrderItem(
                chips.toString(),
                chips.getChipPrice(),
                OrderItemType.CHIPS,
                chips
            );
        }

        public static OrderItem forDrink(Drink drink) {
            return new OrderItem(
                drink.toString(),
                drink.getDrinkPrice(),
                OrderItemType.DRINK,
                drink
            );
        }

        public String getDescription() {
            return description;
        }

        public double getPrice() {
            return price;
        }

        public OrderItemType getType() {
            return type;
        }

        public Object getItem() {
            return item;
        }

        @Override
        public String toString() {
            return String.format("%s ($%.2f)", description, price);
        }
    }

    /**
     * Enum for order item types.
     */
    public enum OrderItemType {
        SANDWICH,
        CHIPS,
        DRINK
    }
}
