package com.company.delivery.domain.model;

/**
 * Example usage demonstrating the Order domain model.
 *
 * <p>This class shows how to create orders, add items, and manage the order lifecycle
 * following Domain-Driven Design principles.</p>
 */
public class OrderExample {

    public static void main(String[] args) {
        System.out.println("=== DELI ORDER SYSTEM DEMO ===\n");
        
        // Example 1: Creating a simple order
        example1_SimpleOrder();
        
        System.out.println("\n" + "=".repeat(70) + "\n");
        
        // Example 2: Complex order with multiple items
        example2_ComplexOrder();
        
        System.out.println("\n" + "=".repeat(70) + "\n");
        
        // Example 3: Order lifecycle management
        example3_OrderLifecycle();
        
        System.out.println("\n" + "=".repeat(70) + "\n");
        
        // Example 4: Error handling
        example4_ErrorHandling();
    }

    /**
     * Example 1: Creating a simple order with one sandwich.
     */
    private static void example1_SimpleOrder() {
        System.out.println("EXAMPLE 1: Simple Order\n");
        
        // Create a new order
        Order order = Order.create("John Doe");
        System.out.println("Created order: " + order);
        
        // Create a sandwich
        Sandwich sandwich = new Sandwich(8, "White", true);
        sandwich.addMeat("Turkey", false);
        sandwich.addCheese("American", false);
        sandwich.addTopping(new Ingredient("Lettuce", "vegetable", 0.0));
        sandwich.addTopping(new Ingredient("Tomato", "vegetable", 0.0));
        sandwich.addSauce(new Sauce("Mayo"));
        
        // Add sandwich to order
        order.addSandwich(sandwich);
        
        System.out.println("\nOrder after adding sandwich:");
        System.out.println(order.generateReceipt());
    }

    /**
     * Example 2: Creating a complex order with multiple items.
     */
    private static void example2_ComplexOrder() {
        System.out.println("EXAMPLE 2: Complex Order with Multiple Items\n");
        
        Order order = Order.create("Jane Smith");
        
        // Add a BLT sandwich
        Sandwich blt = new Sandwich(12, "Wheat", true);
        blt.addMeat("Bacon", true); // Extra bacon
        blt.addTopping(new Ingredient("Lettuce", "vegetable", 0.0));
        blt.addTopping(new Ingredient("Tomato", "vegetable", 0.0));
        blt.addSauce(new Sauce("Mayo"));
        order.addSandwich(blt);
        
        // Add a veggie sandwich
        Sandwich veggie = new Sandwich(4, "Rye", false);
        veggie.addCheese("Provolone", false);
        veggie.addTopping(new Ingredient("Lettuce", "vegetable", 0.0));
        veggie.addTopping(new Ingredient("Cucumber", "vegetable", 0.0));
        veggie.addTopping(new Ingredient("Onion", "vegetable", 0.0));
        veggie.addSauce(new Sauce("Ranch"));
        order.addSandwich(veggie);
        
        // Add chips
        order.addChips(new Chips("BBQ"));
        
        // Add drinks
        order.addDrink(new Drink("Coke", "large"));
        order.addDrink(new Drink("Sprite", "medium"));
        
        System.out.println("Complete order:");
        System.out.println(order.generateReceipt());
        
        System.out.println(String.format("\nOrder Summary: %d items, Total: $%.2f", 
            order.getItemCount(), order.calculateTotal()));
    }

    /**
     * Example 3: Demonstrating order lifecycle and status transitions.
     */
    private static void example3_OrderLifecycle() {
        System.out.println("EXAMPLE 3: Order Lifecycle Management\n");
        
        // Create and populate an order
        Order order = Order.create("Bob Wilson");
        
        Sandwich sandwich = new Sandwich(8, "Italian", true);
        sandwich.addMeat("Ham", false);
        sandwich.addCheese("Swiss", false);
        sandwich.addSauce(new Sauce("Mustard"));
        order.addSandwich(sandwich);
        
        order.addChips(new Chips("Classic"));
        order.addDrink(new Drink("Water", "small"));
        
        System.out.println("1. Initial status: " + order.getStatus());
        System.out.println("   " + order);
        
        // Confirm the order
        order.confirm();
        System.out.println("\n2. After confirmation: " + order.getStatus());
        System.out.println("   " + order);
        
        // Dispatch for delivery
        order.dispatch();
        System.out.println("\n3. After dispatch: " + order.getStatus());
        System.out.println("   " + order);
        
        // Mark as delivered
        order.deliver();
        System.out.println("\n4. After delivery: " + order.getStatus());
        System.out.println("   " + order);
        
        System.out.println("\n" + order.generateReceipt());
    }

    /**
     * Example 4: Demonstrating business rule violations and error handling.
     */
    private static void example4_ErrorHandling() {
        System.out.println("EXAMPLE 4: Error Handling & Business Rules\n");
        
        // Error 1: Cannot confirm empty order
        try {
            System.out.println("Test 1: Confirming empty order...");
            Order emptyOrder = Order.create("Test Customer");
            emptyOrder.confirm();
            System.out.println("   ❌ Should have thrown exception!");
        } catch (IllegalStateException e) {
            System.out.println("   ✓ Caught expected error: " + e.getMessage());
        }
        
        // Error 2: Cannot modify confirmed order
        try {
            System.out.println("\nTest 2: Modifying confirmed order...");
            Order order = Order.create("Test Customer");
            order.addChips(new Chips("BBQ"));
            order.confirm();
            order.addDrink(new Drink("Coke", "small")); // Should fail
            System.out.println("   ❌ Should have thrown exception!");
        } catch (IllegalStateException e) {
            System.out.println("   ✓ Caught expected error: " + e.getMessage());
        }
        
        // Error 3: Cannot dispatch pending order
        try {
            System.out.println("\nTest 3: Dispatching pending order...");
            Order order = Order.create("Test Customer");
            order.addChips(new Chips("BBQ"));
            order.dispatch(); // Should fail - must confirm first
            System.out.println("   Should have thrown exception!");
        } catch (IllegalStateException e) {
            System.out.println("   ✓ Caught expected error: " + e.getMessage());
        }
        
        // Error 4: Cannot cancel delivered order
        try {
            System.out.println("\nTest 4: Cancelling delivered order...");
            Order order = Order.create("Test Customer");
            order.addChips(new Chips("BBQ"));
            order.confirm();
            order.dispatch();
            order.deliver();
            order.cancel(); // Should fail
            System.out.println("  Should have thrown exception!");
        } catch (IllegalStateException e) {
            System.out.println("   ✓ Caught expected error: " + e.getMessage());
        }
        
        // Success: Cancelling pending order
        System.out.println("\nTest 5: Cancelling pending order (should succeed)...");
        Order order = Order.create("Test Customer");
        order.addChips(new Chips("BBQ"));
        System.out.println("   Status before cancel: " + order.getStatus());
        order.cancel();
        System.out.println("   Status after cancel: " + order.getStatus());
        System.out.println("   ✓ Successfully cancelled!");
    }
}
