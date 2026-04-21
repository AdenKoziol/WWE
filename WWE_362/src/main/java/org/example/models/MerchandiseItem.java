package org.example.models;

public class MerchandiseItem {
    private int ID;
    private String name;
    private String sku;
    private double wholesaleCost;
    private double retailPrice;
    private int globalQuantity;

    public MerchandiseItem() {}

    public MerchandiseItem(int ID, String name, String sku, double wholesale, double retail, int quantity) {
        this.ID = ID;
        this.name = name;
        this.sku = sku;
        this.wholesaleCost = wholesale;
        this.retailPrice = retail;
        this.globalQuantity = quantity;
    }

    // Logic: Business Intelligence for the "Boss"
    public double getProfitMargin() {
        if (retailPrice <= 0) return 0;
        return ((retailPrice - wholesaleCost) / retailPrice) * 100;
    }

    public boolean isLowMargin() {
        return getProfitMargin() < 15.0;
    }

    // Getters and Setters...
    public int getID() { return ID; }
    public String getSku() { return sku; }
    public int getGlobalQuantity() { return globalQuantity; }
    public void setGlobalQuantity(int qty) { this.globalQuantity = qty; }
    public String getName() { return name; }
    public double getRetailPrice() { return retailPrice; }
    public double getWholesaleCost() { return wholesaleCost; }

    @Override
    public String toString() {
        return String.format("ID: %d | %-15s | SKU: %-8s | Margin: %.2f%% | Stock: %d", 
                ID, name, sku, getProfitMargin(), globalQuantity);
    }
} 
