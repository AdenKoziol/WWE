package org.example.models;

/**
 * DECORATOR PATTERN - Concrete Decorator #2
 *
 * LimitedEditionDecorator wraps any MerchandiseItem and marks it as
 * a limited edition release with a scarcity markup applied to the retail price.
 *
 * Like AutographedDecorator, it only overrides getName() and getRetailPrice().
 * Everything else (SKU, quantity management, wholesale cost) passes through
 * to the wrapped item unchanged.
 *
 * Usage example in MerchController:
 *   MerchandiseItem base = new BasicMerchandiseItem(id, "Raw Belt Replica", "RAW-001", 40.00, 80.00, 20);
 *   MerchandiseItem limited = new LimitedEditionDecorator(base, 20.00);
 *   // limited.getName()        → "[LIMITED EDITION] Raw Belt Replica"
 *   // limited.getRetailPrice() → 100.00
 *
 * Decorators can also be stacked with AutographedDecorator:
 *   MerchandiseItem superItem = new AutographedDecorator(limited, "CM Punk", 30.00);
 *   // superItem.getRetailPrice() → 130.00
 */
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
