package org.example.models;

public class ProductionCrew {
    private int ID;
    private String crewName;
    private String supervisorName;

    public ProductionCrew() {
    }

    public ProductionCrew(int ID, String crewName, String supervisorName) {
        this.ID = ID;
        this.crewName = crewName;
        this.supervisorName = supervisorName;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getCrewName() {
        return crewName;
    }

    public void setCrewName(String crewName) {
        this.crewName = crewName;
    }

    public String getSupervisorName() {
        return supervisorName;
    }

    public void setSupervisorName(String supervisorName) {
        this.supervisorName = supervisorName;
    }

    public boolean hasMissingInfo() {
        return isBlank(crewName) || isBlank(supervisorName);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "Production Crew ID: " + ID +
                " | Crew Name: " + crewName +
                " | Supervisor Name: " + supervisorName;
    }
}