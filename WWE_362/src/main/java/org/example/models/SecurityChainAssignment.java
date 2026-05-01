package org.example.models;

public class SecurityChainAssignment {

    private int eventID;
    private int employeeID;
    private int reportsToEmployeeID;

    public SecurityChainAssignment() {
    }

    public SecurityChainAssignment(int eventID, int employeeID, int reportsToEmployeeID) {
        this.eventID = eventID;
        this.employeeID = employeeID;
        this.reportsToEmployeeID = reportsToEmployeeID;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public int getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(int employeeID) {
        this.employeeID = employeeID;
    }

    public int getReportsToEmployeeID() {
        return reportsToEmployeeID;
    }

    public void setReportsToEmployeeID(int reportsToEmployeeID) {
        this.reportsToEmployeeID = reportsToEmployeeID;
    }

    @Override
    public String toString() {
        return String.format(
            "Event ID: %-5d | Employee ID: %-5d | Reports To: %-5d",
            eventID, employeeID, reportsToEmployeeID
        );
    }
}