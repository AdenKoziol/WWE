package org.example.models;


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
