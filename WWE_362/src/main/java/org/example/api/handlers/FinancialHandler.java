package org.example.api.handlers;

import org.example.models.BroadcastDeal;

public class FinancialHandler extends BroadcastHandler {
    @Override
    public boolean handle(BroadcastDeal deal) {
        if (deal.getBroadcastType().equals("Live PPV") && deal.getDealAmount() < 1000000) {
            System.out.println("Approval Failed: Live PPV deals must exceed the $1,000,000 minimum threshold.");
            return false; // Break the chain
        }
        
        if (nextHandler != null) {
            return nextHandler.handle(deal);
        }
        return true;
    }
}
