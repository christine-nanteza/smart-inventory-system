package com.christine.smartinventorysystem.models;
import java.time.LocalDate;

public class PerishableProduct extends Product{

    private LocalDate expiryDate;

    public PerishableProduct(String productId, String name, String category,
                             double price, int quantity, int lowStockThreshold, LocalDate expiryDate){

        super(productId, name, category, price, quantity, lowStockThreshold);
        this.expiryDate = expiryDate;
    }

    public LocalDate getExpiryDate()                            { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate)             { this.expiryDate = expiryDate; }

    @Override
        public boolean isExpired(){
        return LocalDate.now().isAfter(expiryDate);
        }
        public boolean isExpiringSoon(){
        return !isExpired() && LocalDate.now().plusDays(7). isAfter(expiryDate);
        }

        @Override
        public String getProductType(){
        return "Perishable";
    }

    @Override
    public String toString(){
        String expiryStatus = isExpired()           ?"❌ EXPIRED"
                            :isExpiringSoon()       ?"⚠ EXPIRING SOON"
                            :"✔ FRESH";
        return super.toString() + String.format(" | Expiry: %s [%s]",
                expiryDate, expiryStatus);

    }
}
