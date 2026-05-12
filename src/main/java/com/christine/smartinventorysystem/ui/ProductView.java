package com.christine.smartinventorysystem.ui;

import com.christine.smartinventorysystem.models.NonPerishableProduct;
import com.christine.smartinventorysystem.models.PerishableProduct;
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

import java.time.LocalDate;
import java.util.List;

public class ProductView {

    private VBox view;

    public ProductView() {

        // ── Title ─────────────────────────────────────────
        Label title = new Label("📦 Manage Products");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#2c3e50"));

        // ── Search bar ────────────────────────────────────
        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search by product name...");
        searchField.setPrefWidth(300);

        Button clearSearchBtn = new Button("Clear");
        clearSearchBtn.setStyle(
                "-fx-background-color: #95a5a6;" +
                        "-fx-text-fill: white;" +
                        "-fx-cursor: hand;"
        );

        HBox searchBar = new HBox(10, searchField, clearSearchBtn);
        searchBar.setPadding(new Insets(0, 0, 5, 0));

        // ── Table ─────────────────────────────────────────
        TableView<Product> table = new TableView<>();
        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(
                new Label("No products found."));

        TableColumn<Product, String> idCol =
                new TableColumn<>("ID");
        idCol.setCellValueFactory(
                new PropertyValueFactory<>("productId"));

        TableColumn<Product, String> nameCol =
                new TableColumn<>("Name");
        nameCol.setCellValueFactory(
                new PropertyValueFactory<>("name"));

        TableColumn<Product, String> categoryCol =
                new TableColumn<>("Category");
        categoryCol.setCellValueFactory(
                new PropertyValueFactory<>("category"));

        TableColumn<Product, Double> priceCol =
                new TableColumn<>("Price (UGX)");
        priceCol.setCellValueFactory(
                new PropertyValueFactory<>("price"));

        TableColumn<Product, Integer> qtyCol =
                new TableColumn<>("Quantity");
        qtyCol.setCellValueFactory(
                new PropertyValueFactory<>("quantity"));

        TableColumn<Product, String> typeCol =
                new TableColumn<>("Type");
        typeCol.setCellValueFactory(
                new PropertyValueFactory<>("productType"));

        table.getColumns().addAll(
                idCol, nameCol, categoryCol,
                priceCol, qtyCol, typeCol);

        refreshTable(table,
                AppLauncher.inventoryManager.getAllProducts());

        // ── Search logic ──────────────────────────────────
        searchField.textProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal == null || newVal.trim().isEmpty()) {
                        refreshTable(table,
                                AppLauncher.inventoryManager
                                        .getAllProducts());
                    } else {
                        refreshTable(table,
                                AppLauncher.inventoryManager
                                        .searchByName(newVal.trim()));
                    }
                });

        clearSearchBtn.setOnAction(e -> {
            searchField.clear();
            refreshTable(table,
                    AppLauncher.inventoryManager
                            .getAllProducts());
        });

        // ── Message label (shared across all forms) ───────
        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("Arial", 13));

        // ── ADD PRODUCT FORM ──────────────────────────────
        Label addTitle = new Label("➕ Add New Product");
        addTitle.setFont(
                Font.font("Arial", FontWeight.BOLD, 14));

        TextField nameField     = new TextField();
        nameField.setPromptText("Product Name");

        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");

        TextField priceField    = new TextField();
        priceField.setPromptText("Price (UGX)");

        TextField qtyField      = new TextField();
        qtyField.setPromptText("Quantity");

        TextField thresholdField = new TextField();
        thresholdField.setPromptText("Low Stock Alert");

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll(
                "Perishable", "Non-Perishable");
        typeBox.setPromptText("Select Type");
        typeBox.setPrefWidth(200);

        TextField expiryField = new TextField();
        expiryField.setPromptText(
                "Expiry Date (dd-MM-yyyy)");
        expiryField.setVisible(false);

        TextField warrantyField = new TextField();
        warrantyField.setPromptText(
                "Warranty (months, 0 if none)");
        warrantyField.setVisible(false);

        typeBox.setOnAction(e -> {
            String selected = typeBox.getValue();
            expiryField.setVisible(
                    "Perishable".equals(selected));
            warrantyField.setVisible(
                    "Non-Perishable".equals(selected));
        });

        Button addBtn = new Button("Add Product");
        addBtn.setStyle(
                "-fx-background-color: #27ae60;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 6 14 6 14;"
        );

        addBtn.setOnAction(e -> {
            try {
                String name     = nameField.getText().trim();
                String category = categoryField.getText().trim();
                double price    = Double.parseDouble(
                        priceField.getText().trim());
                int qty         = Integer.parseInt(
                        qtyField.getText().trim());
                int threshold   = Integer.parseInt(
                        thresholdField.getText().trim());
                String type     = typeBox.getValue();
                String id       = AppLauncher.inventoryManager
                        .generateProductId();

                if (name.isEmpty() || category.isEmpty()
                        || type == null) {
                    messageLabel.setTextFill(Color.RED);
                    messageLabel.setText(
                            "Please fill all fields!");
                    return;
                }

                Product product;
                if ("Perishable".equals(type)) {
                    LocalDate expiry = LocalDate.parse(
                            expiryField.getText().trim(),
                            java.time.format.DateTimeFormatter
                                    .ofPattern("dd-MM-yyyy"));
                    product = new PerishableProduct(
                            id, name, category, price,
                            qty, threshold, expiry);
                } else {
                    int warranty = Integer.parseInt(
                            warrantyField.getText().trim());
                    product = new NonPerishableProduct(
                            id, name, category, price,
                            qty, threshold, warranty);
                }

                AppLauncher.inventoryManager
                        .addProduct(product);
                messageLabel.setTextFill(Color.GREEN);
                messageLabel.setText(
                        "✔ Product '" + name + "' added! ID: " + id);

                nameField.clear();
                categoryField.clear();
                priceField.clear();
                qtyField.clear();
                thresholdField.clear();
                expiryField.clear();
                warrantyField.clear();
                typeBox.setValue(null);

                refreshTable(table,
                        AppLauncher.inventoryManager
                                .getAllProducts());

            } catch (NumberFormatException ex) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText(
                        "Please enter valid numbers!");
            } catch (Exception ex) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText(ex.getMessage());
            }
        });

        GridPane addForm = new GridPane();
        addForm.setHgap(10);
        addForm.setVgap(8);
        addForm.setPadding(new Insets(10));
        addForm.add(nameField,       0, 0);
        addForm.add(categoryField,   1, 0);
        addForm.add(priceField,      0, 1);
        addForm.add(qtyField,        1, 1);
        addForm.add(thresholdField,  0, 2);
        addForm.add(typeBox,         1, 2);
        addForm.add(expiryField,     0, 3);
        addForm.add(warrantyField,   1, 3);
        addForm.add(addBtn,          0, 4);
        addForm.add(messageLabel,    1, 4);

        VBox addBox = new VBox(8, addTitle, addForm);
        addBox.setPadding(new Insets(10));
        addBox.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #27ae60;" +
                        "-fx-border-radius: 5;" +
                        "-fx-border-width: 2;"
        );

        // ── EDIT/UPDATE PRODUCT FORM ──────────────────────
        Label editTitle = new Label("✏ Edit Product");
        editTitle.setFont(
                Font.font("Arial", FontWeight.BOLD, 14));

        TextField editIdField = new TextField();
        editIdField.setPromptText("Product ID to edit");
        editIdField.setPrefWidth(130);

        TextField editNameField = new TextField();
        editNameField.setPromptText("New Name");

        TextField editCategoryField = new TextField();
        editCategoryField.setPromptText("New Category");

        TextField editPriceField = new TextField();
        editPriceField.setPromptText("New Price");

        TextField editThresholdField = new TextField();
        editThresholdField.setPromptText("New Threshold");

        // Load button — fills fields with current values
        Button loadBtn = new Button("Load");
        loadBtn.setStyle(
                "-fx-background-color: #3498db;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 6 14 6 14;"
        );

        loadBtn.setOnAction(e -> {
            try {
                String id = editIdField.getText().trim();
                Product p = AppLauncher.inventoryManager
                        .findProductById(id);
                editNameField.setText(p.getName());
                editCategoryField.setText(p.getCategory());
                editPriceField.setText(
                        String.valueOf(p.getPrice()));
                editThresholdField.setText(
                        String.valueOf(
                                p.getLowStockThreshold()));
                messageLabel.setTextFill(Color.BLUE);
                messageLabel.setText(
                        "✔ Product loaded. Edit and save.");
            } catch (Exception ex) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText(ex.getMessage());
            }
        });

        Button saveEditBtn = new Button("Save Changes");
        saveEditBtn.setStyle(
                "-fx-background-color: #e67e22;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 6 14 6 14;"
        );

        saveEditBtn.setOnAction(e -> {
            try {
                String id       = editIdField.getText().trim();
                String newName  = editNameField.getText().trim();
                String newCat   = editCategoryField
                        .getText().trim();
                double newPrice = Double.parseDouble(
                        editPriceField.getText().trim());
                int newThresh   = Integer.parseInt(
                        editThresholdField.getText().trim());

                AppLauncher.inventoryManager.updateProduct(
                        id, newName, newCat,
                        newPrice, newThresh);

                messageLabel.setTextFill(Color.GREEN);
                messageLabel.setText(
                        "✔ Product '" + id + "' updated!");

                editIdField.clear();
                editNameField.clear();
                editCategoryField.clear();
                editPriceField.clear();
                editThresholdField.clear();

                refreshTable(table,
                        AppLauncher.inventoryManager
                                .getAllProducts());

            } catch (NumberFormatException ex) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText(
                        "Please enter valid numbers!");
            } catch (Exception ex) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText(ex.getMessage());
            }
        });

        GridPane editForm = new GridPane();
        editForm.setHgap(10);
        editForm.setVgap(8);
        editForm.setPadding(new Insets(10));
        editForm.add(editIdField,       0, 0);
        editForm.add(loadBtn,           1, 0);
        editForm.add(editNameField,     0, 1);
        editForm.add(editCategoryField, 1, 1);
        editForm.add(editPriceField,    0, 2);
        editForm.add(editThresholdField,1, 2);
        editForm.add(saveEditBtn,       0, 3);
        editForm.add(messageLabel,      1, 3);

        VBox editBox = new VBox(8, editTitle, editForm);
        editBox.setPadding(new Insets(10));
        editBox.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #e67e22;" +
                        "-fx-border-radius: 5;" +
                        "-fx-border-width: 2;"
        );

        // ── DELETE PRODUCT SECTION ────────────────────────
        Label deleteTitle = new Label("🗑 Delete Product");
        deleteTitle.setFont(
                Font.font("Arial", FontWeight.BOLD, 14));

        TextField deleteIdField = new TextField();
        deleteIdField.setPromptText(
                "Product ID to delete");
        deleteIdField.setPrefWidth(200);

        Button deleteBtn = new Button("Delete Product");
        deleteBtn.setStyle(
                "-fx-background-color: #e74c3c;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 6 14 6 14;"
        );

        deleteBtn.setOnAction(e -> {
            String id = deleteIdField.getText().trim();
            if (id.isEmpty()) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText(
                        "Please enter a Product ID!");
                return;
            }

            // Confirm before deleting
            Alert confirm = new Alert(
                    Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Delete");
            confirm.setHeaderText(
                    "Delete Product " + id + "?");
            confirm.setContentText(
                    "This action cannot be undone.");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        AppLauncher.inventoryManager
                                .removeProduct(id);
                        messageLabel.setTextFill(
                                Color.GREEN);
                        messageLabel.setText(
                                "✔ Product '" + id
                                        + "' deleted!");
                        deleteIdField.clear();
                        refreshTable(table,
                                AppLauncher.inventoryManager
                                        .getAllProducts());
                    } catch (Exception ex) {
                        messageLabel.setTextFill(Color.RED);
                        messageLabel.setText(
                                ex.getMessage());
                    }
                }
            });
        });

        HBox deleteBox = new HBox(10,
                deleteTitle, deleteIdField, deleteBtn);
        deleteBox.setPadding(new Insets(10));
        deleteBox.setStyle(
                "-fx-background-color: #fadbd8;" +
                        "-fx-border-color: #e74c3c;" +
                        "-fx-border-radius: 5;" +
                        "-fx-border-width: 2;"
        );

        // ── Forms row ─────────────────────────────────────
        HBox formsRow = new HBox(15, addBox, editBox);
        HBox.setHgrow(addBox, Priority.ALWAYS);
        HBox.setHgrow(editBox, Priority.ALWAYS);

        // ── Main layout ───────────────────────────────────
        view = new VBox(12,
                title, searchBar, table,
                formsRow, deleteBox);
        view.setPadding(new Insets(20));
        VBox.setVgrow(table, Priority.ALWAYS);
    }

    // ── Refresh table with given product list ─────────────
    private void refreshTable(TableView<Product> table,
                              List<Product> productList) {
        ObservableList<Product> data =
                FXCollections.observableArrayList(productList);
        table.setItems(data);
    }

    public VBox getView() {
        return view;
    }
}