package org.example.models;

public class Costume {
    private int ID;
    private int talentID;
    private String theme;
    private String color;
    private double cost;

    public Costume() {
    }

    public Costume(int ID, int talentID, String theme, String color, double cost) {
        this.ID = ID;
        this.talentID = talentID;
        this.theme = theme;
        this.color = color;
        this.cost = cost;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getTalentID() {
        return talentID;
    }

    public void setTalentID(int talentID) {
        this.talentID = talentID;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public boolean hasMissingInfo() {
        return
                isBlank(theme) ||
                isBlank(color);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

@Override
public String toString() {
    return String.format("Costume ID: %-5d | Talent ID: %-5d | Theme: %-15s | Color: %-10s | Cost: $%.2f", 
                          ID, talentID, theme, color, cost);
}
}