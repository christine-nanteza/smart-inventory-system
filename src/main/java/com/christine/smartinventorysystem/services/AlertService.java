package com.christine.smartinventorysystem.services;

import com.christine.smartinventorysystem.managers.InventoryManager;
import com.christine.smartinventorysystem.models.PerishableProduct;
import com.christine.smartinventorysystem.models.Product;
import com.christine.smartinventorysystem.utils.ConsoleColors;

import java.util.List;

/**
 * Service that automatically checks and displays
 * smart alerts for low stock and expiring products.
 */
public class AlertService {

    private InventoryManager inventoryManager;

    // ── Constructor ───────────────────────────────────────
    public AlertService(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    // ── Run all alerts at once ────────────────────────────
    public void runAllAlerts() {
        checkLowStockAlerts();
        checkExpiryAlerts();
    }

    // ── Low stock alerts ──────────────────────────────────
    public void checkLowStockAlerts() {
        List<Product> lowStock = inventoryManager.getLowStockProducts();

        if (lowStock.isEmpty()) {
            return; // no alerts needed
        }

        ConsoleColors.printWarning("LOW STOCK ALERTS:");
        System.out.println();

        for (Product p : lowStock) {
            ConsoleColors.printWarning(
                    p.getName() + " [" + p.getProductId() + "]"
                            + " — Only " + p.getQuantity() + " units left!"
                            + " (Threshold: " + p.getLowStockThreshold() + ")");
        }
        System.out.println();
    }

    // ── Expiry alerts ─────────────────────────────────────
    public void checkExpiryAlerts() {
        List<Product> expired = inventoryManager.getExpiredProducts();
        List<Product> expiringSoon = inventoryManager.getExpiringSoonProducts();

        // Expired products
        if (!expired.isEmpty()) {
            ConsoleColors.printError("EXPIRED PRODUCTS — Remove immediately:");
            System.out.println();
            for (Product p : expired) {
                ConsoleColors.printError(
                        p.getName() + " [" + p.getProductId() + "] — EXPIRED!");
            }
            System.out.println();
        }

        // Expiring soon products
        if (!expiringSoon.isEmpty()) {
            ConsoleColors.printWarning("EXPIRING SOON (within 7 days):");
            System.out.println();
            for (Product p : expiringSoon) {
                PerishableProduct pp = (PerishableProduct) p;
                ConsoleColors.printWarning(
                        pp.getName() + " [" + pp.getProductId() + "]"
                                + " — Expires: " + pp.getExpiryDate());
            }
            System.out.println();
        }
    }

    // ── Check single product after sale ───────────────────
    public void checkProductAfterSale(Product product) {
        if (product.isExpired()) {
            ConsoleColors.printError(
                    "WARNING: " + product.getName() + " is EXPIRED!");
        } else if (product.isLowStock()) {
            ConsoleColors.printWarning(
                    "WARNING: " + product.getName()
                            + " is running low! Only "
                            + product.getQuantity() + " units remaining.");
        }
    }
    // ── Get alert count ───────────────────────────────────
    public int getTotalAlertCount() {
        return inventoryManager.getLowStockProducts().size()
                + inventoryManager.getExpiredProducts().size()
                + inventoryManager.getExpiringSoonProducts().size();
    }
}