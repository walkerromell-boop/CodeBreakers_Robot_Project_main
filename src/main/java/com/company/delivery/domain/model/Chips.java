package com.company.delivery.domain.model;

public class Chips implements GetValue {
    private String chipName;
    private double chipPrice;

    public Chips(String chipName) {
        this.chipName = chipName;
        this.chipPrice = 1.50; // all chips the same price
    }

    public String getChipName() {
        return chipName;
    }

    public double getChipPrice() {
        return chipPrice;
    }

    @Override
    public double getValue() {
        return chipPrice;
    }

    @Override
    public String toString() {
        String price = String.format("%.2f", chipPrice);
        return chipName + " Chips ($" + price + ")";
    }
}
