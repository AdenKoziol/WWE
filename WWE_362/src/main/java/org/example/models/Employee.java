package org.example.models;

public class Employee {
    private int ID;
    private String name;
    private String employeeType;
    private String employeeRole;

    public Employee() {
    }

    public Employee(int ID, String name, String employeeType) {
        this.ID = ID;
        this.name = name;
        this.employeeType = employeeType;
        this.employeeRole = "";
    }

    public Employee(int ID, String name, String employeeType, String employeeRole) {
        this.ID = ID;
        this.name = name;
        this.employeeType = employeeType;
        this.employeeRole = employeeRole;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmployeeType() {
        return employeeType;
    }

    public void setEmployeeType(String employeeType) {
        this.employeeType = employeeType;
    }

    public String getEmployeeRole() {
        return employeeRole;
    }

    public void setEmployeeRole(String employeeRole) {
        this.employeeRole = employeeRole;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                ", employeeType='" + employeeType + '\'' +
                ", employeeRole='" + employeeRole + '\'' +
                '}';
    }
}