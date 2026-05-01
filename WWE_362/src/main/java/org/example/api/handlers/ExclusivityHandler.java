package org.example.api.handlers;

import org.example.api.controllers.BroadcastController;
import org.example.models.BroadcastDeal;

public class ExclusivityHandler extends BroadcastHandler {
    @Override
    public boolean handle(BroadcastDeal deal) {
        if (deal.getBroadcastType().equals("Live PPV")) {
            if (BroadcastController.hasExclusiveLiveDeal(deal.getEventID())) {
                System.out.println("Approval Failed: Live broadcasting rights are already sold for this event.");
                return false; // Break the chain
            }
        }
        
        if (nextHandler != null) {
            return nextHandler.handle(deal);
        }
        return true;
    }
}
