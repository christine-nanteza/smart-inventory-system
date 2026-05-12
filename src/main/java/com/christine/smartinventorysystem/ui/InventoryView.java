package com.christine.smartinventorysystem.ui;

import com.christine.smartinventorysystem.models.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;

public class InventoryView {

    private VBox view;

    public InventoryView() {

        // ── Title ─────────────────────────────────────────
        Label title = new Label("📊 Inventory Status");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#2c3e50"));

        // ── Summary cards ─────────────────────────────────
        int totalProducts = AppLauncher.inventoryManager
                .getTotalProducts();
        int lowStock = AppLauncher.inventoryManager
                .getLowStockProducts().size();
        int expired = AppLauncher.inventoryManager
                .getExpiredProducts().size();
        double stockValue = AppLauncher.inventoryManager
                .getTotalStockValue();

        HBox summaryBar = new HBox(15,
                createCard("Total Products",
                        String.valueOf(totalProducts), "#3498db"),
                createCard("Low Stock",
                        String.valueOf(lowStock), "#e67e22"),
                createCard("Expired",
                        String.valueOf(expired), "#e74c3c"),
                createCard("Stock Value",
                        String.format("UGX %,.0f", stockValue),
                        "#27ae60")
        );
        summaryBar.setPadding(new Insets(10, 0, 10, 0));

        // ── Table ─────────────────────────────────────────
        TableView<Product> table = new TableView<>();
        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(
                new Label("No products in this category."));

        TableColumn<Product, String> idCol =
                new TableColumn<>("ID");
        idCol.setCellValueFactory(
                new PropertyValueFactory<>("productId"));

        TableColumn<Product, String> nameCol =
                new TableColumn<>("Name");
        nameCol.setCellValueFactory(
                new PropertyValueFactory<>("name"));

        TableColumn<Product, Integer> qtyCol =
                new TableColumn<>("Quantity");
        qtyCol.setCellValueFactory(
                new PropertyValueFactory<>("quantity"));

        TableColumn<Product, Integer> thresholdCol =
                new TableColumn<>("Threshold");
        thresholdCol.setCellValueFactory(
                new PropertyValueFactory<>("lowStockThreshold"));

        TableColumn<Product, String> typeCol =
                new TableColumn<>("Type");
        typeCol.setCellValueFactory(
                new PropertyValueFactory<>("productType"));

        // ── Color rows by status ──────────────────────────
        table.setRowFactory(tv -> new TableRow<Product>() {
            @Override
            protected void updateItem(Product p, boolean empty) {
                super.updateItem(p, empty);
                if (p == null || empty) {
                    setStyle("");
                } else if (p.isExpired()) {
                    setStyle("-fx-background-color: #fadbd8;");
                } else if (p.isLowStock()) {
                    setStyle("-fx-background-color: #fef9e7;");
                } else {
                    setStyle("-fx-background-color: #eafaf1;");
                }
            }
        });

        table.getColumns().addAll(
                idCol, nameCol, qtyCol,
                thresholdCol, typeCol);

        // Load all products initially
        refreshTable(table,
                AppLauncher.inventoryManager.getAllProducts());

        // ── Filter buttons ────────────────────────────────
        Button allBtn = createFilterBtn(
                "⚪ All", "#7f8c8d");
        Button okBtn = createFilterBtn(
                "🟢 OK", "#27ae60");
        Button lowStockBtn = createFilterBtn(
                "🟡 Low Stock", "#e67e22");
        Button expiredBtn = createFilterBtn(
                "🔴 Expired", "#e74c3c");

        // Start with All highlighted
        setActiveFilter(allBtn,
                new Button[]{allBtn, okBtn,
                        lowStockBtn, expiredBtn});

        allBtn.setOnAction(e -> {
            setActiveFilter(allBtn,
                    new Button[]{allBtn, okBtn,
                            lowStockBtn, expiredBtn});
            refreshTable(table,
                    AppLauncher.inventoryManager
                            .getAllProducts());
        });

        okBtn.setOnAction(e -> {
            setActiveFilter(okBtn,
                    new Button[]{allBtn, okBtn,
                            lowStockBtn, expiredBtn});
            List<Product> okProducts = new ArrayList<>();
            for (Product p : AppLauncher.inventoryManager
                    .getAllProducts()) {
                if (!p.isExpired() && !p.isLowStock()) {
                    okProducts.add(p);
                }
            }
            refreshTable(table, okProducts);
        });

        lowStockBtn.setOnAction(e -> {
            setActiveFilter(lowStockBtn,
                    new Button[]{allBtn, okBtn,
                            lowStockBtn, expiredBtn});
            refreshTable(table,
                    AppLauncher.inventoryManager
                            .getLowStockProducts());
        });

        expiredBtn.setOnAction(e -> {
            setActiveFilter(expiredBtn,
                    new Button[]{allBtn, okBtn,
                            lowStockBtn, expiredBtn});
            refreshTable(table,
                    AppLauncher.inventoryManager
                            .getExpiredProducts());
        });

        HBox filterBar = new HBox(10,
                new Label("Filter: "),
                allBtn, okBtn, lowStockBtn, expiredBtn);
        filterBar.setPadding(new Insets(5));

        // ── Restock section ───────────────────────────────
        Label restockTitle = new Label("Restock Product");
        restockTitle.setFont(
                Font.font("Arial", FontWeight.BOLD, 14));

        TextField productIdField = new TextField();
        productIdField.setPromptText("Product ID");
        productIdField.setPrefWidth(150);

        TextField amountField = new TextField();
        amountField.setPromptText("Quantity to add");
        amountField.setPrefWidth(150);

        Label messageLabel = new Label();

        Button restockBtn = new Button("Restock");
        restockBtn.setStyle(
                "-fx-background-color: #8e44ad;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
        );

        restockBtn.setOnAction(e -> {
            try {
                String id = productIdField.getText().trim();
                int amount = Integer.parseInt(
                        amountField.getText().trim());
                int oldQty = AppLauncher.inventoryManager
                        .findProductById(id).getQuantity();

                AppLauncher.inventoryManager
                        .restockProduct(id, amount);

                int newQty = AppLauncher.inventoryManager
                        .findProductById(id).getQuantity();

                messageLabel.setTextFill(Color.GREEN);
                messageLabel.setText("✔ Restocked! "
                        + oldQty + " → " + newQty
                        + " units");

                productIdField.clear();
                amountField.clear();

                refreshTable(table,
                        AppLauncher.inventoryManager
                                .getAllProducts());

                // Reset filter to All
                setActiveFilter(allBtn,
                        new Button[]{allBtn, okBtn,
                                lowStockBtn, expiredBtn});

            } catch (NumberFormatException ex) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText(
                        "Enter a valid quantity!");
            } catch (Exception ex) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText(ex.getMessage());
            }
        });

        HBox restockBox = new HBox(10,
                restockTitle, productIdField,
                amountField, restockBtn, messageLabel);
        restockBox.setPadding(new Insets(12));
        restockBox.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #bdc3c7;" +
                        "-fx-border-radius: 5;"
        );

        view = new VBox(12,
                title, summaryBar,
                filterBar, table, restockBox);
        view.setPadding(new Insets(20));
        VBox.setVgrow(table, Priority.ALWAYS);
    }

    // ── Set active filter button style ────────────────────
    private void setActiveFilter(Button active,
                                 Button[] all) {
        for (Button btn : all) {
            btn.setOpacity(0.5);
            btn.setStyle(btn.getStyle()
                    .replace("-fx-font-weight: bold;", ""));
        }
        active.setOpacity(1.0);
    }

    // ── Filter button helper ──────────────────────────────
    private Button createFilterBtn(String text,
                                   String color) {
        Button btn = new Button(text);
        btn.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 6 12 6 12;" +
                        "-fx-background-radius: 5;"
        );
        return btn;
    }

    // ── Refresh table with given list ─────────────────────
    private void refreshTable(TableView<Product> table,
                              List<Product> list) {
        ObservableList<Product> data =
                FXCollections.observableArrayList(list);
        table.setItems(data);
    }

    // ── Summary card helper ───────────────────────────────
    private VBox createCard(String label,
                            String value, String color) {
        Label valueLabel = new Label(value);
        valueLabel.setFont(
                Font.font("Arial", FontWeight.BOLD, 20));
        valueLabel.setTextFill(Color.WHITE);

        Label nameLabel = new Label(label);
        nameLabel.setTextFill(Color.WHITE);
        nameLabel.setFont(Font.font("Arial", 12));

        VBox card = new VBox(4, valueLabel, nameLabel);
        card.setPadding(new Insets(12, 20, 12, 20));
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