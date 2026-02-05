package com.company.delivery.domain.model;

/**
 * Interface for items that have a calculable price value.
 *
 * <p>This interface is implemented by all order items (Sandwich, Chips, Drink)
 * to provide a uniform way to calculate their prices.</p>
 */
public interface GetValue {
    
    /**
     * Calculates and returns the price value of this item.
     *
     * @return the price as a double
     */
    double getValue();
}
