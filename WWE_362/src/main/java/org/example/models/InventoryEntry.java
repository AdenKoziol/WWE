package org.example.models;

public class InventoryEntry {
    private String sku;
    private int quantity;

    public InventoryEntry() {} // Required for JsonParser

    public InventoryEntry(String sku, int quantity) {
        this.sku = sku;
        this.quantity = quantity;
    }

    // Getters and Setters
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}