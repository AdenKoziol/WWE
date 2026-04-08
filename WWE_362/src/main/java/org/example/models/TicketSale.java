package org.example.models;

public class TicketSale {
    private int ID;
    private int eventID;
    private String ticketTier;
    private int quantity;
    private double totalPrice;

    public TicketSale() {
    }

    public TicketSale(int ID, int eventID, String ticketTier, int quantity, double totalPrice) {
        this.ID = ID;
        this.eventID = eventID;
        this.ticketTier = ticketTier;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    // Getters and Setters
    public int getID() { return ID; }
    public void setID(int ID) { this.ID = ID; }

    public int getEventID() { return eventID; }
    public void setEventID(int eventID) { this.eventID = eventID; }

    public String getTicketTier() { return ticketTier; }
    public void setTicketTier(String ticketTier) { this.ticketTier = ticketTier; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    @Override
    public String toString() {
        return "Sale Receipt{" +
                "SaleID=" + ID +
                ", EventID=" + eventID +
                ", Tier='" + ticketTier + '\'' +
                ", Quantity=" + quantity +
                ", Total Price=$" + String.format("%.2f", totalPrice) +
                '}';
    }
}
