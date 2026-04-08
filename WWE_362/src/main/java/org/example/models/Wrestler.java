package org.example.models;

public class Wrestler {
    private int ID;
    private String stageName;
    private String realName;
    private String hometown;
    private String style; 
    private boolean active;

    public Wrestler() {
    }

    public Wrestler(int ID, String stageName, String realName, String hometown, String style) {
        this.ID = ID;
        this.stageName = stageName;
        this.realName = realName;
        this.hometown = hometown;
        this.style = style;
        this.active = true; 
    }

    // Getters and Setters
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    public boolean hasMissingInfo() {
        return isBlank(stageName) || isBlank(realName) || isBlank(style);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    @Override
    public String toString() {
        String status = active ? "ACTIVE" : "INACTIVE";
        return String.format("Talent ID: %-5d | Name: %-15s | Style: %-12s | Status: %s", 
                             ID, stageName, style, status);
    }
}