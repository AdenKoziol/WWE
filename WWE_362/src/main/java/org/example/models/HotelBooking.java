package org.example.models;

public class HotelBooking {

    private int ID;
    private String wrestlerName;
    private String location;
    private String date;
    private String hotelName;

    public HotelBooking() {
    }

    public HotelBooking(int ID, String wrestlerName, String location, String date, String hotelName) {
        this.ID = ID;
        this.wrestlerName = wrestlerName;
        this.location = location;
        this.date = date;
        this.hotelName = hotelName;
    }

    public HotelBooking(String wrestlerName, String location, String date, String hotelName) {
        this.wrestlerName = wrestlerName;
        this.location = location;
        this.date = date;
        this.hotelName = hotelName;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    @Override
    public String toString() {
        return "HotelBooking{" +
                "ID=" + ID +
                ", wrestlerName='" + wrestlerName + '\'' +
                ", location='" + location + '\'' +
                ", date='" + date + '\'' +
                ", hotelName='" + hotelName + '\'' +
                '}';
    }
}