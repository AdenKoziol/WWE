package org.example.models;

public class TransportationBooking {

    private int ID;
    private String wrestlerName;
    private String location;
    private String transportationType;

    public TransportationBooking() {
    }

    public TransportationBooking(int ID, String wrestlerName, String location, String transportationType) {
        this.ID = ID;
        this.wrestlerName = wrestlerName;
        this.location = location;
        this.transportationType = transportationType;
    }

    public TransportationBooking(String wrestlerName, String location, String transportationType) {
        this.wrestlerName = wrestlerName;
        this.location = location;
        this.transportationType = transportationType;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getWrestlerName() {
        return wrestlerName;
    }

    public void setWrestlerName(String wrestlerName) {
        this.wrestlerName = wrestlerName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTransportationType() {
        return transportationType;
    }

    public void setTransportationType(String transportationType) {
        this.transportationType = transportationType;
    }

    @Override
    public String toString() {
        return "TransportationBooking{" +
                "ID=" + ID +
                ", wrestlerName='" + wrestlerName + '\'' +
                ", location='" + location + '\'' +
                ", transportationType='" + transportationType + '\'' +
                '}';
    }
}