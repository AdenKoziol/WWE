package org.example.models;

public class MedicalStaffAssignment {

    private int employeeID;
    private int eventID;

    public MedicalStaffAssignment() {
    }

    public MedicalStaffAssignment(int employeeID, int eventID) {
        this.employeeID = employeeID;
        this.eventID = eventID;
    }

    public int getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(int employeeID) {
        this.employeeID = employeeID;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    @Override
    public String toString() {
        return String.format("Employee ID: %-5d | Event ID: %-5d",
                employeeID, eventID);
    }
}