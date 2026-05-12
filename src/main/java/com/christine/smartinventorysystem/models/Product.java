package com.christine.smartinventorysystem.models;

public abstract class Product{
    private String productId;
    private String name;
    private String category;
    private double price;
    private int quantity;
    private int lowStockThreshold;

    public Product(String productId, String name, String category, double price,
                   int quantity, int lowStockThreshold){
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.lowStockThreshold = lowStockThreshold;
    }

    public abstract String getProductType();
    public abstract boolean isExpired();

    public String getProductId()         { return productId; }
    public String getName()             { return name; }
    public String getCategory()         { return category; }
    public double getPrice()              { return price; }
    public int getQuantity()              { return quantity; }
    public int getLowStockThreshold()     { return lowStockThreshold; }


    public void setName(String name)                { this.name = name; }
    public void setCategory(String category)        { this.category = category; }
    public void setPrice(double price)              { this.price = price; }
    public void setQuantity(int quantity)           { this.quantity = quantity; }
    public void setLowStockThreshold(int t)  {this.lowStockThreshold = t; }

    public boolean isLowStock(){
        return quantity <= lowStockThreshold;
    }

    public void reduceQuantity(int amount){
        this.quantity -= amount;
    }

    public void increaseQuantity(int amount){
        this.quantity += amount;
    }

    @Override
    public String toString() {
        return String.format("%-10s %-20s %-15s %-15s QTY:%-6d %s",
                productId, name, category, getProductType(),
                quantity, isLowStock() ? "⚠ LOW STOCK" : "✔ OK");
    }
}