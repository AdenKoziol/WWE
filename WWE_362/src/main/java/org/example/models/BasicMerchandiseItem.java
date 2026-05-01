package org.example.models;

/**
 * DECORATOR PATTERN - Concrete Component
 *
 * BasicMerchandiseItem is the base, undecorated merchandise item.
 * It holds all the core data and implements MerchandiseItem directly.
 *
 * This replaces the old MerchandiseItem class. The logic is identical —
 * only the class name and "implements MerchandiseItem" declaration are new.
 *
 * A BasicMerchandiseItem can be used as-is, or wrapped in one or more
 * decorators (e.g., AutographedDecorator, LimitedEditionDecorator)
 * to add features without modifying this class at all.
 */
public class BasicMerchandiseItem implements MerchandiseItem {

    private int ID;
    private String name;
    private String sku;
    private double wholesaleCost;
    private double retailPrice;
    private int globalQuantity;

    // Required for JSON deserialization
    public BasicMerchandiseItem() {}

    public BasicMerchandiseItem(int ID, String name, String sku,
                                double wholesale, double retail, int quantity) {
        this.ID = ID;
        this.name = name;
        this.sku = sku;
        this.wholesaleCost = wholesale;
        this.retailPrice = retail;
        this.globalQuantity = quantity;
    }

    @Override public int getID()                    { return ID; }
    @Override public String getName()               { return name; }
    @Override public String getSku()                { return sku; }
    @Override public double getRetailPrice()        { return retailPrice; }
    @Override public double getWholesaleCost()      { return wholesaleCost; }
    @Override public int getGlobalQuantity()        { return globalQuantity; }
    @Override public void setGlobalQuantity(int qty){ this.globalQuantity = qty; }

    @Override
    public String toString() {
        return String.format("ID: %d | %-20s | SKU: %-8s | Margin: %.2f%% | Stock: %d",
                ID, name, sku, getProfitMargin(), globalQuantity);
    }
}
