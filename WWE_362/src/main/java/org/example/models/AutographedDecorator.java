package org.example.models;

/**
 * DECORATOR PATTERN - Concrete Decorator #1
 *
 * AutographedDecorator wraps any MerchandiseItem and adds an autograph surcharge.
 * It overrides getName() and getRetailPrice() to reflect the upgrade,
 * while all other behavior (SKU, quantity, wholesale cost) is delegated
 * transparently to the wrapped item via MerchandiseDecorator.
 *
 * Usage example in MerchController:
 *   MerchandiseItem base = new BasicMerchandiseItem(id, "John Cena Shirt", "JC-001", 12.00, 30.00, 50);
 *   MerchandiseItem autographed = new AutographedDecorator(base, "John Cena", 25.00);
 *   // autographed.getName()        → "[AUTOGRAPHED by John Cena] John Cena Shirt"
 *   // autographed.getRetailPrice() → 55.00
 *
 * Decorators can be stacked:
 *   MerchandiseItem limitedAutograph = new LimitedEditionDecorator(autographed, 10.00);
 *   // limitedAutograph.getRetailPrice() → 65.00
 */
public class AutographedDecorator extends MerchandiseDecorator {

    private final String signedBy;
    private final double autographSurcharge;

    public AutographedDecorator(MerchandiseItem item, String signedBy, double surcharge) {
        super(item);
        this.signedBy = signedBy;
        this.autographSurcharge = surcharge;
    }

    /**
     * Returns the item name with an autograph label prepended.
     */
    @Override
    public String getName() {
        return "[AUTOGRAPHED by " + signedBy + "] " + wrapped.getName();
    }

    /**
     * Adds the autograph surcharge on top of the wrapped item's retail price.
     */
    @Override
    public double getRetailPrice() {
        return wrapped.getRetailPrice() + autographSurcharge;
    }

    public String getSignedBy() {
        return signedBy;
    }

    public double getAutographSurcharge() {
        return autographSurcharge;
    }

    @Override
    public String toString() {
        return String.format("ID: %d | %-35s | SKU: %-8s | Price: $%.2f (+$%.2f autograph) | Margin: %.2f%% | Stock: %d",
                getID(), getName(), getSku(), getRetailPrice(),
                autographSurcharge, getProfitMargin(), getGlobalQuantity());
    }
}
