package org.example.models;

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
