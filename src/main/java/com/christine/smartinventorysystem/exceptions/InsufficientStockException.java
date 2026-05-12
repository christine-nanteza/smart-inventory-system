package com.christine.smartinventorysystem.exceptions;

public class InsufficientStockException extends Exception {

    private String productName;
    private int requested;
    private int available;

    public InsufficientStockException(String productName, int requested, int available) {
        super("Insufficient stock for '" + productName + "'. Requested: " + requested + ", Available: " + available);
        this.productName = productName;
        this.requested = requested;
        this.available = available;
    }

    public String getProductName() {
        return productName;
    }

    public int getRequested() {
        return requested;
    }

    public int getAvailable() {
        return available;
    }
}