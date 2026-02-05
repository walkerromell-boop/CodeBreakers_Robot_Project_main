package com.company.delivery.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * Command object for creating a new order.
 *
 * <p>This record encapsulates all input parameters required to create an order.
 * It includes the customer name and all items they want to order.</p>
 *
 * <h2>Validation</h2>
 * <p>Uses Jakarta Bean Validation to ensure data integrity at the application boundary.</p>
 */
public record CreateOrderCommand(
    
    @NotBlank(message = "Customer name is required")
    String customerName,
    
    @Valid
    List<SandwichDto> sandwiches,
    
    @Valid
    List<ChipsDto> chips,
    
    @Valid
    List<DrinkDto> drinks
    
) {
    
    /**
     * DTO for a sandwich within an order.
     */
    public record SandwichDto(
        
        @NotNull(message = "Bread size is required")
        @Positive(message = "Bread size must be positive (4, 8, or 12)")
        Integer breadSize,
        
        @NotBlank(message = "Bread type is required")
        String breadType,
        
        @NotNull(message = "Toasted preference is required")
        Boolean toasted,
        
        List<MeatDto> meats,
        List<CheeseDto> cheeses,
        List<ToppingDto> toppings,
        List<String> sauces
        
    ) {}
    
    /**
     * DTO for meat on a sandwich.
     */
    public record MeatDto(
        @NotBlank(message = "Meat name is required")
        String name,
        
        @NotNull(message = "Extra meat preference is required")
        Boolean extra
    ) {}
    
    /**
     * DTO for cheese on a sandwich.
     */
    public record CheeseDto(
        @NotBlank(message = "Cheese name is required")
        String name,
        
        @NotNull(message = "Extra cheese preference is required")
        Boolean extra
    ) {}
    
    /**
     * DTO for toppings on a sandwich.
     */
    public record ToppingDto(
        @NotBlank(message = "Topping name is required")
        String name,
        
        @NotBlank(message = "Topping category is required")
        String category
    ) {}
    
    /**
     * DTO for chips in an order.
     */
    public record ChipsDto(
        @NotBlank(message = "Chip name is required")
        String chipName
    ) {}
    
    /**
     * DTO for a drink in an order.
     */
    public record DrinkDto(
        @NotBlank(message = "Drink name is required")
        String drinkName,
        
        @NotBlank(message = "Drink size is required")
        String drinkSize
    ) {}
}
