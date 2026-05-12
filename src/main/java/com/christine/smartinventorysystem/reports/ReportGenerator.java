package com.christine.smartinventorysystem.reports;

import com.christine.smartinventorysystem.interfaces.Reportable;
import com.christine.smartinventorysystem.managers.InventoryManager;
import com.christine.smartinventorysystem.managers.SalesManager;
import com.christine.smartinventorysystem.models.PerishableProduct;
import com.christine.smartinventorysystem.models.Product;
import com.christine.smartinventorysystem.models.Sale;
import com.christine.smartinventorysystem.services.AnalyticsService;
import com.christine.smartinventorysystem.utils.ConsoleColors;

import java.time.LocalDate;
import java.util.List;

/**
 * Generates all system reports.
 * Implements Reportable interface.
 */
public class ReportGenerator implements Reportable {

    private InventoryManager inventoryManager;
    private SalesManager salesManager;
    private AnalyticsService analyticsService;

    // ── Constructor ───────────────────────────────────────
    public ReportGenerator(InventoryManager inventoryManager,
                           SalesManager salesManager,
                           AnalyticsService analyticsService) {
        this.inventoryManager = inventoryManager;
        this.salesManager = salesManager;
        this.analyticsService = analyticsService;
    }

    // ── Report 1: Full Inventory Report ───────────────────
    @Override
    public void generateFullReport() {
        ConsoleColors.printHeader("FULL INVENTORY REPORT");
        System.out.printf("  Generated: %s%n%n", LocalDate.now());

        List<Product> products = inventoryManager.getAllProducts();

        if (products.isEmpty()) {
            ConsoleColors.printWarning("No products in inventory.");
            return;
        }

        // Table header
        System.out.println(ConsoleColors.BOLD_WHITE
                + String.format("%-10s %-20s %-15s %-15s %-8s %-12s %s",
                "ID", "NAME", "CATEGORY", "TYPE", "QTY",
                "PRICE(UGX)", "STATUS")
                + ConsoleColors.RESET);
        ConsoleColors.printDivider();

        // Table rows
        for (Product p : products) {
            String status = p.isExpired() ? ConsoleColors.RED + "EXPIRED"
                    : p.isLowStock() ? ConsoleColors.YELLOW + "LOW STOCK"
                      : ConsoleColors.GREEN + "OK";

            System.out.printf("%-10s %-20s %-15s %-15s %-8d %-12.0f %s%s%n",
                    p.getProductId(), p.getName(), p.getCategory(),
                    p.getProductType(), p.getQuantity(), p.getPrice(),
                    status, ConsoleColors.RESET);
        }

        ConsoleColors.printDivider();
        System.out.printf("  Total Products   : %d%n",
                inventoryManager.getTotalProducts());
        System.out.printf("  Total Stock Value: UGX %,.0f%n",
                inventoryManager.getTotalStockValue());
        System.out.println();
    }

    // ── Report 2: Low Stock Report ────────────────────────
    @Override
    public void generateLowStockReport() {
        ConsoleColors.printHeader("LOW STOCK REPORT");
        System.out.printf("  Generated: %s%n%n", LocalDate.now());

        List<Product> lowStock = inventoryManager.getLowStockProducts();

        if (lowStock.isEmpty()) {
            ConsoleColors.printSuccess("All products have sufficient stock!");
            return;
        }

        System.out.println(ConsoleColors.BOLD_WHITE
                + String.format("%-10s %-20s %-8s %-10s",
                "ID", "NAME", "QTY", "THRESHOLD")
                + ConsoleColors.RESET);
        ConsoleColors.printDivider();

        for (Product p : lowStock) {
            System.out.printf(ConsoleColors.YELLOW
                            + "%-10s %-20s %-8d %-10d%n" + ConsoleColors.RESET,
                    p.getProductId(), p.getName(),
                    p.getQuantity(), p.getLowStockThreshold());
        }

        ConsoleColors.printDivider();
        System.out.printf("  Total Low Stock Items: %d%n%n",
                lowStock.size());
    }

