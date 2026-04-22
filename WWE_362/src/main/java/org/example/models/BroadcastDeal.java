package org.example.models;

public class BroadcastDeal {
    private int ID;
    private int eventID;
    private String networkName;
    private double dealAmount;
    private String broadcastType;

    public BroadcastDeal() {}

    public BroadcastDeal(int ID, int eventID, String networkName, double dealAmount, String broadcastType) {
        this.ID = ID;
        this.eventID = eventID;
        this.networkName = networkName;
        this.dealAmount = dealAmount;
        this.broadcastType = broadcastType;
    }

    public int getID() { return ID; }
    public void setID(int ID) { this.ID = ID; }

    public int getEventID() { return eventID; }
    public void setEventID(int eventID) { this.eventID = eventID; }

    public String getNetworkName() { return networkName; }
    public void setNetworkName(String networkName) { this.networkName = networkName; }

    public double getDealAmount() { return dealAmount; }
    public void setDealAmount(double dealAmount) { this.dealAmount = dealAmount; }

    public String getBroadcastType() { return broadcastType; }
    public void setBroadcastType(String broadcastType) { this.broadcastType = broadcastType; }

    public boolean hasMissingInfo() {
        return networkName == null || networkName.trim().isEmpty() || 
               broadcastType == null || broadcastType.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "Broadcast Deal ID: " + ID +
               " | Event ID: " + eventID +
               " | Network: " + networkName +
               " | Type: " + broadcastType +
               " | Amount: $" + String.format("%.2f", dealAmount);
    }
}
