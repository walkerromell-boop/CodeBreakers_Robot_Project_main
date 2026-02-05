package com.company.delivery.application.usecase;

import com.company.delivery.application.dto.CreateOrderCommand;
import com.company.delivery.application.dto.OrderDto;
import com.company.delivery.application.port.input.CreateOrderUseCase;
import com.company.delivery.domain.model.*;
import com.company.delivery.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * Implementation of the create order use case.
 *
 * <p>This application service orchestrates the order creation process:
 * <ol>
 *   <li>Validates the command input</li>
 *   <li>Creates domain objects (Order, Sandwich, etc.)</li>
 *   <li>Delegates business rules to the domain</li>
 *   <li>Persists through repository port</li>
 *   <li>Maps result to DTO for the caller</li>
 * </ol>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *   <li><strong>Orchestration</strong> - Coordinates domain objects and infrastructure</li>
 *   <li><strong>Transaction Boundary</strong> - Defines where transactions start/end</li>
 *   <li><strong>Mapping</strong> - Converts between DTOs and domain objects</li>
 * </ul>
 *
 * <h2>What This Service Does NOT Do</h2>
 * <ul>
 *   <li>❌ Business logic (that's in Order domain class)</li>
 *   <li>❌ HTTP handling (that's in controllers)</li>
 *   <li>❌ Database queries (that's in repositories)</li>
 * </ul>
 */
@Service
@Transactional
public class CreateOrderUseCaseImpl implements CreateOrderUseCase {

    private final OrderRepository orderRepository;

    /**
     * Constructs the use case with required dependencies.
     *
     * @param orderRepository the order repository port
     */
    public CreateOrderUseCaseImpl(OrderRepository orderRepository) {
        this.orderRepository = Objects.requireNonNull(orderRepository, 
            "OrderRepository cannot be null");
    }

    /**
     * Executes the create order use case.
     *
     * <p>This method orchestrates the creation of a new order by:
     * <ol>
     *   <li>Creating an Order domain object</li>
     *   <li>Adding items based on the command</li>
     *   <li>Persisting the order</li>
     *   <li>Returning a DTO representation</li>
     * </ol>
     *
     * @param command the order creation command containing customer info and items
     * @return the created order as a DTO
     * @throws IllegalArgumentException if command is null or invalid
     */
    @Override
    public OrderDto execute(CreateOrderCommand command) {
        Objects.requireNonNull(command, "CreateOrderCommand cannot be null");
        
        // Step 1: Create the Order aggregate root
        Order order = Order.create(command.customerName());
        
        // Step 2: Add sandwiches to the order
        if (command.sandwiches() != null) {
            command.sandwiches().forEach(sandwichDto -> {
                Sandwich sandwich = buildSandwich(sandwichDto);
                order.addSandwich(sandwich);
            });
        }
        
        // Step 3: Add chips to the order
        if (command.chips() != null) {
            command.chips().forEach(chipsDto -> {
                Chips chips = new Chips(chipsDto.chipName());
                order.addChips(chips);
            });
        }
        
        // Step 4: Add drinks to the order
        if (command.drinks() != null) {
            command.drinks().forEach(drinkDto -> {
                Drink drink = new Drink(drinkDto.drinkName(), drinkDto.drinkSize());
                order.addDrink(drink);
            });
        }
        
        // Step 5: Persist the order through the repository port
        orderRepository.save(order);
        
        // Step 6: Convert domain object to DTO and return
        return OrderDto.from(order);
    }

    /**
     * Builds a Sandwich domain object from a DTO.
     *
     * @param dto the sandwich DTO
     * @return the constructed Sandwich
     */
    private Sandwich buildSandwich(CreateOrderCommand.SandwichDto dto) {
        Sandwich sandwich = new Sandwich(
            dto.breadSize(),
            dto.breadType(),
            dto.toasted()
        );
        
        // Add meats
        if (dto.meats() != null) {
            dto.meats().forEach(meat -> 
                sandwich.addMeat(meat.name(), meat.extra())
            );
        }
        
        // Add cheeses
        if (dto.cheeses() != null) {
            dto.cheeses().forEach(cheese -> 
                sandwich.addCheese(cheese.name(), cheese.extra())
            );
        }
        
        // Add toppings
        if (dto.toppings() != null) {
            dto.toppings().forEach(topping -> {
                Ingredient ingredient = new Ingredient(
                    topping.name(),
                    topping.category(),
                    0.0  // Toppings are free
                );
                sandwich.addTopping(ingredient);
            });
        }
        
        // Add sauces
        if (dto.sauces() != null) {
            dto.sauces().forEach(sauceName -> 
                sandwich.addSauce(new Sauce(sauceName))
            );
        }
        
        return sandwich;
    }
}
