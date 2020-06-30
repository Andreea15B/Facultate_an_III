package com.example.andreea.shoppingassistant;

public class Product {
    public String name, category, amountType;
    public int amount;

    Product(String name, String category, int amount, String amountType) {
        this.name = name;
        this.category = category;
        this.amount = amount;
        this.amountType = amountType;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getAmount() {
        return amount;
    }

    public String getAmountType() {
        return amountType;
    }
}
