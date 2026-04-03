package org.example.models;

public class Venue {
    private String ID;
    private String name;
    private String location;

    public Venue() {
    }

    public Venue(String ID, String name, String location) {
        this.ID = ID;
        this.name = name;
        this.location = location;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
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

    @Override
    public String toString() {
        return "Venue{" +
                "ID='" + ID + '\'' +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}