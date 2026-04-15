package org.example.models;

public class Employee {
    private int ID;
    private String name;
    private String employeeType;

    public Employee() {
    }

    public Employee(int ID, String name, String employeeType) {
        this.ID = ID;
        this.name = name;
        this.employeeType = employeeType;
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

    @Override
    public String toString() {
        return "Employee{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                ", employeeType='" + employeeType + '\'' +
                '}';
    }
}