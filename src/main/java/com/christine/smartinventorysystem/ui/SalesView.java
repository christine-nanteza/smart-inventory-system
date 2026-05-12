package com.christine.smartinventorysystem.ui;

import com.christine.smartinventorysystem.models.Product;
import com.christine.smartinventorysystem.models.Sale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SalesView {

    private VBox view;

    public SalesView() {

        // ── Title ─────────────────────────────────────────
        Label title = new Label("🛒 Process Sales");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#2c3e50"));

        // ── Sales Table ───────────────────────────────────
        TableView<Sale> table = new TableView<>();
        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(
                new Label("No sales recorded yet."));

        TableColumn<Sale, String> idCol =
                new TableColumn<>("Sale ID");
        idCol.setCellValueFactory(
                new PropertyValueFactory<>("saleId"));

        TableColumn<Sale, String> productCol =
                new TableColumn<>("Product");
        productCol.setCellValueFactory(
                new PropertyValueFactory<>("productName"));

        TableColumn<Sale, Integer> qtyCol =
                new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(
                new PropertyValueFactory<>("quantitySold"));

        TableColumn<Sale, Double> totalCol =
                new TableColumn<>("Total (UGX)");
        totalCol.setCellValueFactory(
                new PropertyValueFactory<>("totalAmount"));

        TableColumn<Sale, String> cashierCol =
                new TableColumn<>("Cashier");
        cashierCol.setCellValueFactory(
                new PropertyValueFactory<>("cashierName"));

        // ── Custom Status column ──────────────────────────
        TableColumn<Sale, Boolean> refundCol =
                new TableColumn<>("Status");
        refundCol.setCellValueFactory(
                new PropertyValueFactory<>("refunded"));
        refundCol.setCellFactory(col ->
                new TableCell<Sale, Boolean>() {
                    @Override
                    protected void updateItem(
                            Boolean refunded,
                            boolean empty) {
                        super.updateItem(refunded, empty);
                        if (empty || refunded == null) {
                            setText(null);
                            setStyle("");
                        } else if (refunded) {
                            setText("↩ Refunded");
                            setStyle(
                                    "-fx-text-fill: #e74c3c;" +
                                            "-fx-font-weight: bold;");
                        } else {
                            setText("✔ Completed");
                            setStyle(
                                    "-fx-text-fill: #27ae60;" +
                                            "-fx-font-weight: bold;");
                        }
                    }
                });

        table.getColumns().addAll(
                idCol, productCol, qtyCol,
                totalCol, cashierCol, refundCol);

        refreshTable(table);

        // ── Message label ─────────────────────────────────
        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("Arial", 13));

        // ── PROCESS SALE FORM ─────────────────────────────
        Label formTitle = new Label("Process New Sale");
        formTitle.setFont(
                Font.font("Arial", FontWeight.BOLD, 14));

        // ── Cashier info display ──────────────────────────
        String loggedInName = AppLauncher.userManager
                .getCurrentUser() != null
                ? AppLauncher.userManager
                  .getCurrentUser().getFullName()
                : "Unknown";

        Label cashierInfoLabel = new Label(
                "👤 Serving as: " + loggedInName);
        cashierInfoLabel.setFont(
                Font.font("Arial", FontWeight.BOLD, 13));
        cashierInfoLabel.setTextFill(
                Color.web("#2980b9"));
        cashierInfoLabel.setStyle(
                "-fx-background-color: #ebf5fb;" +
                        "-fx-padding: 6 12 6 12;" +
                        "-fx-background-radius: 5;"
        );

        TextField productSearchField = new TextField();
        productSearchField.setPromptText(
                "Product ID or Name (e.g. PRD001 or Milk)");
        productSearchField.setPrefWidth(280);

        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        quantityField.setPrefWidth(280);

        Button sellBtn = new Button("🛒 Process Sale");
        sellBtn.setPrefWidth(180);
        sellBtn.setPrefHeight(40);
        sellBtn.setStyle(
                "-fx-background-color: #27ae60;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
        );

        // ── Sell button action ────────────────────────────
        sellBtn.setOnAction(e -> {
            try {
                String input = productSearchField
                        .getText().trim();
                int quantity = Integer.parseInt(
                        quantityField.getText().trim());

                if (input.isEmpty()) {
                    messageLabel.setTextFill(Color.RED);
                    messageLabel.setText(
                            "Please enter a Product ID "
                                    + "or Name!");
                    return;
                }

                String productId =
                        resolveProductId(input);
                if (productId == null) {
                    messageLabel.setTextFill(Color.RED);
                    messageLabel.setText(
                            "No product found for: '"
                                    + input + "'");
                    return;
                }

                Sale sale = AppLauncher.salesManager
                        .processSale(
                                productId,
                                quantity,
                                AppLauncher.userManager
                                        .getCurrentUser()
                                        .getFullName());

                messageLabel.setTextFill(Color.GREEN);
                messageLabel.setText(
                        "✔ Sale processed for: "
                                + sale.getProductName());

                productSearchField.clear();
                quantityField.clear();
                refreshTable(table);

                // Show receipt popup automatically
                showReceiptPopup(sale);

            } catch (NumberFormatException ex) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText(
                        "Please enter a valid quantity!");
            } catch (Exception ex) {
                Alert alert = new Alert(
                        Alert.AlertType.ERROR);
                alert.setTitle("Sale Error");
                alert.setHeaderText(
                        "Cannot Process Sale");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        });

        // ── Form layout ───────────────────────────────────
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10));
        form.add(cashierInfoLabel,       0, 0, 2, 1);
        form.add(new Label("Product:"),  0, 1);
        form.add(productSearchField,     1, 1);
        form.add(new Label("Quantity:"), 0, 2);
        form.add(quantityField,          1, 2);
        form.add(sellBtn,                1, 3);
        form.add(messageLabel,           1, 4);

        VBox formBox = new VBox(10, formTitle, form);
        formBox.setPadding(new Insets(10));
        formBox.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #27ae60;" +
                        "-fx-border-radius: 5;" +
                        "-fx-border-width: 2;"
        );

        // ── REFUND FORM ───────────────────────────────────
        Label refundTitle = new Label(
                "↩ Process Refund");
        refundTitle.setFont(
                Font.font("Arial", FontWeight.BOLD, 14));

        TextField saleIdField = new TextField();
        saleIdField.setPromptText(
                "Sale ID to refund (e.g. SAL001)");
        saleIdField.setPrefWidth(200);

        Button refundBtn = new Button("Issue Refund");
        refundBtn.setPrefWidth(160);
        refundBtn.setPrefHeight(38);
        refundBtn.setStyle(
                "-fx-background-color: #e74c3c;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
        );

        refundBtn.setOnAction(e -> {
            try {
                String saleId =
                        saleIdField.getText().trim();
                AppLauncher.salesManager
                        .processRefund(saleId);
                messageLabel.setTextFill(Color.GREEN);
                messageLabel.setText(
                        "✔ Refund processed for "
                                + saleId);
                saleIdField.clear();
                refreshTable(table);
            } catch (Exception ex) {
                Alert alert = new Alert(
                        Alert.AlertType.ERROR);
                alert.setTitle("Refund Error");
                alert.setHeaderText(
                        "Cannot Process Refund");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        });

        VBox refundBox = new VBox(8,
                refundTitle,
                new HBox(10,
                        new Label("Sale ID:"),
                        saleIdField, refundBtn));
        refundBox.setPadding(new Insets(10));
        refundBox.setStyle(
                "-fx-background-color: #ffeaa7;" +
                        "-fx-border-color: #fdcb6e;" +
                        "-fx-border-radius: 5;" +
                        "-fx-border-width: 2;"
        );

        // ── Main layout ───────────────────────────────────
        view = new VBox(15,
                title, table, formBox, refundBox);
        view.setPadding(new Insets(20));
        VBox.setVgrow(table, Priority.ALWAYS);
    }

    // ── Receipt popup window ──────────────────────────────
    private void showReceiptPopup(Sale sale) {
        Stage receiptStage = new Stage();
        receiptStage.initModality(
                Modality.APPLICATION_MODAL);
        receiptStage.setTitle("🧾 Sale Receipt");

        String receiptText = AppLauncher.salesManager
                .generateReceipt(sale);

        TextArea receiptArea = new TextArea(receiptText);
        receiptArea.setEditable(false);
        receiptArea.setFont(
                Font.font("Monospaced", 14));
        receiptArea.setPrefSize(420, 320);
        receiptArea.setStyle(
                "-fx-font-family: monospace;");

        Button saveBtn = new Button(
                "💾 Save Receipt to File");
        saveBtn.setStyle(
                "-fx-background-color: #2c3e50;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 8 16 8 16;"
        );

        Button closeBtn = new Button("Close");
        closeBtn.setStyle(
                "-fx-background-color: #95a5a6;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 8 16 8 16;"
        );
        closeBtn.setOnAction(e ->
                receiptStage.close());

        saveBtn.setOnAction(e -> {
            try {
                java.io.File folder =
                        new java.io.File("receipts");
                if (!folder.exists()) folder.mkdirs();

                String timestamp = LocalDateTime.now()
                        .format(DateTimeFormatter
                                .ofPattern(
                                        "yyyyMMdd_HHmmss"));
                String fileName = "receipts/receipt_"
                        + sale.getSaleId()
                        + "_" + timestamp + ".txt";

                try (PrintWriter writer =
                             new PrintWriter(
                                     new FileWriter(fileName))) {
                    writer.print(receiptText);
                }

                Alert success = new Alert(
                        Alert.AlertType.INFORMATION);
                success.setTitle("Saved!");
                success.setHeaderText(
                        "Receipt saved successfully!");
                success.setContentText(
                        "Saved to: " + fileName);
                success.showAndWait();

            } catch (IOException ex) {
                Alert error = new Alert(
                        Alert.AlertType.ERROR);
                error.setTitle("Save Error");
                error.setHeaderText(
                        "Could not save receipt");
                error.setContentText(ex.getMessage());
                error.showAndWait();
            }
        });

        HBox btnRow = new HBox(10, saveBtn, closeBtn);
        btnRow.setAlignment(Pos.CENTER);
        btnRow.setPadding(new Insets(10));

        VBox layout = new VBox(10,
                receiptArea, btnRow);
        layout.setPadding(new Insets(15));
        layout.setStyle(
                "-fx-background-color: #f9f9f9;");

        Scene scene = new Scene(layout);
        receiptStage.setScene(scene);
        receiptStage.setResizable(false);
        receiptStage.show();
    }

    // ── Resolve product ID from ID or name ────────────────
    private String resolveProductId(String input) {
        Product byId = AppLauncher.inventoryManager
                .findProductByIdSilent(input);
        if (byId != null) return byId.getProductId();

        List<Product> byName = AppLauncher.inventoryManager
                .searchByName(input);
        if (!byName.isEmpty()) {
            if (byName.size() == 1) {
                return byName.get(0).getProductId();
            } else {
                List<String> choices =
                        new java.util.ArrayList<>();
                for (Product p : byName) {
                    choices.add(p.getProductId()
                            + " — " + p.getName());
                }
                ChoiceDialog<String> dialog =
                        new ChoiceDialog<>(
                                choices.get(0), choices);
                dialog.setTitle(
                        "Multiple Products Found");
                dialog.setHeaderText(
                        "Multiple products match '"
                                + input + "'");
                dialog.setContentText("Select one:");
                return dialog.showAndWait()
                        .map(s -> s.split(" — ")[0])
                        .orElse(null);
            }
        }
        return null;
    }

    private void refreshTable(TableView<Sale> table) {
        ObservableList<Sale> data =
                FXCollections.observableArrayList(
                        AppLauncher.salesManager
                                .getAllSales());
        table.setItems(data);
    }

    public VBox getView() {
        return view;
    }
}