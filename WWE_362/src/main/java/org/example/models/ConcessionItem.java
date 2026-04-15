package org.example.models;

public class ConcessionItem {
    private int ID;
    private String name;
    private double price;

    public ConcessionItem() {
    }

    public ConcessionItem(int ID, String name, double price) {
        this.ID = ID;
        this.name = name;
        this.price = price;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "ConcessionItem{" +
                "ID=" + ID +
                ", Name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}