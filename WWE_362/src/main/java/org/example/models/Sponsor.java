package org.example.models;

public class Sponsor {
    private int ID;
    private String name;
    private String contactName;
    private String contactEmail;

    public Sponsor() {
    }

    public Sponsor(int ID, String name, String contactName, String contactEmail) {
        this.ID = ID;
        this.name = name;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
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

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public boolean hasMissingInfo() {
        return isBlank(name) || isBlank(contactName) || isBlank(contactEmail);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "Sponsor ID: " + ID +
                " | Name: " + name +
                " | Contact Name: " + contactName +
                " | Contact Email: " + contactEmail;
    }
}