package com.company.delivery.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Sandwich implements GetValue {

    private int breadSize; //4,8,12
    private String breadType;
    private List<Sauce> sauces;
    private boolean toasted;
    private List<Ingredient> toppings = new ArrayList<>();
    private List<Ingredient> meats = new ArrayList<>();
    private List<Ingredient> cheeses = new ArrayList<>();

    public Sandwich(int breadSize, String breadType, boolean toasted) {
        this.breadSize = breadSize;
        this.breadType = breadType;
        this.toasted = toasted;
        this.sauces = new ArrayList<>();
        this.toppings = new ArrayList<>();
        this.meats = new ArrayList<>();
        this.cheeses = new ArrayList<>();
    }

    public void addMeat(String ingredientName, boolean extra) {
        double basePrice;
        double extraCharge;

        switch (breadSize) {
            case 4 -> {
                basePrice = 1.00;
                extraCharge = 0.50;
            }
            case 8 -> {
                basePrice = 1.75;
                extraCharge = 1.00;
            }
            case 12 -> {
                basePrice = 2.25;
                extraCharge = 1.50;
            }
            default -> {
                basePrice = 1.00;
                extraCharge = 0.50;
            }
        }

        double finalPrice;
        if (extra) {
            finalPrice = basePrice + extraCharge;
        } else {
            finalPrice = basePrice;
        }
        meats.add(new Ingredient(ingredientName, "meat", finalPrice, extra));
    }

    public void addCheese(String ingredientName, boolean extra) {
        double basePrice;
        double extraCharge;

        switch (breadSize) {
            case 4 -> {
                basePrice = 0.75;
                extraCharge = 0.30;
            }
            case 8 -> {
                basePrice = 1.50;
                extraCharge = 0.60;
            }
            case 12 -> {
                basePrice = 2.25;
                extraCharge = 0.90;
            }
            default -> {
                basePrice = 0.75;
                extraCharge = 0.30;
            }
        }

        double finalPrice;
        if (extra) {
            finalPrice = basePrice + extraCharge;
        } else {
            finalPrice = basePrice;
        }

        cheeses.add(new Ingredient(ingredientName, "Cheese", finalPrice, extra));
    }

    public void addTopping(Ingredient ingredient) {
        toppings.add(ingredient);
    }

    public void addSauce(Sauce sauce) {
        sauces.add(sauce);
    }

    @Override
    public double getValue() {
        double basePrice;

        switch (breadSize) {
            case 4 -> basePrice = 3.99;
            case 8 -> basePrice = 5.99;
            case 12 -> basePrice = 7.99;
            default -> basePrice = 0;
        }

        // use streams to sum all ingredient values
        double meatsTotal = meats.stream().mapToDouble(Ingredient::getValue).sum();
        double cheesesTotal = cheeses.stream().mapToDouble(Ingredient::getValue).sum();
        double toppingsTotal = toppings.stream().mapToDouble(Ingredient::getValue).sum();

        return basePrice + meatsTotal + cheesesTotal + toppingsTotal;
    }

    public String getSummary() {
        String meatsList = meats.stream()
                .map(Ingredient::getIngredientName)
                .collect(Collectors.joining(", ", "[", "]"));

        String cheeseList = cheeses.stream()
                .map(Ingredient::getIngredientName)
                .collect(Collectors.joining(", ", "[", "]"));

        String toppingList = toppings.stream()
                .map(Ingredient::getIngredientName)
                .collect(Collectors.joining(", ", "[", "]"));

        String sauceList = sauces.stream()
                .map(Sauce::getSauceName)
                .collect(Collectors.joining(", ", "[", "]"));

        String toastedText = "";
        if (toasted) {
            toastedText = " (Toasted)";
        }

        return String.format("""
                        Sandwich Summary:
                        -----------------
                        Bread: %d" %s%s
                        Meats: %s
                        Cheeses: %s
                        Toppings: %s
                        Sauces: %s
                        Total Price: $%.2f
                        """,
                breadSize, breadType, toastedText,
                meatsList, cheeseList, toppingList, sauceList, getValue());
    }
}
