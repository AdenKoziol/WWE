package org.example.api.handlers;

import org.example.models.BroadcastDeal;

public class ExecutiveHandler extends BroadcastHandler {
    @Override
    public boolean handle(BroadcastDeal deal) {
        if (deal.getDealAmount() >= 10000000) {
            System.out.println("Notice: Deal exceeds $10M. Flagged for automatic Board of Directors review.");
            // We do not break the chain, just flag it and pass it along
        }
        
        if (nextHandler != null) {
            return nextHandler.handle(deal);
        }
        return true;
    }
}
