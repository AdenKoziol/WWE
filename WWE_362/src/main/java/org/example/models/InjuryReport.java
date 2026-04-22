package org.example.models;

public class InjuryReport {

    private int ID;
    private int wrestlerID;
    private int reportingStaffID;
    private String injuryType;
    private String description;
    private String date;

    public InjuryReport() {
    }

    public InjuryReport(int ID, int wrestlerID, int reportingStaffID, String injuryType, String description, String date) {
        this.ID = ID;
        this.wrestlerID = wrestlerID;
        this.reportingStaffID = reportingStaffID;
        this.injuryType = injuryType;
        this.description = description;
        this.date = date;
    }

    public int getID() { return ID; }
    public void setID(int ID) { this.ID = ID; }

    public int getWrestlerID() { return wrestlerID; }
    public void setWrestlerID(int wrestlerID) { this.wrestlerID = wrestlerID; }

    public int getReportingStaffID() { return reportingStaffID; }
    public void setReportingStaffID(int reportingStaffID) { this.reportingStaffID = reportingStaffID; }

    public String getInjuryType() { return injuryType; }
    public void setInjuryType(String injuryType) { this.injuryType = injuryType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public boolean hasMissingInfo() {
        return isBlank(injuryType) || isBlank(description) || isBlank(date);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    @Override
    public String toString() {
        return String.format(
            "Report ID: %-5d | Wrestler ID: %-5d | Staff ID: %-5d | Type: %-12s | Date: %s",
            ID, wrestlerID, reportingStaffID, injuryType, date
        );
    }
}