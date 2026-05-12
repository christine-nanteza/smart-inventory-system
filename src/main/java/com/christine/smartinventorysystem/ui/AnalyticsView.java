package com.christine.smartinventorysystem.ui;

import com.christine.smartinventorysystem.models.Product;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class AnalyticsView {

    private VBox view;

    public AnalyticsView() {

        // ── Title ─────────────────────────────────────────
        Label title = new Label("📈 Analytics Dashboard");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#2c3e50"));

        // ── Revenue cards ─────────────────────────────────
        double totalRevenue =
                AppLauncher.analyticsService.getTotalRevenue();
        double todayRevenue =
                AppLauncher.salesManager.getTodaysRevenue();
        double totalRefunds =
                AppLauncher.analyticsService.getTotalRefunds();
        double netRevenue   = totalRevenue - totalRefunds;

        HBox revenueRow = new HBox(15,
                createCard("Total Revenue",
                        String.format("UGX %,.0f", totalRevenue),
                        "#27ae60"),
                createCard("Today's Revenue",
                        String.format("UGX %,.0f", todayRevenue),
                        "#3498db"),
                createCard("Total Refunds",
                        String.format("UGX %,.0f", totalRefunds),
                        "#e74c3c"),
                createCard("Net Revenue",
                        String.format("UGX %,.0f", netRevenue),
                        "#8e44ad")
        );

        // ── Best selling product ──────────────────────────
        Label bestSellerTitle = new Label("🏆 Best Selling Product");
        bestSellerTitle.setFont(
                Font.font("Arial", FontWeight.BOLD, 14));

        Product best = AppLauncher.analyticsService
                .getBestSellingProduct();

        Label bestSellerValue = new Label(
                best != null
                        ? best.getName() + "  [" + best.getProductId() + "]"
                        : "No sales recorded yet");
        bestSellerValue.setFont(Font.font("Arial", 14));
        bestSellerValue.setTextFill(Color.web("#27ae60"));

        VBox bestSellerBox = new VBox(6,
                bestSellerTitle, bestSellerValue);
        bestSellerBox.setPadding(new Insets(12));
        bestSellerBox.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #27ae60;" +
                        "-fx-border-radius: 5;" +
                        "-fx-border-width: 2;"
        );

        // ── Dead stock ────────────────────────────────────
        Label deadStockTitle = new Label("💀 Dead Stock (Never Sold)");
        deadStockTitle.setFont(
                Font.font("Arial", FontWeight.BOLD, 14));

        List<Product> deadStock = AppLauncher.analyticsService
                .getDeadStockProducts();

        VBox deadStockList = new VBox(4);
        if (deadStock.isEmpty()) {
            Label none = new Label(
                    "✔ All products have been sold at least once!");
            none.setTextFill(Color.GREEN);
            deadStockList.getChildren().add(none);
        } else {
            for (Product p : deadStock) {
                Label item = new Label(
                        "  • " + p.getName()
                                + "  [" + p.getProductId() + "]"
                                + "  —  QTY: " + p.getQuantity());
                item.setTextFill(Color.web("#e74c3c"));
                deadStockList.getChildren().add(item);
            }
        }

        VBox deadStockBox = new VBox(8,
                deadStockTitle, deadStockList);
        deadStockBox.setPadding(new Insets(12));
        deadStockBox.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #e74c3c;" +
                        "-fx-border-radius: 5;" +
                        "-fx-border-width: 2;"
        );

        // ── Stock summary ─────────────────────────────────
        Label stockTitle = new Label("📦 Stock Summary");
        stockTitle.setFont(
                Font.font("Arial", FontWeight.BOLD, 14));

        HBox stockRow = new HBox(15,
                createCard("Total Products",
                        String.valueOf(AppLauncher.inventoryManager
                                .getTotalProducts()), "#3498db"),
                createCard("Low Stock",
                        String.valueOf(AppLauncher.inventoryManager
                                .getLowStockProducts()
                                .size()), "#e67e22"),
                createCard("Expired",
                        String.valueOf(AppLauncher.inventoryManager
                                .getExpiredProducts()
                                .size()), "#e74c3c"),
                createCard("Stock Value",
                        String.format("UGX %,.0f",
                                AppLauncher.analyticsService
                                        .getTotalStockValue()),
                        "#16a085")
        );

        // ── Refresh button ────────────────────────────────
        Button refreshBtn = new Button("🔄 Refresh Analytics");
        refreshBtn.setStyle(
                "-fx-background-color: #2c3e50;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 8 16 8 16;"
        );
        refreshBtn.setOnAction(e -> {
            // Rebuild the view by replacing content
            view.getChildren().clear();
            AnalyticsView fresh = new AnalyticsView();
            view.getChildren().addAll(
                    fresh.getView().getChildren());
        });

        // ── Layout ────────────────────────────────────────
        view = new VBox(15,
                title,
                revenueRow,
                bestSellerBox,
                deadStockBox,
                new Label("📊 Stock Overview:"),
                stockRow,
                refreshBtn);
        view.setPadding(new Insets(20));
    }

    // ── Card helper ───────────────────────────────────────
    private VBox createCard(String label,
                            String value, String color) {
        Label valueLabel = new Label(value);
        valueLabel.setFont(
                Font.font("Arial", FontWeight.BOLD, 16));
        valueLabel.setTextFill(Color.WHITE);

        Label nameLabel = new Label(label);
        nameLabel.setTextFill(Color.WHITE);
        nameLabel.setFont(Font.font("Arial", 11));

        VBox card = new VBox(4, valueLabel, nameLabel);
        card.setPadding(new Insets(10, 16, 10, 16));
        card.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-background-radius: 8;"
        );
        return card;
    }

    public VBox getView() {
        return view;
    }
}