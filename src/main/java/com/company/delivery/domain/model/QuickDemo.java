package com.company.delivery.domain.model;

/**
 * QUICK DEMO - Working Order System Demo
 * 
 * Run this after fixing the Order.java validation issue!
 */
public class QuickDemo {

    public static void main(String[] args) {
        System.out.println(" DELI ORDER SYSTEM - QUICK DEMO");
        System.out.println("=" .repeat(60));
        
        try {
            // Demo 1: Simple order
            simpleOrder();
            
            System.out.println("\n" + "=".repeat(60) + "\n");
            
            // Demo 2: Order lifecycle
            orderLifecycle();
            
            System.out.println("\n DEMO COMPLETED SUCCESSFULLY!");
            
        } catch (Exception e) {
            System.err.println("\n DEMO FAILED!");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Demo 1: Create a simple order with sandwich, chips, and drink
     */
    private static void simpleOrder() {
        System.out.println(" DEMO 1: Creating a Simple Order\n");
        
        // Step 1: Create order
        Order order = Order.create("John Doe");
        System.out.println("✓ Created order for: " + order.getCustomerName());
        
        // Step 2: Build a sandwich
        Sandwich sandwich = new Sandwich(8, "White", true);
        sandwich.addMeat("Turkey", false);
        sandwich.addCheese("American", false);
        sandwich.addTopping(new Ingredient("Lettuce", "vegetable", 0.0));
        sandwich.addTopping(new Ingredient("Tomato", "vegetable", 0.0));
        sandwich.addSauce(new Sauce("Mayo"));
        System.out.println("✓ Built 8\" White sandwich with Turkey");
        
        // Step 3: Add items to order
        order.addSandwich(sandwich);
        order.addChips(new Chips("BBQ"));
        order.addDrink(new Drink("Coke", "large"));
        
        System.out.println("✓ Added " + order.getItemCount() + " items to order");
        System.out.println("✓ Order total: $" + String.format("%.2f", order.calculateTotal()));
        
        // Step 4: Display receipt
        System.out.println("\n" + order.generateReceipt());
    }

    /**
     * Demo 2: Show order lifecycle (PENDING → CONFIRMED → DISPATCHED → DELIVERED)
     */
    private static void orderLifecycle() {
        System.out.println(" DEMO 2: Order Lifecycle Management\n");
        
        // Create order with items
        Order order = Order.create("Jane Smith");
        
        Sandwich sandwich = new Sandwich(12, "Wheat", true);
        sandwich.addMeat("Ham", false);
        sandwich.addCheese("Swiss", false);
        sandwich.addSauce(new Sauce("Mustard"));
        
        order.addSandwich(sandwich);
        order.addChips(new Chips("Classic"));
        order.addDrink(new Drink("Water", "small"));
        
        // Show status transitions
        System.out.println("  Status: " + order.getStatus() + " (Order created)");
        
        order.confirm();
        System.out.println(" Status: " + order.getStatus() + " (Order confirmed)");
        
        order.dispatch();
        System.out.println("  Status: " + order.getStatus() + " (Order dispatched)");
        
        order.deliver();
        System.out.println("  Status: " + order.getStatus() + " (Order delivered)");
        
        System.out.println("\n✓ Order lifecycle completed!");
        System.out.println("✓ Final total: $" + String.format("%.2f", order.calculateTotal()));
    }
}
