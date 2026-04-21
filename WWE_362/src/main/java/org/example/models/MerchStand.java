package org.example.models;

import java.util.HashMap;
import java.util.Map;

public class MerchStand {
    private String standID;
    private String location;
    // SKU -> Quantity
    private Map<String, Integer> localInventory; 

    public MerchStand() {
        this.localInventory = new HashMap<>();
    }

    public MerchStand(String standID, String location) {
        this.standID = standID;
        this.location = location;
        this.localInventory = new HashMap<>();
    }

    // Getters and Setters for JsonParser
    public String getStandID() { return standID; }
    public void setStandID(String standID) { this.standID = standID; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Map<String, Integer> getLocalInventory() { return localInventory; }
    public void setLocalInventory(Map<String, Integer> inv) { this.localInventory = inv; }
}