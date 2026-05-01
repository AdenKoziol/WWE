package org.example.models;

/**
 * DECORATOR PATTERN - Component Interface
 *
 * MerchandiseItem is now an interface rather than a concrete class.
 * This is the "Component" role in the Decorator pattern.
 *
 * Both BasicMerchandiseItem (the base object) and all decorators
 * (AutographedDecorator, LimitedEditionDecorator) implement this interface,
 * meaning they are all interchangeable wherever MerchandiseItem is used.
 *
 * The rest of the system (MerchController, MerchStandController) types against
 * this interface and requires NO changes — they simply call these methods
 * and get the decorated behavior transparently.
 */
public interface MerchandiseItem {

    int getID();
    String getName();
    String getSku();
    double getRetailPrice();
    double getWholesaleCost();
    int getGlobalQuantity();
    void setGlobalQuantity(int qty);

    default double getProfitMargin() {
        double retail = getRetailPrice();
        if (retail <= 0) return 0;
        return ((retail - getWholesaleCost()) / retail) * 100;
    }

    default boolean isLowMargin() {
        return getProfitMargin() < 15.0;
    }
}