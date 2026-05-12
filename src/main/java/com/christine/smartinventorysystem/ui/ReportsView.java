package com.christine.smartinventorysystem.ui;

import com.christine.smartinventorysystem.models.PerishableProduct;
import com.christine.smartinventorysystem.models.Product;
import com.christine.smartinventorysystem.models.Sale;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportsView {

    private VBox view;

    public ReportsView() {

        // ── Title ─────────────────────────────────────────
        Label title = new Label("📋 Reports");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#2c3e50"));

        // ── Report buttons ────────────────────────────────
        Button fullReportBtn   = createReportBtn(
                "📦 Full Inventory Report", "#3498db");
        Button lowStockBtn     = createReportBtn(
                "⚠ Low Stock Report",       "#e67e22");
        Button expiredBtn      = createReportBtn(
                "❌ Expired Products",       "#e74c3c");
        Button salesReportBtn  = createReportBtn(
                "🛒 Sales Report",           "#27ae60");
        Button dailySummaryBtn = createReportBtn(
                "📊 Daily Summary",          "#8e44ad");

        HBox btnRow = new HBox(10,
                fullReportBtn, lowStockBtn, expiredBtn,
                salesReportBtn, dailySummaryBtn);
        btnRow.setPadding(new Insets(10, 0, 10, 0));

        // ── Report output area ────────────────────────────
        TextArea reportArea = new TextArea();
        reportArea.setEditable(false);
        reportArea.setFont(Font.font("Monospaced", 13));
        reportArea.setStyle("-fx-font-family: monospace;");
        VBox.setVgrow(reportArea, Priority.ALWAYS);

        // ── Save report button (hidden until report shown)
        Button saveReportBtn = new Button(
                "💾 Save Report to File");
        saveReportBtn.setStyle(
                "-fx-background-color: #2c3e50;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 8 16 8 16;"
        );
        saveReportBtn.setVisible(false);

        // Label to track current report name
        Label reportNameLabel = new Label();
        reportNameLabel.setVisible(false);

        // ── Button actions ────────────────────────────────
        fullReportBtn.setOnAction(e -> {
            String report = generateFullReport();
            reportArea.setText(report);
            saveReportBtn.setVisible(true);
            saveReportBtn.setUserData(report);
            reportNameLabel.setText("Full_Inventory_Report");
        });

        lowStockBtn.setOnAction(e -> {
            String report = generateLowStockReport();
            reportArea.setText(report);
            saveReportBtn.setVisible(true);
            saveReportBtn.setUserData(report);
            reportNameLabel.setText("Low_Stock_Report");
        });

        expiredBtn.setOnAction(e -> {
            String report = generateExpiredReport();
            reportArea.setText(report);
            saveReportBtn.setVisible(true);
            saveReportBtn.setUserData(report);
            reportNameLabel.setText("Expired_Products_Report");
        });

        salesReportBtn.setOnAction(e -> {
            String report = generateSalesReport();
            reportArea.setText(report);
            saveReportBtn.setVisible(true);
            saveReportBtn.setUserData(report);
            reportNameLabel.setText("Sales_Report");
        });

        dailySummaryBtn.setOnAction(e -> {
            String report = generateDailySummary();
            reportArea.setText(report);
            saveReportBtn.setVisible(true);
            saveReportBtn.setUserData(report);
            reportNameLabel.setText("Daily_Summary");
        });

        // ── Save report button action ─────────────────────
        saveReportBtn.setOnAction(e -> {
            String reportText = (String) saveReportBtn
                    .getUserData();
            if (reportText == null) return;

            try {
                java.io.File folder =
                        new java.io.File("reports");
                if (!folder.exists()) folder.mkdirs();

                String timestamp = LocalDateTime.now()
                        .format(DateTimeFormatter
                                .ofPattern("yyyyMMdd_HHmmss"));
                String reportName = reportNameLabel.getText();
                String fileName = "reports/"
                        + reportName + "_"
                        + timestamp + ".txt";

                try (PrintWriter writer = new PrintWriter(
                        new FileWriter(fileName))) {
                    writer.print(reportText);
                }

                Alert success = new Alert(
                        Alert.AlertType.INFORMATION);
                success.setTitle("Report Saved");
                success.setHeaderText(
                        "Report saved successfully!");
                success.setContentText(
                        "Saved to: " + fileName);
                success.showAndWait();

            } catch (IOException ex) {
                Alert error = new Alert(
                        Alert.AlertType.ERROR);
                error.setTitle("Save Error");
                error.setHeaderText(
                        "Could not save report");
                error.setContentText(ex.getMessage());
                error.showAndWait();
            }
        });

        // ── Layout ────────────────────────────────────────
        view = new VBox(12,
                title, btnRow, reportArea, saveReportBtn);
        view.setPadding(new Insets(20));
    }

    // ── Full inventory report ─────────────────────────────
    private String generateFullReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("         FULL INVENTORY REPORT          \n");
        sb.append("  Generated: ")
                .append(LocalDate.now()).append("\n");
        sb.append("═══════════════════════════════════════\n\n");

        List<Product> products =
                AppLauncher.inventoryManager.getAllProducts();

        if (products.isEmpty()) {
            sb.append("  No products in inventory.\n");
            return sb.toString();
        }

        sb.append(String.format("%-10s %-20s %-12s %-8s %-15s %s%n",
                "ID", "NAME", "PRICE(UGX)",
                "QTY", "TYPE", "STATUS"));
        sb.append("─".repeat(75)).append("\n");

        for (Product p : products) {
            String status = p.isExpired() ? "❌ EXPIRED"
                    : p.isLowStock() ? "⚠ LOW STOCK"
                      : "✔ OK";
            sb.append(String.format(
                    "%-10s %-20s %-12.0f %-8d %-15s %s%n",
                    p.getProductId(), p.getName(),
                    p.getPrice(), p.getQuantity(),
                    p.getProductType(), status));
        }

        sb.append("─".repeat(75)).append("\n");
        sb.append(String.format(
                "  Total Products   : %d%n",
                AppLauncher.inventoryManager
                        .getTotalProducts()));
        sb.append(String.format(
                "  Total Stock Value: UGX %,.0f%n",
                AppLauncher.inventoryManager
                        .getTotalStockValue()));
        return sb.toString();
    }

    // ── Low stock report ──────────────────────────────────
    private String generateLowStockReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("           LOW STOCK REPORT             \n");
        sb.append("  Generated: ")
                .append(LocalDate.now()).append("\n");
        sb.append("═══════════════════════════════════════\n\n");

        List<Product> lowStock =
                AppLauncher.inventoryManager
                        .getLowStockProducts();

        if (lowStock.isEmpty()) {
            sb.append(
                    "  ✔ All products have sufficient stock!\n");
            return sb.toString();
        }

        for (Product p : lowStock) {
            sb.append(String.format(
                    "  ⚠ %-20s [%s]  QTY: %d  THRESHOLD: %d%n",
                    p.getName(), p.getProductId(),
                    p.getQuantity(),
                    p.getLowStockThreshold()));
        }

        sb.append("\n  Total Low Stock Items: ")
                .append(lowStock.size()).append("\n");
        return sb.toString();
    }

    // ── Expired report ────────────────────────────────────
    private String generateExpiredReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("        EXPIRED PRODUCTS REPORT         \n");
        sb.append("  Generated: ")
                .append(LocalDate.now()).append("\n");
        sb.append("═══════════════════════════════════════\n\n");

        List<Product> expired =
                AppLauncher.inventoryManager
                        .getExpiredProducts();
        List<Product> expiringSoon =
                AppLauncher.inventoryManager
                        .getExpiringSoonProducts();

        if (expired.isEmpty() && expiringSoon.isEmpty()) {
            sb.append(
                    "  ✔ No expired or expiring products!\n");
            return sb.toString();
        }

        if (!expired.isEmpty()) {
            sb.append(
                    "  ❌ EXPIRED — Remove immediately:\n\n");
            for (Product p : expired) {
                PerishableProduct pp =
                        (PerishableProduct) p;
                sb.append(String.format(
                        "  %-20s [%s]  Expired: %s%n",
                        pp.getName(), pp.getProductId(),
                        pp.getExpiryDate()));
            }
            sb.append("\n");
        }

        if (!expiringSoon.isEmpty()) {
            sb.append(
                    "  ⚠ EXPIRING SOON (within 7 days):\n\n");
            for (Product p : expiringSoon) {
                PerishableProduct pp =
                        (PerishableProduct) p;
                sb.append(String.format(
                        "  %-20s [%s]  Expires: %s%n",
                        pp.getName(), pp.getProductId(),
                        pp.getExpiryDate()));
            }
        }
        return sb.toString();
    }

    // ── Sales report ──────────────────────────────────────
    private String generateSalesReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("            SALES REPORT                \n");
        sb.append("  Generated: ")
                .append(LocalDate.now()).append("\n");
        sb.append("═══════════════════════════════════════\n\n");

        List<Sale> sales =
                AppLauncher.salesManager.getAllSales();

        if (sales.isEmpty()) {
            sb.append("  No sales recorded yet.\n");
            return sb.toString();
        }

        for (Sale s : sales) {
            sb.append(String.format(
                    "  %-10s %-20s QTY:%-5d UGX%-10.0f %s%n",
                    s.getSaleId(), s.getProductName(),
                    s.getQuantitySold(), s.getTotalAmount(),
                    s.isRefunded() ? "[REFUNDED]" : ""));
        }

        sb.append("\n─".repeat(38)).append("\n");
        sb.append(String.format(
                "  Total Sales  : %d%n",
                AppLauncher.salesManager.getTotalSales()));
        sb.append(String.format(
                "  Total Revenue: UGX %,.0f%n",
                AppLauncher.salesManager.getTotalRevenue()));
        return sb.toString();
    }

    // ── Daily summary ─────────────────────────────────────
    private String generateDailySummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("          DAILY SUMMARY REPORT          \n");
        sb.append("  Date: ")
                .append(LocalDate.now()).append("\n");
        sb.append("═══════════════════════════════════════\n\n");

        sb.append("  TODAY'S SALES:\n");
        sb.append(String.format(
                "  Transactions : %d%n",
                AppLauncher.salesManager
                        .getTodaysSales().size()));
        sb.append(String.format(
                "  Revenue      : UGX %,.0f%n%n",
                AppLauncher.salesManager
                        .getTodaysRevenue()));

        sb.append("  ALERTS:\n");
        sb.append(String.format(
                "  Low Stock    : %d items%n",
                AppLauncher.inventoryManager
                        .getLowStockProducts().size()));
        sb.append(String.format(
                "  Expired      : %d items%n",
                AppLauncher.inventoryManager
                        .getExpiredProducts().size()));
        sb.append(String.format(
                "  Expiring Soon: %d items%n%n",
                AppLauncher.inventoryManager
                        .getExpiringSoonProducts().size()));

        sb.append("  STOCK:\n");
        sb.append(String.format(
                "  Total Products: %d%n",
                AppLauncher.inventoryManager
                        .getTotalProducts()));
        sb.append(String.format(
                "  Stock Value   : UGX %,.0f%n",
                AppLauncher.inventoryManager
                        .getTotalStockValue()));
        return sb.toString();
    }

    // ── Report button helper ──────────────────────────────
    private Button createReportBtn(String text,
                                   String color) {
        Button btn = new Button(text);
        btn.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 8 12 8 12;"
        );
        return btn;
    }

    public VBox getView() {
        return view;
    }
}