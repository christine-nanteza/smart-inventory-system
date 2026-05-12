package com.christine.smartinventorysystem.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Sale {
    private String saleId;
    private String productId;
    private String productName;
    private int quantitySold;
    private double pricePerUnit;
    private double totalAmount;
    private String cashierName;
    private LocalDateTime saleDateTime;
    private boolean isRefunded;

    public Sale(String saleId, String productId, String productName, int quantitySold, double pricePerUnit,
                String cashierName){
        this.saleId       = saleId;
        this.productId    = productId;
        this.productName  = productName;
        this.quantitySold = quantitySold;
        this.pricePerUnit = pricePerUnit;
        this.totalAmount  = quantitySold * pricePerUnit;
        this.cashierName  = cashierName;
        this.saleDateTime = LocalDateTime.now();
        this.isRefunded   = false;
    }

    // Constructor for restoring a sale from file
    public Sale(String saleId, String productId, String productName, int quantitySold, double pricePerUnit,
                String cashierName, LocalDateTime saleDateTime,
                boolean isRefunded) {
        this.saleId       = saleId;
        this.productId    = productId;
        this.productName  = productName;
        this.quantitySold = quantitySold;
        this.pricePerUnit = pricePerUnit;
        this.totalAmount  = quantitySold * pricePerUnit;
        this.cashierName  = cashierName;
        this.saleDateTime = saleDateTime;
        this.isRefunded   = isRefunded;
    }

    // Getters
    public String getSaleId()              { return saleId; }
    public String getProductId()           { return productId; }
    public String getProductName()         { return productName; }
    public int getQuantitySold()           { return quantitySold; }
    public double getPricePerUnit()        { return pricePerUnit; }
    public double getTotalAmount()         { return totalAmount; }
    public String getCashierName()         { return cashierName; }
    public LocalDateTime getSaleDateTime() { return saleDateTime; }
    public boolean isRefunded()            { return isRefunded; }

    // Setters
    public void setRefunded(boolean refunded) {
        this.isRefunded = refunded;
    }

    public void setSaleDateTime(LocalDateTime saleDateTime) {
        this.saleDateTime = saleDateTime;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return String.format(
                "%-10s %-10s %-20s QTY:%-5d UGX%-10.0f " +
                        "TOTAL:UGX%-10.0f %-15s %s %s",
                saleId, productId, productName, quantitySold,
                pricePerUnit, totalAmount, cashierName,
                saleDateTime.format(formatter),
                isRefunded ? "[REFUNDED]" : "");
    }
}