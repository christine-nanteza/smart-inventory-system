package com.christine.smartinventorysystem.managers;

import com.christine.smartinventorysystem.exceptions.InsufficientStockException;
import com.christine.smartinventorysystem.exceptions.InvalidInputException;
import com.christine.smartinventorysystem.exceptions.ProductNotFoundException;
import com.christine.smartinventorysystem.models.PerishableProduct;
import com.christine.smartinventorysystem.models.Product;
import com.christine.smartinventorysystem.utils.FileHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages all product inventory operations.
 * Handles adding, updating, removing, searching and sorting products.
 */
public class InventoryManager {

    //  The main product list
    private List<Product> products = new ArrayList<>();
    private int productCounter = 1;

    //  Add product
    public void addProduct(Product product)
            throws InvalidInputException {
        if (product == null) {
            throw new InvalidInputException("product",
                    "Product cannot be null");
        }
        if (findProductByIdSilent(
                product.getProductId()) != null) {
            throw new InvalidInputException("productId",
                    "Product with ID '"
                            + product.getProductId()
                            + "' already exists");
        }
        products.add(product);

        // prevents Id duplication so new IDs never clash with existing ones
        try {
            int num = Integer.parseInt(
                    product.getProductId()
                            .replace("PRD", "")
                            .trim());
            if (num >= productCounter) {
                productCounter = num + 1;
            }
        } catch (NumberFormatException ignored) {}

        FileHandler.saveProducts(products); // auto-save
    }

    //  Generate unique product ID
    public String generateProductId() {
        return String.format("PRD%03d", productCounter++);
    }

    //  Get all products
    public List<Product> getAllProducts() {
        return products;
    }

    //  Find product by ID (throws if not found)
    public Product findProductById(String productId)
            throws ProductNotFoundException {
        for (Product p : products) {
            if (p.getProductId()
                    .equalsIgnoreCase(productId)) {
                return p;
            }
        }
        throw new ProductNotFoundException(productId);
    }

    //  Find product by ID silently (returns null)
    public Product findProductByIdSilent(String productId) {
        for (Product p : products) {
            if (p.getProductId()
                    .equalsIgnoreCase(productId)) {
                return p;
            }
        }
        return null;
    }

    //  Search products by name
    public List<Product> searchByName(String keyword) {
        List<Product> results = new ArrayList<>();
        for (Product p : products) {
            if (p.getName().toLowerCase()
                    .contains(keyword.toLowerCase())) {
                results.add(p);
            }
        }
        return results;
    }

    //  Update product details
    public void updateProduct(String productId,
                              String newName,
                              String newCategory,
                              double newPrice,
                              int newThreshold)
            throws ProductNotFoundException {
        Product product = findProductById(productId);
        product.setName(newName);
        product.setCategory(newCategory);
        product.setPrice(newPrice);
        product.setLowStockThreshold(newThreshold);
        FileHandler.saveProducts(products); // auto-save
    }

    //  Remove product
    public void removeProduct(String productId)
            throws ProductNotFoundException {
        Product product = findProductById(productId);
        products.remove(product);
        FileHandler.saveProducts(products); // auto-save
    }

    //  Restock product
    public void restockProduct(String productId, int quantity)
            throws ProductNotFoundException,
            InvalidInputException {
        if (quantity <= 0) {
            throw new InvalidInputException("quantity",
                    "Restock quantity must be positive");
        }
        Product product = findProductById(productId);
        product.increaseQuantity(quantity);
        FileHandler.saveProducts(products); // auto-save
    }

    //  Reduce stock (used during sales)
    public void reduceStock(String productId, int quantity)
            throws ProductNotFoundException,
            InsufficientStockException {
        Product product = findProductById(productId);
        if (product.getQuantity() < quantity) {
            throw new InsufficientStockException(
                    product.getName(), quantity,
                    product.getQuantity());
        }
        product.reduceQuantity(quantity);
        FileHandler.saveProducts(products); // auto-save
    }

    //  Get low stock products
    public List<Product> getLowStockProducts() {
        List<Product> lowStock = new ArrayList<>();
        for (Product p : products) {
            if (p.isLowStock()) {
                lowStock.add(p);
            }
        }
        return lowStock;
    }

    //  Get expired products
    public List<Product> getExpiredProducts() {
        List<Product> expired = new ArrayList<>();
        for (Product p : products) {
            if (p.isExpired()) {
                expired.add(p);
            }
        }
        return expired;
    }

    // Get expiring soon products
    public List<Product> getExpiringSoonProducts() {
        List<Product> expiringSoon = new ArrayList<>();
        for (Product p : products) {
            if (p instanceof PerishableProduct) {
                PerishableProduct pp =
                        (PerishableProduct) p;
                if (pp.isExpiringSoon()) {
                    expiringSoon.add(pp);
                }
            }
        }
        return expiringSoon;
    }

    //  Sort by price
    public List<Product> sortByPrice() {
        return products.stream()
                .sorted(Comparator.comparingDouble(
                        Product::getPrice))
                .collect(Collectors.toList());
    }

    //  Sort by quantity
    public List<Product> sortByQuantity() {
        return products.stream()
                .sorted(Comparator.comparingInt(
                        Product::getQuantity))
                .collect(Collectors.toList());
    }

    //  Get total stock value
    public double getTotalStockValue() {
        double total = 0;
        for (Product p : products) {
            total += p.getPrice() * p.getQuantity();
        }
        return total;
    }

    //  Get total number of products 
    public int getTotalProducts() {
        return products.size();
    }
}