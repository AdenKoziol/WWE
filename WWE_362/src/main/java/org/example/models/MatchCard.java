package org.example.models;

public class MatchCard {
    private int ID;
    private int eventID;
    private String eventName;
    private String wrestler1;
    private String wrestler2;
    private String matchType;

    public MatchCard() {
    }

    public MatchCard(int ID, int eventID, String eventName, String wrestler1, String wrestler2, String matchType) {
        this.ID = ID;
        this.eventID = eventID;
        this.eventName = eventName;
        this.wrestler1 = wrestler1;
        this.wrestler2 = wrestler2;
        this.matchType = matchType;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getWrestler1() {
        return wrestler1;
    }

    public void setWrestler1(String wrestler1) {
        this.wrestler1 = wrestler1;
    }

    public String getWrestler2() {
        return wrestler2;
    }

    public void setWrestler2(String wrestler2) {
        this.wrestler2 = wrestler2;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public boolean hasMissingInfo() {
        return
                isBlank(eventName) ||
                isBlank(wrestler1) ||
                isBlank(wrestler2) ||
                isBlank(matchType);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "MatchCard{" +
                "ID='" + ID + '\'' +
                ", eventID='" + eventID + '\'' +
                ", eventName='" + eventName + '\'' +
                ", wrestler1='" + wrestler1 + '\'' +
                ", wrestler2='" + wrestler2 + '\'' +
                ", matchType='" + matchType + '\'' +
                '}';
    }
}