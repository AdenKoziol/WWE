package org.example.models;

import java.util.ArrayList;
import java.util.List;

public class MerchStand {
    private String standID;
    private String location;
    private List<InventoryEntry> localInventory = new ArrayList<>();

    public MerchStand() {}

    public MerchStand(String standID, String location) {
        this.standID = standID;
        this.location = location;
        this.localInventory = new ArrayList<>();
    }

    // Business Logic: Find an entry by SKU within the list
    public InventoryEntry findEntry(String sku) {
        return localInventory.stream()
                .filter(e -> e.getSku().equalsIgnoreCase(sku))
                .findFirst()
                .orElse(null);
    }

    // Getters and Setters
    public String getStandID() { return standID; }
    public void setStandID(String standID) { this.standID = standID; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public List<InventoryEntry> getLocalInventory() { return localInventory; }
    public void setLocalInventory(List<InventoryEntry> localInventory) { this.localInventory = localInventory; }
}