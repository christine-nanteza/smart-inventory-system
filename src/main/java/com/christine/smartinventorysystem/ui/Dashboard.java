package com.christine.smartinventorysystem.ui;

import com.christine.smartinventorysystem.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Dashboard {

    private BorderPane view;

    public Dashboard(Stage stage, User user) {

        // ── Top bar ───────────────────────────────────────
        Label title = new Label("Joan & Vicky Inventory Management System");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.WHITE);

        Label userLabel = new Label("👤 " + user.getFullName()
                + "  |  " + user.getRole());
        userLabel.setTextFill(Color.web("#ecf0f1"));
        userLabel.setFont(Font.font("Arial", 13));

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle(
                "-fx-background-color: #e74c3c;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
        );
        logoutBtn.setOnAction(e -> {
            AppLauncher.userManager.logout();
            LoginScreen login = new LoginScreen(stage);
            stage.getScene().setRoot(login.getView());
            stage.setWidth(450);
            stage.setHeight(350);
            stage.centerOnScreen();
        });

        HBox topBar = new HBox(20, title, userLabel);
        topBar.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(topBar, Priority.ALWAYS);

        HBox topArea = new HBox(topBar, logoutBtn);
        topArea.setAlignment(Pos.CENTER);
        topArea.setPadding(new Insets(15, 20, 15, 20));
        topArea.setStyle("-fx-background-color: #2c3e50;");
        HBox.setHgrow(topBar, Priority.ALWAYS);

        // ── Menu buttons ──────────────────────────────────
        Button productsBtn  = createMenuButton("📦  Manage Products", "#3498db");
        Button salesBtn     = createMenuButton("🛒  Process Sales",   "#27ae60");
        Button inventoryBtn = createMenuButton("📊  View Inventory",  "#8e44ad");
        Button reportsBtn   = createMenuButton("📋  View Reports",    "#e67e22");
        Button analyticsBtn = createMenuButton("📈  Analytics",       "#16a085");
        Button usersBtn     = createMenuButton("👥  Manage Users",    "#2980b9");

        // ── Content area ──────────────────────────────────
        StackPane contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color: #ecf0f1;");

        Label welcomeLabel = new Label(
                "Welcome, " + user.getFullName() + "!\n\n"
                        + "Select an option from the menu to get started.");
        welcomeLabel.setFont(Font.font("Arial", 16));
        welcomeLabel.setTextFill(Color.web("#7f8c8d"));
        welcomeLabel.setAlignment(Pos.CENTER);
        contentArea.getChildren().add(welcomeLabel);

        // ── Button actions ────────────────────────────────
        productsBtn.setOnAction(e -> {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(new ProductView().getView());
        });

        salesBtn.setOnAction(e -> {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(new SalesView().getView());
        });

        inventoryBtn.setOnAction(e -> {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(new InventoryView().getView());
        });

        reportsBtn.setOnAction(e -> {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(new ReportsView().getView());
        });

        analyticsBtn.setOnAction(e -> {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(new AnalyticsView().getView());
        });

        // ── Show users only for admin ─────────────────────
        if (user.isAdmin()) {
            usersBtn.setOnAction(e -> {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(new UsersView().getView());
            });
        } else {
            usersBtn.setDisable(true);
            usersBtn.setOpacity(0.4);
        }

        // ── Side menu ─────────────────────────────────────
        // ── Side menu — role based ────────────────────────────
        VBox sideMenu;
        if (user.isAdmin()) {
            sideMenu = new VBox(8,
                    productsBtn, salesBtn, inventoryBtn,
                    reportsBtn, analyticsBtn, usersBtn);
        } else {
            // Cashier only sees sales and inventory
            sideMenu = new VBox(8,
                    salesBtn, inventoryBtn);
        }
        sideMenu.setPadding(new Insets(20, 10, 20, 10));
        sideMenu.setStyle("-fx-background-color: #34495e;");
        sideMenu.setPrefWidth(200);

        // ── Main layout ───────────────────────────────────
        view = new BorderPane();
        view.setTop(topArea);
        view.setLeft(sideMenu);
        view.setCenter(contentArea);
    }

    // ── Helper: create styled menu button ─────────────────
    private Button createMenuButton(String text, String color) {
        Button btn = new Button(text);
        btn.setPrefWidth(180);
        btn.setPrefHeight(42);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #ecf0f1;" +
                        "-fx-font-size: 13px;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 0 0 0 10;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 0 0 0 10;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #ecf0f1;" +
                        "-fx-font-size: 13px;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 0 0 0 10;"
        ));
        return btn;
    }

    public BorderPane getView() {
        return view;
    }
}