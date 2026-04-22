package org.example.models;

public class WellnessCheck {

    private int ID;
    private int wrestlerID;
    private int staffID;
    private String date;
    private String status;
    private String notes;

    public WellnessCheck() {
    }

    public WellnessCheck(int ID, int wrestlerID, int staffID, String date, String status, String notes) {
        this.ID = ID;
        this.wrestlerID = wrestlerID;
        this.staffID = staffID;
        this.date = date;
        this.status = status;
        this.notes = notes;
    }

    public int getID() { return ID; }
    public void setID(int ID) { this.ID = ID; }

    public int getWrestlerID() { return wrestlerID; }
    public void setWrestlerID(int wrestlerID) { this.wrestlerID = wrestlerID; }

    public int getStaffID() { return staffID; }
    public void setStaffID(int staffID) { this.staffID = staffID; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean hasMissingInfo() {
        return date == null || date.trim().isEmpty();
    }
}