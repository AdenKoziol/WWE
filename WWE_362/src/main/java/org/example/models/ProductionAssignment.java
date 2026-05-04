package org.example.models;

public class ProductionAssignment {
    private int ID;
    private int crewID;
    private String crewName;
    private int eventID;
    private String eventName;
    private String role;
    private String status;

    public ProductionAssignment() {
    }

    public ProductionAssignment(int ID, int crewID, String crewName, int eventID, String eventName, String role, String status) {
        this.ID = ID;
        this.crewID = crewID;
        this.crewName = crewName;
        this.eventID = eventID;
        this.eventName = eventName;
        this.role = role;
        this.status = status;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getCrewID() {
        return crewID;
    }

    public void setCrewID(int crewID) {
        this.crewID = crewID;
    }

    public String getCrewName() {
        return crewName;
    }

    public void setCrewName(String crewName) {
        this.crewName = crewName;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean hasMissingInfo() {
        return isBlank(crewName) ||
                isBlank(eventName) ||
                isBlank(role) ||
                isBlank(status);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "Production Assignment ID: " + ID +
                " | Crew ID: " + crewID +
                " | Crew Name: " + crewName +
                " | Event ID: " + eventID +
                " | Event Name: " + eventName +
                " | Role: " + role +
                " | Status: " + status;
    }
}