package org.example.models;

public class SecurityIncident {

    private int ID;
    private int eventID;
    private int reportedByEmployeeID;
    private int assignedToEmployeeID;
    private int resolvedByEmployeeID;
    private String incidentType;
    private String location;
    private String description;
    private String status;
    private String escalationTrail;
    private String resolutionNotes;

    public SecurityIncident() {
    }

    public SecurityIncident(int ID, int eventID, int reportedByEmployeeID, int assignedToEmployeeID,
                            int resolvedByEmployeeID, String incidentType, String location,
                            String description, String status, String escalationTrail, String resolutionNotes) {
        this.ID = ID;
        this.eventID = eventID;
        this.reportedByEmployeeID = reportedByEmployeeID;
        this.assignedToEmployeeID = assignedToEmployeeID;
        this.resolvedByEmployeeID = resolvedByEmployeeID;
        this.incidentType = incidentType;
        this.location = location;
        this.description = description;
        this.status = status;
        this.escalationTrail = escalationTrail;
        this.resolutionNotes = resolutionNotes;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public int getReportedByEmployeeID() {
        return reportedByEmployeeID;
    }

    public void setReportedByEmployeeID(int reportedByEmployeeID) {
        this.reportedByEmployeeID = reportedByEmployeeID;
    }

    public int getAssignedToEmployeeID() {
        return assignedToEmployeeID;
    }

    public void setAssignedToEmployeeID(int assignedToEmployeeID) {
        this.assignedToEmployeeID = assignedToEmployeeID;
    }

    public int getResolvedByEmployeeID() {
        return resolvedByEmployeeID;
    }

    public void setResolvedByEmployeeID(int resolvedByEmployeeID) {
        this.resolvedByEmployeeID = resolvedByEmployeeID;
    }

    public String getIncidentType() {
        return incidentType;
    }

    public void setIncidentType(String incidentType) {
        this.incidentType = incidentType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEscalationTrail() {
        return escalationTrail;
    }

    public void setEscalationTrail(String escalationTrail) {
        this.escalationTrail = escalationTrail;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }

    public void setResolutionNotes(String resolutionNotes) {
        this.resolutionNotes = resolutionNotes;
    }

    public boolean hasMissingInfo() {
        return isBlank(incidentType) ||
               isBlank(location) ||
               isBlank(description);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    @Override
    public String toString() {
        return String.format(
            "Incident ID: %-5d | Event ID: %-5d | Assigned To: %-5d | Type: %-18s | Status: %-10s | Location: %s",
            ID, eventID, assignedToEmployeeID, incidentType, status, location
        );
    }
}