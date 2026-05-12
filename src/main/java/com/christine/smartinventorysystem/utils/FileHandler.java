package com.christine.smartinventorysystem.utils;

import com.christine.smartinventorysystem.models.*;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    private static final String PRODUCTS_FILE = "data/products.txt";
    private static final String SALES_FILE    = "data/sales.txt";

    // ── Ensure data folder exists ─────────────────────────
    private static void ensureDataFolder() {
        File folder = new File("data");
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    // ── Save all products ─────────────────────────────────
    public static void saveProducts(List<Product> products) {
        ensureDataFolder();
        try (PrintWriter writer = new PrintWriter(
                new FileWriter(PRODUCTS_FILE))) {
            for (Product p : products) {
                if (p instanceof PerishableProduct) {
                    PerishableProduct pp = (PerishableProduct) p;
                    writer.println("PERISHABLE,"        +
                            pp.getProductId()           + "," +
                            pp.getName()                + "," +
                            pp.getCategory()            + "," +
                            pp.getPrice()               + "," +
                            pp.getQuantity()            + "," +
                            pp.getLowStockThreshold()   + "," +
                            pp.getExpiryDate());
                } else {
                    NonPerishableProduct np =
                            (NonPerishableProduct) p;
                    writer.println("NONPERISHABLE,"     +
                            np.getProductId()           + "," +
                            np.getName()                + "," +
                            np.getCategory()            + "," +
                            np.getPrice()               + "," +
                            np.getQuantity()            + "," +
                            np.getLowStockThreshold()   + "," +
                            np.getWarrantyMonths());
                }
            }
            System.out.println("Products saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving products: "
                    + e.getMessage());
        }
    }

    // ── Load all products ─────────────────────────────────
    public static List<Product> loadProducts() {
        List<Product> products = new ArrayList<>();
        File file = new File(PRODUCTS_FILE);
        if (!file.exists()) return products;

        try (BufferedReader reader = new BufferedReader(
                new FileReader(PRODUCTS_FILE))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                try {
                    if (parts[0].equals("PERISHABLE")
                            && parts.length == 8) {
                        products.add(new PerishableProduct(
                                parts[1], parts[2], parts[3],
                                Double.parseDouble(parts[4]),
                                Integer.parseInt(parts[5]),
                                Integer.parseInt(parts[6]),
                                LocalDate.parse(parts[7])));

                    } else if (parts[0].equals("NONPERISHABLE")
                            && parts.length == 8) {
                        products.add(new NonPerishableProduct(
                                parts[1], parts[2], parts[3],
                                Double.parseDouble(parts[4]),
                                Integer.parseInt(parts[5]),
                                Integer.parseInt(parts[6]),
                                Integer.parseInt(parts[7])));
                    } else {
                        System.out.println(
                                "Skipping malformed line "
                                        + lineNumber + ": " + line);
                    }
                } catch (NumberFormatException |
                         java.time.format.DateTimeParseException e) {
                    System.out.println("Error parsing line "
                            + lineNumber + ": " + e.getMessage());
                }
            }
            System.out.println("Loaded " + products.size()
                    + " products.");
        } catch (IOException e) {
            System.out.println("Error loading products: "
                    + e.getMessage());
        }
        return products;
    }

    // ── Save all sales ────────────────────────────────────
    public static void saveSales(List<Sale> sales) {
        ensureDataFolder();
        try (PrintWriter writer = new PrintWriter(
                new FileWriter(SALES_FILE))) {
            for (Sale s : sales) {
                writer.println(
                        s.getSaleId()                   + "," +
                                s.getProductId()                + "," +
                                s.getProductName()              + "," +
                                s.getQuantitySold()             + "," +
                                s.getPricePerUnit()             + "," +
                                s.getCashierName()              + "," +
                                s.getSaleDateTime().toString()  + "," +
                                s.isRefunded());
            }
            System.out.println("Sales saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving sales: "
                    + e.getMessage());
        }
    }

    // ── Load all sales ────────────────────────────────────
    public static List<Sale> loadSales() {
        List<Sale> sales = new ArrayList<>();
        File file = new File(SALES_FILE);
        if (!file.exists()) return sales;

        try (BufferedReader reader = new BufferedReader(
                new FileReader(SALES_FILE))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                try {
                    if (parts.length == 8) {
                        Sale s = new Sale(
                                parts[0],
                                parts[1],
                                parts[2],
                                Integer.parseInt(parts[3]),
                                Double.parseDouble(parts[4]),
                                parts[5],
                                LocalDateTime.parse(parts[6]),
                                Boolean.parseBoolean(parts[7]));
                        sales.add(s);
                    } else {
                        System.out.println(
                                "Skipping malformed sale line "
                                        + lineNumber);
                    }
                } catch (NumberFormatException |
                         java.time.format.DateTimeParseException e) {
                    System.out.println("Error parsing sale line "
                            + lineNumber + ": " + e.getMessage());
                }
            }
            System.out.println("Loaded " + sales.size()
                    + " sales.");
        } catch (IOException e) {
            System.out.println("Error loading sales: "
                    + e.getMessage());
        }
        return sales;
    }
}