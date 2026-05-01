package org.example.models;

/**
 * DECORATOR PATTERN - Concrete Decorator #3
 *
 * DiscountDecorator wraps any MerchandiseItem and applies a percentage
 * discount to its retail price. It overrides getName() to label the item
 * as on sale and overrides getRetailPrice() to return the reduced price.
 *
 * Wholesale cost is deliberately left unchanged — this lets isLowMargin()
 * and getProfitMargin() (inherited from the MerchandiseItem interface)
 * automatically reflect the tightened margin after the discount is applied,
 * which will trigger low margin warnings if the discount cuts too deep.
 *
 * Usage example in MerchController:
 *   MerchandiseItem base = new BasicMerchandiseItem(id, "Cody Rhodes T-Shirt", "CR-TSH-01", 9.00, 32.00, 97);
 *   MerchandiseItem sale = new DiscountDecorator(base, 20.0);
 *   // sale.getName()        → "[20% OFF] Cody Rhodes T-Shirt"
 *   // sale.getRetailPrice() → 25.60  (32.00 - 20%)
 *   // sale.getProfitMargin()→ 64.8%  (recalculated automatically against reduced price)
 *
 * Decorators can be stacked — discount an already-autographed item:
 *   MerchandiseItem clearance = new DiscountDecorator(new AutographedDecorator(base, "Cody Rhodes", 25.00), 10.0);
 *   // clearance.getRetailPrice() → (32.00 + 25.00) * 0.90 = 51.30
 */
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
