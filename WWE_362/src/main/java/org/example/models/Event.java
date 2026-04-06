package org.example.models;

public class Event {
    private int ID;
    private String name;
    private String date;
    private String time;
    private int venueID;
    private String venueName;

    public Event() {
    }

    public Event(int ID, String name, String date, String time, int venueID, String venueName) {
        this.ID = ID;
        this.name = name;
        this.date = date;
        this.time = time;
        this.venueID = venueID;
        this.venueName = venueName;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getVenueID() {
        return venueID;
    }

    public void setVenueID(int venueID) {
        this.venueID = venueID;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public boolean hasMissingInfo() {
        return
                isBlank(name) ||
                isBlank(date) ||
                isBlank(time) ||
                isBlank(venueName);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "Event{" +
                "ID='" + ID + '\'' +
                ", name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", venueID='" + venueID + '\'' +
                ", venueName='" + venueName + '\'' +
                '}';
    }
}