package org.example.models;

/**
 * DECORATOR PATTERN - Abstract Decorator
 *
 * MerchandiseDecorator is the abstract base for all decorators.
 * It implements MerchandiseItem (so it IS a MerchandiseItem),
 * and wraps another MerchandiseItem (so it HAS a MerchandiseItem).
 *
 * This dual relationship is the heart of the Decorator pattern:
 *   - Implements the interface → can be used anywhere a MerchandiseItem is expected
 *   - Holds a reference to another MerchandiseItem → delegates calls to the wrapped object
 *
 * By default, every method simply delegates to the wrapped item.
 * Concrete decorators (Autographed, LimitedEdition) override only the
 * methods they need to change (e.g., getRetailPrice, getName).
 */
public abstract class MerchandiseDecorator implements MerchandiseItem {

    // The wrapped item — could be a BasicMerchandiseItem or another decorator
    protected final MerchandiseItem wrapped;

    public MerchandiseDecorator(MerchandiseItem item) {
        this.wrapped = item;
    }

    // Default delegation — all decorators inherit these unless overridden
    @Override public int getID()                    { return wrapped.getID(); }
    @Override public String getName()               { return wrapped.getName(); }
    @Override public String getSku()                { return wrapped.getSku(); }
    @Override public double getRetailPrice()        { return wrapped.getRetailPrice(); }
    @Override public double getWholesaleCost()      { return wrapped.getWholesaleCost(); }
    @Override public int getGlobalQuantity()        { return wrapped.getGlobalQuantity(); }
    @Override public void setGlobalQuantity(int qty){ wrapped.setGlobalQuantity(qty); }
}
