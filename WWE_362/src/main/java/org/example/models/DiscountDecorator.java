package org.example.models;

public class DiscountDecorator extends MerchandiseDecorator {

    private final double discountPercent;

    /**
     * @param item            The MerchandiseItem to wrap (can be basic or another decorator)
     * @param discountPercent The percentage to discount, e.g. 20.0 for 20% off
     */
    public DiscountDecorator(MerchandiseItem item, double discountPercent) {
        super(item);
        this.discountPercent = discountPercent;
    }

    /**
     * Prepends a discount label showing the exact percentage off.
     */
    @Override
    public String getName() {
        return String.format("[%.0f%% OFF] %s", discountPercent, wrapped.getName());
    }

    /**
     * Reduces the wrapped item's retail price by the discount percentage.
     * Wholesale cost is unchanged, so margin calculations reflect the real impact.
     */
    @Override
    public double getRetailPrice() {
        return wrapped.getRetailPrice() * (1.0 - (discountPercent / 100.0));
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    @Override
    public String toString() {
        return String.format("ID: %d | %-35s | SKU: %-8s | Price: $%.2f (%.0f%% off $%.2f) | Margin: %.2f%% | Stock: %d",
                getID(), getName(), getSku(), getRetailPrice(),
                discountPercent, wrapped.getRetailPrice(), getProfitMargin(), getGlobalQuantity());
    }
}