    // ── Report 3: Expired Products Report ─────────────────
    @Override
    public void generateExpiredReport() {
        ConsoleColors.printHeader("EXPIRED PRODUCTS REPORT");
        System.out.printf("  Generated: %s%n%n", LocalDate.now());

        List<Product> expired = inventoryManager.getExpiredProducts();
        List<Product> expiringSoon = inventoryManager.getExpiringSoonProducts();

        if (expired.isEmpty() && expiringSoon.isEmpty()) {
            ConsoleColors.printSuccess("No expired or expiring products!");
            return;
        }

        // Expired
        if (!expired.isEmpty()) {
            ConsoleColors.printError("EXPIRED — Remove immediately:");
            System.out.println();
            for (Product p : expired) {
                PerishableProduct pp = (PerishableProduct) p;
                System.out.printf(ConsoleColors.RED
                                + "  %-10s %-20s Expired: %s%n"
                                + ConsoleColors.RESET,
                        pp.getProductId(), pp.getName(), pp.getExpiryDate());
            }
            System.out.println();
        }

        // Expiring soon
        if (!expiringSoon.isEmpty()) {
            ConsoleColors.printWarning("EXPIRING SOON (within 7 days):");
            System.out.println();
            for (Product p : expiringSoon) {
                PerishableProduct pp = (PerishableProduct) p;
                System.out.printf(ConsoleColors.YELLOW
                                + "  %-10s %-20s Expires: %s%n"
                                + ConsoleColors.RESET,
                        pp.getProductId(), pp.getName(), pp.getExpiryDate());
            }
            System.out.println();
        }
    }

    // ── Report 4: Sales Report ────────────────────────────
    @Override
    public void generateSalesReport() {
        ConsoleColors.printHeader("SALES REPORT");
        System.out.printf("  Generated: %s%n%n", LocalDate.now());

        List<Sale> sales = salesManager.getAllSales();

        if (sales.isEmpty()) {
            ConsoleColors.printWarning("No sales recorded yet.");
            return;
        }

        System.out.println(ConsoleColors.BOLD_WHITE
                + String.format("%-10s %-10s %-20s %-6s %-12s %-12s %-15s",
                "SALE ID", "PROD ID", "PRODUCT", "QTY",
                "UNIT PRICE", "TOTAL", "CASHIER")
                + ConsoleColors.RESET);
        ConsoleColors.printDivider();

        for (Sale s : sales) {
            String color = s.isRefunded()
                    ? ConsoleColors.RED : ConsoleColors.WHITE;
            System.out.printf(color
                            + "%-10s %-10s %-20s %-6d %-12.0f %-12.0f %-15s %s%n"
                            + ConsoleColors.RESET,
                    s.getSaleId(), s.getProductId(), s.getProductName(),
                    s.getQuantitySold(), s.getPricePerUnit(),
                    s.getTotalAmount(), s.getCashierName(),
                    s.isRefunded() ? "[REFUNDED]" : "");
        }

        ConsoleColors.printDivider();
        System.out.printf("  Total Sales  : %d%n",
                salesManager.getTotalSales());
        System.out.printf("  Total Revenue: UGX %,.0f%n",
                salesManager.getTotalRevenue());
        System.out.println();
    }

    // ── Report 5: Daily Summary ───────────────────────────
    @Override
    public void generateDailySummary() {
        ConsoleColors.printHeader("DAILY SUMMARY REPORT");
        System.out.printf("  Date: %s%n%n", LocalDate.now());

        List<Sale> todaysSales = salesManager.getTodaysSales();

        // Sales summary
        ConsoleColors.printInfo("TODAY'S SALES:");
        System.out.printf("  Total Transactions : %d%n",
                todaysSales.size());
        System.out.printf("  Total Revenue      : UGX %,.0f%n",
                salesManager.getTodaysRevenue());
        System.out.println();

        // Alerts summary
        ConsoleColors.printInfo("ALERTS:");
        System.out.printf("  Low Stock Items    : %d%n",
                inventoryManager.getLowStockProducts().size());
        System.out.printf("  Expired Items      : %d%n",
                inventoryManager.getExpiredProducts().size());
        System.out.printf("  Expiring Soon      : %d%n",
                inventoryManager.getExpiringSoonProducts().size());
        System.out.println();

        // Stock summary
        ConsoleColors.printInfo("STOCK SUMMARY:");
        System.out.printf("  Total Products     : %d%n",
                inventoryManager.getTotalProducts());
        System.out.printf("  Total Stock Value  : UGX %,.0f%n",
                inventoryManager.getTotalStockValue());
        System.out.println();
    }
}