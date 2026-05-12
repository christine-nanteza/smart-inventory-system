package com.christine.smartinventorysystem.ui;

import com.christine.smartinventorysystem.managers.InventoryManager;
import com.christine.smartinventorysystem.managers.SalesManager;
import com.christine.smartinventorysystem.managers.UserManager;
import com.christine.smartinventorysystem.models.NonPerishableProduct;
import com.christine.smartinventorysystem.models.PerishableProduct;
import com.christine.smartinventorysystem.models.Product;
import com.christine.smartinventorysystem.models.Sale;
import com.christine.smartinventorysystem.services.AlertService;
import com.christine.smartinventorysystem.services.AnalyticsService;
import com.christine.smartinventorysystem.utils.FileHandler;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.List;

public class AppLauncher extends Application {

    // ── Shared system instances ───────────────────────────
    public static InventoryManager inventoryManager =
            new InventoryManager();
    public static SalesManager salesManager =
            new SalesManager(inventoryManager);
    public static UserManager userManager =
            new UserManager();
    public static AlertService alertService =
            new AlertService(inventoryManager);
    public static AnalyticsService analyticsService =
            new AnalyticsService(inventoryManager, salesManager);

    @Override
    public void start(Stage primaryStage) {
        initializeData();

        LoginScreen loginScreen = new LoginScreen(primaryStage);
        Scene scene = new Scene(
                loginScreen.getView(), 450, 350);
        primaryStage.setTitle(
                "Joan & Vicky Inventory Management System");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    // ── Initialize: load from file or add sample data ─────
    private void initializeData() {
        List<Product> savedProducts =
                FileHandler.loadProducts();
        List<Sale> savedSales =
                FileHandler.loadSales();

        if (!savedProducts.isEmpty()) {
            // Returning user — restore saved data
            for (Product p : savedProducts) {
                try {
                    inventoryManager.addProduct(p);
                } catch (Exception ignored) {}
            }
            for (Sale s : savedSales) {
                salesManager.restoreSale(s);
            }
            System.out.println("Data restored from file.");
        } else {
            // First run — load sample data then save it
            loadSampleData();
            FileHandler.saveProducts(
                    inventoryManager.getAllProducts());
            FileHandler.saveSales(
                    salesManager.getAllSales());
            System.out.println(
                    "Sample data loaded and saved.");
        }
    }

    // ── Sample data (first run only) ──────────────────────
    private void loadSampleData() {
        try {
            String milkId    =
                    inventoryManager.generateProductId();
            String breadId   =
                    inventoryManager.generateProductId();
            String yoghurtId =
                    inventoryManager.generateProductId();
            String chargerId =
                    inventoryManager.generateProductId();
            String bookId    =
                    inventoryManager.generateProductId();

            inventoryManager.addProduct(
                    new PerishableProduct(
                            milkId, "Fresh Milk", "Dairy",
                            3500, 50, 10,
                            LocalDate.now().plusDays(5)));

            inventoryManager.addProduct(
                    new PerishableProduct(
                            breadId, "Bread", "Bakery",
                            4000, 3, 8,
                            LocalDate.now().plusDays(2)));

            inventoryManager.addProduct(
                    new PerishableProduct(
                            yoghurtId, "Expired Yoghurt",
                            "Dairy", 2500, 5, 3,
                            LocalDate.now().minusDays(3)));

            inventoryManager.addProduct(
                    new NonPerishableProduct(
                            chargerId, "Samsung Charger",
                            "Electronics", 25000, 4, 5, 12));

            inventoryManager.addProduct(
                    new NonPerishableProduct(
                            bookId, "Exercise Book",
                            "Stationery", 1500, 200, 30, 0));

            // Sample sales using actual generated IDs
            salesManager.processSale(
                    milkId, 5, "System Admin");
            salesManager.processSale(
                    bookId, 10, "System Admin");

        } catch (Exception e) {
            System.out.println(
                    "Sample data warning: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}