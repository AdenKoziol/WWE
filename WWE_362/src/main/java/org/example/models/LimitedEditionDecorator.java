package org.example.models;


public class LimitedEditionDecorator extends MerchandiseDecorator {

    private final double scarcityMarkup;

    public LimitedEditionDecorator(MerchandiseItem item, double scarcityMarkup) {
        super(item);
        this.scarcityMarkup = scarcityMarkup;
    }

    /**
     * Returns the item name with a limited edition label prepended.
     */
    @Override
    public String getName() {
        return "[LIMITED EDITION] " + wrapped.getName();
    }

    /**
     * Adds the scarcity markup on top of the wrapped item's retail price.
     */
    @Override
    public double getRetailPrice() {
        return wrapped.getRetailPrice() + scarcityMarkup;
    }

    public double getScarcityMarkup() {
        return scarcityMarkup;
    }

    @Override
    public String toString() {
        return String.format("ID: %d | %-35s | SKU: %-8s | Price: $%.2f (+$%.2f LE markup) | Margin: %.2f%% | Stock: %d",
                getID(), getName(), getSku(), getRetailPrice(),
                scarcityMarkup, getProfitMargin(), getGlobalQuantity());
    }
}
