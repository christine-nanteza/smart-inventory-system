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

public class LoginScreen {

    private VBox view;

    public LoginScreen(Stage stage) {

        // ── Title ─────────────────────────────────────────
        Label title = new Label("Joan & Vicky Inventory Management System");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#2c3e50"));

        Label subtitle = new Label("Kyengera | Victoria University Uganda");
        subtitle.setFont(Font.font("Arial", 13));
        subtitle.setTextFill(Color.web("#7f8c8d"));

        // ── Username field ────────────────────────────────
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setMaxWidth(280);
        usernameField.setPrefHeight(38);

        // ── Password field ────────────────────────────────
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setMaxWidth(280);
        passwordField.setPrefHeight(38);

        // ── Login button ──────────────────────────────────
        Button loginBtn = new Button("Login");
        loginBtn.setPrefWidth(280);
        loginBtn.setPrefHeight(40);
        loginBtn.setStyle(
                "-fx-background-color: #2980b9;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
        );

        // ── Error message ─────────────────────────────────
        Label messageLabel = new Label();
        messageLabel.setTextFill(Color.RED);
        messageLabel.setFont(Font.font("Arial", 12));

        // ── Default credentials hint ──────────────────────
        Label hint = new Label("Default: admin / admin123");
        hint.setTextFill(Color.web("#95a5a6"));
        hint.setFont(Font.font("Arial", 11));

        // ── Login button action ───────────────────────────
        loginBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Please enter username and password!");
                return;
            }

            try {
                User user = AppLauncher.userManager.login(username, password);
                if (user != null) {
                    // Go to dashboard
                    Dashboard dashboard = new Dashboard(stage, user);
                    stage.getScene().setRoot(dashboard.getView());
                    stage.setWidth(900);
                    stage.setHeight(650);
                    stage.centerOnScreen();
                } else {
                    messageLabel.setText("Invalid username or password!");
                    passwordField.clear();
                }
            } catch (Exception ex) {
                messageLabel.setText(ex.getMessage());
            }
        });

        // ── Allow Enter key to login ──────────────────────
        passwordField.setOnAction(e -> loginBtn.fire());

        // ── Layout ────────────────────────────────────────
        view = new VBox(12,
                title, subtitle,
                new Separator(),
                usernameField, passwordField,
                loginBtn, messageLabel, hint);
        view.setAlignment(Pos.CENTER);
        view.setPadding(new Insets(40));
        view.setStyle("-fx-background-color: #f5f6fa;");
    }

    public VBox getView() {
        return view;
    }
}