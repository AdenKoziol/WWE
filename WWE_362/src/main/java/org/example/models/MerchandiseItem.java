package org.example.models;


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