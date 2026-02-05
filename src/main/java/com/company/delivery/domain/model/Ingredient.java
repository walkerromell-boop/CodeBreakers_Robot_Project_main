package com.company.delivery.domain.model;

public class Ingredient implements GetValue {
    private String ingredientName;
    private String category;
    private double price;
    private boolean isExtra;

    public Ingredient(String ingredientName, String category, double price, boolean isExtra) {
        this.ingredientName = ingredientName;
        this.category = category;
        this.price = price;
        this.isExtra = isExtra;
    }

    public Ingredient(String ingredientName, String category, double price) {
        this.ingredientName = ingredientName;
        this.category = category;
        this.price = price;
        this.isExtra = false;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public double getPrice() {
        return price;
    }

    public boolean isExtra() {
        return isExtra;
    }

    public void setExtra(boolean extra) {
        isExtra = extra;
    }

    public String getCategory() {
        return category;
    }

    public double getPriceForSize(int size) {
        // Return the extra charge depending on bread size
        double extraCharge = 0.0;
        if (isExtra) {
            switch (category.toLowerCase()) {
                case "meat" -> {
                    if (size == 4) extraCharge = 0.50;
                    else if (size == 8) extraCharge = 1.00;
                    else if (size == 12) extraCharge = 1.50;
                }
                case "cheese" -> {
                    if (size == 4) extraCharge = 0.30;
                    else if (size == 8) extraCharge = 0.60;
                    else if (size == 12) extraCharge = 0.90;
                }
            }
        }
        return price + extraCharge;
    }

    @Override
    public double getValue() {
        return price;
    }

    @Override
    public String toString() {
        return ingredientName + (isExtra ? " (extra)" : "");
    }
}
