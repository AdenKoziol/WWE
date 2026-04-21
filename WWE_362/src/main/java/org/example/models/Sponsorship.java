package org.example.models;

public class Sponsorship {
    private int ID;
    private int sponsorID;
    private String sponsorName;
    private int eventID;
    private String eventName;
    private double amount;
    private String status;

    public Sponsorship() {
    }

    public Sponsorship(int ID, int sponsorID, String sponsorName, int eventID, String eventName, double amount, String status) {
        this.ID = ID;
        this.sponsorID = sponsorID;
        this.sponsorName = sponsorName;
        this.eventID = eventID;
        this.eventName = eventName;
        this.amount = amount;
        this.status = status;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getSponsorID() {
        return sponsorID;
    }

    public void setSponsorID(int sponsorID) {
        this.sponsorID = sponsorID;
    }

    public String getSponsorName() {
        return sponsorName;
    }

    public void setSponsorName(String sponsorName) {
        this.sponsorName = sponsorName;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean hasMissingInfo() {
        return
                isBlank(sponsorName) ||
                        isBlank(eventName) ||
                        isBlank(status);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "Sponsorship ID: " + ID +
                " | Sponsor ID: " + sponsorID +
                " | Sponsor Name: " + sponsorName +
                " | Event ID: " + eventID +
                " | Event Name: " + eventName +
                " | Amount: $" + amount +
                " | Status: " + status;
    }
}