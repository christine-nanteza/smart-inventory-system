package com.christine.smartinventorysystem.managers;

import com.christine.smartinventorysystem.exceptions.InsufficientStockException;
import com.christine.smartinventorysystem.exceptions.InvalidInputException;
import com.christine.smartinventorysystem.exceptions.ProductNotFoundException;
import com.christine.smartinventorysystem.models.Product;
import com.christine.smartinventorysystem.models.Sale;
import com.christine.smartinventorysystem.utils.FileHandler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages all sales transactions.
 * Handles processing sales, refunds and sales history.
 */
public class SalesManager {

    //  Sales list + counter
    private List<Sale> sales = new ArrayList<>();
    private int saleCounter = 1;
    private InventoryManager inventoryManager;

    //  Constructor
    public SalesManager(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    //  Generate unique sale ID
    public String generateSaleId() {
        return String.format("SAL%03d", saleCounter++);
    }

    //  Process a sale
    public Sale processSale(String productId, int quantity,
                            String cashierName)
            throws ProductNotFoundException,
            InsufficientStockException,
            InvalidInputException {

        if (quantity <= 0) {
            throw new InvalidInputException("quantity",
                    "Quantity must be greater than zero");
        }

        Product product =
                inventoryManager.findProductById(productId);

        if (product.isExpired()) {
            throw new InvalidInputException("product",
                    "Cannot sell expired product: "
                            + product.getName());
        }

        if (product.getQuantity() < quantity) {
            throw new InsufficientStockException(
                    product.getName(), quantity,
                    product.getQuantity());
        }

        inventoryManager.reduceStock(productId, quantity);

        Sale sale = new Sale(
                generateSaleId(),
                productId,
                product.getName(),
                quantity,
                product.getPrice(),
                cashierName);
        sales.add(sale);
        FileHandler.saveSales(sales); // auto-save
        return sale;
    }

    //  Restore a sale from file (no stock deduction)
    public void restoreSale(Sale sale) {
        sales.add(sale);
        // Update counter so new IDs don't clash
        try {
            int num = Integer.parseInt(
                    sale.getSaleId()
                            .replace("SAL", "")
                            .trim());
            if (num >= saleCounter) {
                saleCounter = num + 1;
            }
        } catch (NumberFormatException ignored) {}
    }

    // Process refund
    public void processRefund(String saleId)
            throws InvalidInputException,
            ProductNotFoundException {

        Sale sale = findSaleById(saleId);
        if (sale == null) {
            throw new InvalidInputException("saleId",
                    "Sale with ID '" + saleId
                            + "' not found");
        }

        if (sale.isRefunded()) {
            throw new InvalidInputException("saleId",
                    "Sale '" + saleId
                            + "' has already been refunded");
        }

        Product product = inventoryManager
                .findProductById(sale.getProductId());
        product.increaseQuantity(sale.getQuantitySold());
        sale.setRefunded(true);
        FileHandler.saveSales(sales); // auto-save
    }

    //  Find sale by ID
    public Sale findSaleById(String saleId) {
        for (Sale s : sales) {
            if (s.getSaleId().equalsIgnoreCase(saleId)) {
                return s;
            }
        }
        return null;
    }

    //  Get all sales
    public List<Sale> getAllSales() {
        return sales;
    }

    //  Get today's sales
    public List<Sale> getTodaysSales() {
        List<Sale> todaysSales = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (Sale s : sales) {
            if (s.getSaleDateTime()
                    .toLocalDate().equals(today)) {
                todaysSales.add(s);
            }
        }
        return todaysSales;
    }

    //  Get total revenue
    public double getTotalRevenue() {
        double total = 0;
        for (Sale s : sales) {
            if (!s.isRefunded()) {
                total += s.getTotalAmount();
            }
        }
        return total;
    }

    //  Get today's revenue
    public double getTodaysRevenue() {
        double total = 0;
        for (Sale s : getTodaysSales()) {
            if (!s.isRefunded()) {
                total += s.getTotalAmount();
            }
        }
        return total;
    }

    //  Get total number of sales
    public int getTotalSales() {
        return sales.size();
    }

    // Generate receipt
    public String generateReceipt(Sale sale) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("\n╔══════════════════════════════════════════╗\n");
        receipt.append("║         SMART INVENTORY SYSTEM           ║\n");
        receipt.append("║              SALES RECEIPT               ║\n");
        receipt.append("╠══════════════════════════════════════════╣\n");
        receipt.append(String.format(
                "║ Sale ID  : %-30s║\n",
                sale.getSaleId()));
        receipt.append(String.format(
                "║ Date     : %-30s║\n",
                sale.getSaleDateTime().toLocalDate()));
        receipt.append(String.format(
                "║ Time     : %-30s║\n",
                sale.getSaleDateTime()
                        .toLocalTime().withNano(0)));
        receipt.append(String.format(
                "║ Cashier  : %-30s║\n",
                sale.getCashierName()));
        receipt.append("╠══════════════════════════════════════════╣\n");
        receipt.append(String.format(
                "║ Product  : %-30s║\n",
                sale.getProductName()));
        receipt.append(String.format(
                "║ Quantity : %-30d║\n",
                sale.getQuantitySold()));
        receipt.append(String.format(
                "║ Price    : UGX %-27.0f║\n",
                sale.getPricePerUnit()));
        receipt.append("╠══════════════════════════════════════════╣\n");
        receipt.append(String.format(
                "║ TOTAL    : UGX %-27.0f║\n",
                sale.getTotalAmount()));
        receipt.append("╚══════════════════════════════════════════╝\n");
        return receipt.toString();
    }
}