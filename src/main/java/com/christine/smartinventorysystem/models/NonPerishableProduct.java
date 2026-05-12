package com.christine.smartinventorysystem.models;

public class NonPerishableProduct extends Product{
    private int warrantyMonths;

    public NonPerishableProduct(String productId, String name, String category, double price,
                                int quantity, int lowStockThreshold, int warrantyMonths){

        super(productId, name, category, price, quantity, lowStockThreshold);
        this.warrantyMonths = warrantyMonths;
    }

    public int getWarrantyMonths()                  { return warrantyMonths; }
    public void setWarrantyMonths(int warrantyMonths) { this.warrantyMonths = warrantyMonths; }

    @Override
    public boolean isExpired(){
        return false;
    }

    @Override
    public String getProductType() {
        return "Non-Perishable";
    }

    @Override
    public String toString() {
        String warranty = warrantyMonths > 0
                ? "Warranty: " + warrantyMonths + " months"
                : "No Warranty";
        return super.toString() + " | " + warranty;
    }
}
