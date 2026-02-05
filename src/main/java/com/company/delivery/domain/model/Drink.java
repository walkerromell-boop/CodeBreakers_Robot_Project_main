package com.company.delivery.domain.model;

public class Drink implements GetValue {
    private String drinkName;
    private String drinkSize;
    private double drinkPrice;

    public Drink(String drinkName, String drinkSize) {
        this.drinkName = drinkName;
        this.drinkSize = drinkSize.toLowerCase();
        this.drinkPrice = calculatePrice();
    }

    private double calculatePrice() {
        return switch (drinkSize) {
            case "small" -> 2.00;
            case "medium" -> 2.50;
            case "large" -> 3.00;
            default -> 2.50; // default to medium
        };
    }

    public double getDrinkPrice() {
        return drinkPrice;
    }

    public String getDrinkName() {
        return drinkName;
    }

    public String getDrinkSize() {
        return drinkSize;
    }

    @Override
    public double getValue() {
        return drinkPrice;
    }

    @Override
    public String toString() {
        String price = String.format("%.2f", drinkPrice);
        return drinkSize + " " + drinkName + " ($" + price + ")";
    }
}
