package org.example.models;

public class FlightBooking {

    private int ID;
    private String wrestlerName;
    private String destination;
    private String date;
    private String flightType;

    public FlightBooking() {
    }

    public FlightBooking(int ID, String wrestlerName, String destination, String date, String flightType) {
        this.ID = ID;
        this.wrestlerName = wrestlerName;
        this.destination = destination;
        this.date = date;
        this.flightType = flightType;
    }

    public FlightBooking(String wrestlerName, String destination, String date, String flightType) {
        this.wrestlerName = wrestlerName;
        this.destination = destination;
        this.date = date;
        this.flightType = flightType;
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

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFlightType() {
        return flightType;
    }

    public void setFlightType(String flightType) {
        this.flightType = flightType;
    }

    @Override
    public String toString() {
        return "FlightBooking{" +
                "ID=" + ID +
                ", wrestlerName='" + wrestlerName + '\'' +
                ", destination='" + destination + '\'' +
                ", date='" + date + '\'' +
                ", flightType='" + flightType + '\'' +
                '}';
    }
}