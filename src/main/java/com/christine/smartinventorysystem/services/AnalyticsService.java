package com.christine.smartinventorysystem.services;

import com.christine.smartinventorysystem.managers.InventoryManager;
import com.christine.smartinventorysystem.managers.SalesManager;
import com.christine.smartinventorysystem.models.Product;
import com.christine.smartinventorysystem.models.Sale;
import com.christine.smartinventorysystem.utils.ConsoleColors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Analytics engine that provides business insights.
 * Answers key business questions about sales and stock.
 */
public class AnalyticsService {

    private InventoryManager inventoryManager;
    private SalesManager salesManager;

    // ── Constructor ───────────────────────────────────────
    public AnalyticsService(InventoryManager inventoryManager,
                            SalesManager salesManager) {
        this.inventoryManager = inventoryManager;
        this.salesManager = salesManager;
    }

    // ── Best selling product ──────────────────────────────
    public Product getBestSellingProduct() {
        Map<String, Integer> salesCount = new HashMap<>();

        for (Sale s : salesManager.getAllSales()) {
            if (!s.isRefunded()) {
                salesCount.put(s.getProductId(),
                        salesCount.getOrDefault(s.getProductId(), 0)
                                + s.getQuantitySold());
            }
        }

        String bestId = null;
        int bestCount = 0;

        for (Map.Entry<String, Integer> entry : salesCount.entrySet()) {
            if (entry.getValue() > bestCount) {
                bestCount = entry.getValue();
                bestId = entry.getKey();
            }
        }

        if (bestId == null) return null;

        try {
            return inventoryManager.findProductById(bestId);
        } catch (Exception e) {
            return null;
        }
    }

    // ── Dead stock (never sold) ───────────────────────────
    public List<Product> getDeadStockProducts() {
        List<Product> deadStock = new ArrayList<>();
        List<Sale> allSales = salesManager.getAllSales();

        for (Product p : inventoryManager.getAllProducts()) {
            boolean everSold = false;
            for (Sale s : allSales) {
                if (s.getProductId().equals(p.getProductId())
                        && !s.isRefunded()) {
                    everSold = true;
                    break;
                }
            }
            if (!everSold) {
                deadStock.add(p);
            }
        }
        return deadStock;
    }

    // ── Total revenue ─────────────────────────────────────
    public double getTotalRevenue() {
        return salesManager.getTotalRevenue();
    }

    // ── Total stock value ─────────────────────────────────
    public double getTotalStockValue() {
        return inventoryManager.getTotalStockValue();
    }

    // ── Total refunds ─────────────────────────────────────
    public double getTotalRefunds() {
        double total = 0;
        for (Sale s : salesManager.getAllSales()) {
            if (s.isRefunded()) {
                total += s.getTotalAmount();
            }
        }
        return total;
    }

    // ── Print full analytics dashboard ────────────────────
    public void printAnalyticsDashboard() {
        ConsoleColors.printHeader("ANALYTICS DASHBOARD");
        System.out.println();

        // Revenue summary
        ConsoleColors.printInfo("REVENUE SUMMARY:");
        System.out.printf("  Total Revenue    : UGX %,.0f%n",
                getTotalRevenue());
        System.out.printf("  Today's Revenue  : UGX %,.0f%n",
                salesManager.getTodaysRevenue());
        System.out.printf("  Total Refunds    : UGX %,.0f%n",
                getTotalRefunds());
        System.out.printf("  Net Revenue      : UGX %,.0f%n",
                getTotalRevenue() - getTotalRefunds());
        System.out.println();

        // Stock summary
        ConsoleColors.printInfo("STOCK SUMMARY:");
        System.out.printf("  Total Products   : %d%n",
                inventoryManager.getTotalProducts());
        System.out.printf("  Total Stock Value: UGX %,.0f%n",
                getTotalStockValue());
        System.out.printf("  Low Stock Items  : %d%n",
                inventoryManager.getLowStockProducts().size());
        System.out.printf("  Expired Items    : %d%n",
                inventoryManager.getExpiredProducts().size());
        System.out.println();

        // Best seller
        ConsoleColors.printInfo("BEST SELLING PRODUCT:");
        Product best = getBestSellingProduct();
        if (best != null) {
            System.out.println("  " + best.getName()
                    + " [" + best.getProductId() + "]");
        } else {
            System.out.println("  No sales recorded yet.");
        }
        System.out.println();

        // Dead stock
        ConsoleColors.printInfo("DEAD STOCK (Never Sold):");
        List<Product> dead = getDeadStockProducts();
        if (dead.isEmpty()) {
            System.out.println("  All products have been sold at least once.");
        } else {
            for (Product p : dead) {
                System.out.println("  " + p.getName()
                        + " [" + p.getProductId() + "]"
                        + " — QTY: " + p.getQuantity());
            }
        }
        System.out.println();
    }
}