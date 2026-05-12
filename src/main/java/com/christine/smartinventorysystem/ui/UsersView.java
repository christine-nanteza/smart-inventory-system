package com.christine.smartinventorysystem.ui;

import com.christine.smartinventorysystem.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class UsersView {

    private VBox view;

    public UsersView() {

        // ── Title ─────────────────────────────────────────
        Label title = new Label("👥 Manage Users");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#2c3e50"));

        // ── Users Table ───────────────────────────────────
        TableView<User> table = new TableView<>();
        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No users found."));

        TableColumn<User, String> usernameCol =
                new TableColumn<>("Username");
        usernameCol.setCellValueFactory(
                new PropertyValueFactory<>("username"));

        TableColumn<User, String> fullNameCol =
                new TableColumn<>("Full Name");
        fullNameCol.setCellValueFactory(
                new PropertyValueFactory<>("fullName"));

        TableColumn<User, String> roleCol =
                new TableColumn<>("Role");
        roleCol.setCellValueFactory(
                new PropertyValueFactory<>("role"));

        // ── Custom Status column ──────────────────────────
        TableColumn<User, Boolean> activeCol =
                new TableColumn<>("Account Status");
        activeCol.setCellValueFactory(
                new PropertyValueFactory<>("active"));
        activeCol.setCellFactory(col ->
                new TableCell<User, Boolean>() {
                    @Override
                    protected void updateItem(Boolean active,
                                              boolean empty) {
                        super.updateItem(active, empty);
                        if (empty || active == null) {
                            setText(null);
                            setStyle("");
                        } else if (active) {
                            setText("✔ Active");
                            setStyle(
                                    "-fx-text-fill: #27ae60;" +
                                            "-fx-font-weight: bold;");
                        } else {
                            setText("✗ Inactive");
                            setStyle(
                                    "-fx-text-fill: #e74c3c;" +
                                            "-fx-font-weight: bold;");
                        }
                    }
                });

        table.getColumns().addAll(
                usernameCol, fullNameCol,
                roleCol, activeCol);

        refreshTable(table);

        // ── Shared message label ──────────────────────────
        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("Arial", 12));

        // ── ADD USER FORM ─────────────────────────────────
        Label addTitle = new Label("➕ Add New User");
        addTitle.setFont(
                Font.font("Arial", FontWeight.BOLD, 14));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(
                "Password (min 4 chars)");

        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Full Name");

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("ADMIN", "CASHIER");
        roleBox.setPromptText("Select Role");
        roleBox.setPrefWidth(180);

        Button addBtn = new Button("Add User");
        addBtn.setStyle(
                "-fx-background-color: #2980b9;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 6 14 6 14;"
        );

        addBtn.setOnAction(e -> {
            try {
                String username =
                        usernameField.getText().trim();
                String password =
                        passwordField.getText().trim();
                String fullName =
                        fullNameField.getText().trim();
                String roleStr  = roleBox.getValue();

                if (username.isEmpty()
                        || password.isEmpty()
                        || fullName.isEmpty()
                        || roleStr == null) {
                    messageLabel.setTextFill(Color.RED);
                    messageLabel.setText(
                            "Please fill all fields!");
                    return;
                }

                User.Role role = roleStr.equals("ADMIN")
                        ? User.Role.ADMIN
                        : User.Role.CASHIER;

                AppLauncher.userManager.registerUser(
                        username, password, fullName, role);

                messageLabel.setTextFill(Color.GREEN);
                messageLabel.setText(
                        "✔ User '" + username + "' added!");

                usernameField.clear();
                passwordField.clear();
                fullNameField.clear();
                roleBox.setValue(null);
                refreshTable(table);

            } catch (Exception ex) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText(ex.getMessage());
            }
        });

        GridPane addForm = new GridPane();
        addForm.setHgap(10);
        addForm.setVgap(8);
        addForm.setPadding(new Insets(10));
        addForm.add(usernameField, 0, 0);
        addForm.add(passwordField, 1, 0);
        addForm.add(fullNameField, 0, 1);
        addForm.add(roleBox,       1, 1);
        addForm.add(addBtn,        0, 2);
        addForm.add(messageLabel,  1, 2);

        VBox addBox = new VBox(8, addTitle, addForm);
        addBox.setPadding(new Insets(10));
        addBox.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #2980b9;" +
                        "-fx-border-radius: 5;" +
                        "-fx-border-width: 2;"
        );

        // ── EDIT USER FORM ────────────────────────────────
        Label editTitle = new Label("✏ Edit User");
        editTitle.setFont(
                Font.font("Arial", FontWeight.BOLD, 14));

        TextField editUsernameField = new TextField();
        editUsernameField.setPromptText(
                "Username to edit");
        editUsernameField.setPrefWidth(150);

        Button loadUserBtn = new Button("Load");
        loadUserBtn.setStyle(
                "-fx-background-color: #3498db;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 6 14 6 14;"
        );

        TextField editFullNameField = new TextField();
        editFullNameField.setPromptText("New Full Name");

        PasswordField editPasswordField =
                new PasswordField();
        editPasswordField.setPromptText(
                "New Password (leave blank to keep)");

        ComboBox<String> editRoleBox = new ComboBox<>();
        editRoleBox.getItems().addAll("ADMIN", "CASHIER");
        editRoleBox.setPromptText("New Role");
        editRoleBox.setPrefWidth(180);

        loadUserBtn.setOnAction(e -> {
            try {
                String username =
                        editUsernameField.getText().trim();
                User u = AppLauncher.userManager
                        .findUserByUsername(username);
                if (u == null) {
                    messageLabel.setTextFill(Color.RED);
                    messageLabel.setText(
                            "User '" + username
                                    + "' not found!");
                    return;
                }
                editFullNameField.setText(u.getFullName());
                editRoleBox.setValue(
                        u.getRole().toString());
                editPasswordField.clear();
                messageLabel.setTextFill(Color.BLUE);
                messageLabel.setText(
                        "✔ User loaded. Edit and save.");
            } catch (Exception ex) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText(ex.getMessage());
            }
        });

        Button saveUserBtn = new Button("Save Changes");
        saveUserBtn.setStyle(
                "-fx-background-color: #e67e22;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 6 14 6 14;"
        );

        saveUserBtn.setOnAction(e -> {
            try {
                String username =
                        editUsernameField.getText().trim();
                String newFullName =
                        editFullNameField.getText().trim();
                String newPassword =
                        editPasswordField.getText().trim();
                String newRoleStr = editRoleBox.getValue();

                if (username.isEmpty()) {
                    messageLabel.setTextFill(Color.RED);
                    messageLabel.setText(
                            "Please load a user first!");
                    return;
                }

                User u = AppLauncher.userManager
                        .findUserByUsername(username);
                if (u == null) {
                    messageLabel.setTextFill(Color.RED);
                    messageLabel.setText(
                            "User not found!");
                    return;
                }

                // Update full name
                if (!newFullName.isEmpty()) {
                    u.setFullName(newFullName);
                }

                // Update password only if provided
                if (!newPassword.isEmpty()) {
                    if (newPassword.length() < 4) {
                        messageLabel.setTextFill(Color.RED);
                        messageLabel.setText(
                                "Password must be at least "
                                        + "4 characters!");
                        return;
                    }
                    u.setPassword(newPassword);
                }

                // Update role
                if (newRoleStr != null) {
                    u.setRole(newRoleStr.equals("ADMIN")
                            ? User.Role.ADMIN
                            : User.Role.CASHIER);
                }

                messageLabel.setTextFill(Color.GREEN);
                messageLabel.setText(
                        "✔ User '" + username
                                + "' updated!");

                editUsernameField.clear();
                editFullNameField.clear();
                editPasswordField.clear();
                editRoleBox.setValue(null);
                refreshTable(table);

            } catch (Exception ex) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText(ex.getMessage());
            }
        });

        GridPane editForm = new GridPane();
        editForm.setHgap(10);
        editForm.setVgap(8);
        editForm.setPadding(new Insets(10));
        editForm.add(editUsernameField, 0, 0);
        editForm.add(loadUserBtn,       1, 0);
        editForm.add(editFullNameField, 0, 1);
        editForm.add(editPasswordField, 1, 1);
        editForm.add(editRoleBox,       0, 2);
        editForm.add(saveUserBtn,       1, 2);
        editForm.add(messageLabel,      0, 3);

        VBox editBox = new VBox(8, editTitle, editForm);
        editBox.setPadding(new Insets(10));
        editBox.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #e67e22;" +
                        "-fx-border-radius: 5;" +
                        "-fx-border-width: 2;"
        );

        // ── Forms row ─────────────────────────────────────
        HBox formsRow = new HBox(15, addBox, editBox);
        HBox.setHgrow(addBox, Priority.ALWAYS);
        HBox.setHgrow(editBox, Priority.ALWAYS);

        // ── DEACTIVATE SECTION ────────────────────────────
        TextField deactivateField = new TextField();
        deactivateField.setPromptText(
                "Username to deactivate");
        deactivateField.setPrefWidth(200);

        Button deactivateBtn = new Button("Deactivate");
        deactivateBtn.setStyle(
                "-fx-background-color: #e67e22;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 6 14 6 14;"
        );

        deactivateBtn.setOnAction(e -> {
            try {
                String username =
                        deactivateField.getText().trim();
                AppLauncher.userManager
                        .deactivateUser(username);
                messageLabel.setTextFill(Color.ORANGE);
                messageLabel.setText(
                        "⚠ User '" + username
                                + "' deactivated!");
                deactivateField.clear();
                refreshTable(table);
            } catch (Exception ex) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText(ex.getMessage());
            }
        });

        HBox deactivateBox = new HBox(10,
                new Label("Deactivate User:"),
                deactivateField, deactivateBtn);
        deactivateBox.setPadding(new Insets(10));
        deactivateBox.setStyle(
                "-fx-background-color: #ffeaa7;" +
                        "-fx-border-color: #fdcb6e;" +
                        "-fx-border-radius: 5;" +
                        "-fx-border-width: 2;"
        );

        // ── Main layout ───────────────────────────────────
        view = new VBox(15,
                title, table, formsRow, deactivateBox);
        view.setPadding(new Insets(20));
        VBox.setVgrow(table, Priority.ALWAYS);
    }

    private void refreshTable(TableView<User> table) {
        ObservableList<User> data =
                FXCollections.observableArrayList(
                        AppLauncher.userManager
                                .getAllUsers());
        table.setItems(data);
    }

    public VBox getView() {
        return view;
    }
}