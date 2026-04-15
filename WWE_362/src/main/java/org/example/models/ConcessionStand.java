package org.example.models;

import java.util.List;

public class ConcessionStand {
    private int ID;
    private String name;
    private String location;
    private List<ConcessionItem> itemsSold;
    private List<Employee> employeesOnShift;

    public ConcessionStand() {
    }

    public ConcessionStand(int ID, String name, String location, List<ConcessionItem> itemsSold, List<Employee> employeesOnShift) {
        this.ID = ID;
        this.name = name;
        this.location = location;
        this.itemsSold = itemsSold;
        this.employeesOnShift = employeesOnShift;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<ConcessionItem> getItemsSold() {
        return itemsSold;
    }

    public void setItemsSold(List<ConcessionItem> itemsSold) {
        this.itemsSold = itemsSold;
    }

    public List<Employee> getEmployeesOnShift() {
        return employeesOnShift;
    }

    public void setEmployeesOnShift(List<Employee> employeesOnShift) {
        this.employeesOnShift = employeesOnShift;
    }

    @Override
    public String toString() {
        return "ConcessionStand{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", itemsSold=" + (itemsSold != null ? itemsSold.size() + " items" : "0 items") +
                ", employeesOnShift=" + (employeesOnShift != null ? employeesOnShift.size() + " employees" : "0 employees") +
                '}';
    }
}