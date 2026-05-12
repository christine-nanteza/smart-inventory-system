package com.christine.smartinventorysystem.exceptions;

public class ProductNotFoundException extends Exception {

    private String productId;

    public ProductNotFoundException(String productId) {
        super("Product with ID '" + productId + "' was not found in the system.");
        this.productId = productId;
    }

    public String getProductId() {
        return productId;
    }
}