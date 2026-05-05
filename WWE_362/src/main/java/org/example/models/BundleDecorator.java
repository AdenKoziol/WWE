package org.example.models;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DECORATOR PATTERN - Concrete Decorator #4 (Bundle)
 *
 * BundleDecorator wraps a synthetic BasicMerchandiseItem (used as a handle for
 * ID, SKU, wholesale cost, and quantity) and holds a list of member MerchandiseItems
 * that make up the bundle's contents.
 *
 * This extends the classic single-item decorator pattern into a multi-item variant:
 * instead of adding one attribute to one item, BundleDecorator composes N items
 * into a single purchasable unit while still implementing MerchandiseItem — so it
 * is fully transparent to all existing controller and reporting code.
 *
 * Price:
 *   getRetailPrice() returns the sum of all member retail prices.
 *   An optional discount can be stacked on top via DiscountDecorator.
 *
 * Wholesale cost:
 *   getWholesaleCost() returns the sum of all member wholesale costs, giving
 *   getProfitMargin() and isLowMargin() accurate values automatically.
 *
 * Stock:
 *   getGlobalQuantity() / setGlobalQuantity() delegate to the wrapped handle item.
 *   The bundle's sellable quantity is tracked independently in the handle — it is
 *   the caller's (BundleController's) responsibility to keep this in sync with the
 *   minimum available quantity across all member SKUs.
 *
 * Usage example:
 *   MerchandiseItem shirt     = new BasicMerchandiseItem(1, "John Cena Shirt",    "JC-SHIRT-001", 8.00, 30.00, 150);
 *   MerchandiseItem wristband = new BasicMerchandiseItem(2, "John Cena Wristband","JC-WRI-07",    2.50, 10.00, 200);
 *   MerchandiseItem handle    = new BasicMerchandiseItem(99, "bundle-handle", "JC-BUNDLE-01", 0, 0, 150);
 *   BundleDecorator bundle    = new BundleDecorator(handle, List.of(shirt, wristband));
 *   // bundle.getName()         → "[BUNDLE] John Cena Shirt + John Cena Wristband"
 *   // bundle.getRetailPrice()  → 40.00
 *   // bundle.getWholesaleCost()→ 10.50
 *   // bundle.getProfitMargin() → 73.75%
 *
 * Stacking a discount on top:
 *   MerchandiseItem discounted = new DiscountDecorator(bundle, 10.0);
 *   // discounted.getRetailPrice() → 36.00  (40.00 - 10%)
 */
public class BundleDecorator extends MerchandiseDecorator {

    private final List<MerchandiseItem> members;

    /**
     * @param handle  A BasicMerchandiseItem used solely as a carrier for ID, SKU,
     *                and global quantity. Its name and prices are ignored — they are
     *                derived from the member list instead.
     * @param members The individual MerchandiseItems that make up this bundle.
     *                Each can itself be a decorator (e.g. an autographed shirt in a bundle).
     */
    public BundleDecorator(MerchandiseItem handle, List<MerchandiseItem> members) {
        super(handle);
        this.members = members;
    }

    /**
     * Returns a formatted bundle name listing all member item names joined by " + ".
     * Example: "[BUNDLE] John Cena Shirt + John Cena Wristband"
     */
    @Override
    public String getName() {
        String memberNames = members.stream()
                .map(MerchandiseItem::getName)
                .collect(Collectors.joining(" + "));
        return "[BUNDLE] " + memberNames;
    }

    /**
     * Returns the sum of all member retail prices.
     * If a DiscountDecorator is stacked on top, it will reduce this total.
     */
    @Override
    public double getRetailPrice() {
        return members.stream()
                .mapToDouble(MerchandiseItem::getRetailPrice)
                .sum();
    }

    /**
     * Returns the sum of all member wholesale costs.
     * This ensures getProfitMargin() reflects the true combined cost.
     */
    @Override
    public double getWholesaleCost() {
        return members.stream()
                .mapToDouble(MerchandiseItem::getWholesaleCost)
                .sum();
    }

    /**
     * Returns the list of member items in this bundle.
     * Used by BundleController and processSale() to identify which SKUs to decrement.
     */
    public List<MerchandiseItem> getMembers() {
        return members;
    }

    @Override
    public String toString() {
        String memberSummary = members.stream()
                .map(m -> m.getName() + " ($" + String.format("%.2f", m.getRetailPrice()) + ")")
                .collect(Collectors.joining(", "));

        return String.format(
                "ID: %d | %-40s | SKU: %-12s | Price: $%.2f | Margin: %.2f%% | Stock: %d%n  Members: [%s]",
                getID(), getName(), getSku(), getRetailPrice(),
                getProfitMargin(), getGlobalQuantity(), memberSummary
        );
    }
}
