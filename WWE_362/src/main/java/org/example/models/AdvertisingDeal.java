package org.example.models;

public class AdvertisingDeal {
    private int ID;
    private int eventID;
    private int broadcastDealID;
    private String advertiserName;
    private int adMinutes;
    private double costPerMinute;
    private double totalCost;
    private String placement;

    public AdvertisingDeal() {}

    public AdvertisingDeal(int ID, int eventID, int broadcastDealID, String advertiserName,
                           int adMinutes, double costPerMinute, String placement) {
        this.ID = ID;
        this.eventID = eventID;
        this.broadcastDealID = broadcastDealID;
        this.advertiserName = advertiserName;
        this.adMinutes = adMinutes;
        this.costPerMinute = costPerMinute;
        this.totalCost = adMinutes * costPerMinute;
        this.placement = placement;
    }

    public int getID() { return ID; }
    public void setID(int ID) { this.ID = ID; }

    public int getEventID() { return eventID; }
    public void setEventID(int eventID) { this.eventID = eventID; }

    public int getBroadcastDealID() { return broadcastDealID; }
    public void setBroadcastDealID(int broadcastDealID) { this.broadcastDealID = broadcastDealID; }

    public String getAdvertiserName() { return advertiserName; }
    public void setAdvertiserName(String advertiserName) { this.advertiserName = advertiserName; }

    public int getAdMinutes() { return adMinutes; }
    public void setAdMinutes(int adMinutes) {
        this.adMinutes = adMinutes;
        this.totalCost = adMinutes * costPerMinute;
    }

    public double getCostPerMinute() { return costPerMinute; }
    public void setCostPerMinute(double costPerMinute) {
        this.costPerMinute = costPerMinute;
        this.totalCost = adMinutes * costPerMinute;
    }

    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }

    public String getPlacement() { return placement; }
    public void setPlacement(String placement) { this.placement = placement; }

    public boolean hasMissingInfo() {
        return advertiserName == null || advertiserName.trim().isEmpty() ||
                placement == null || placement.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "Advertising Deal ID: " + ID +
                " | Event ID: " + eventID +
                " | Broadcast Deal ID: " + broadcastDealID +
                " | Advertiser: " + advertiserName +
                " | Placement: " + placement +
                " | Minutes: " + adMinutes +
                " | Rate: $" + String.format("%.2f", costPerMinute) + "/min" +
                " | Total: $" + String.format("%.2f", totalCost);
    }
}
