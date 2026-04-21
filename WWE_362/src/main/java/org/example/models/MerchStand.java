package org.example.models;

import java.util.ArrayList;
import java.util.List;

public class MerchStand {
    private String standID;
    private String location;
    private List<InventoryEntry> localInventory = new ArrayList<>();
    private double profit = 0.0;
    private List<Employee> staffOnShift = new ArrayList<>();

    public MerchStand() {}

    public MerchStand(String standID, String location) {
        this.standID = standID;
        this.location = location;
        this.localInventory = new ArrayList<>();
    }

    public InventoryEntry findEntry(String sku) {
        return localInventory.stream()
                .filter(e -> e.getSku().equalsIgnoreCase(sku))
                .findFirst()
                .orElse(null);
    }

    public List<Employee> getStaffOnShift() { return staffOnShift; }
    public void setStaffOnShift(List<Employee> staffOnShift) { this.staffOnShift = staffOnShift; }
    public String getStandID() { return standID; }
    public void setStandID(String standID) { this.standID = standID; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public List<InventoryEntry> getLocalInventory() { return localInventory; }
    public void setLocalInventory(List<InventoryEntry> localInventory) { this.localInventory = localInventory; }
    public double getProfit() { return profit;}
    public void setProfit(double profit) {this.profit += profit;}   
    public void makeSale(double sale) {this.profit += sale;}
}