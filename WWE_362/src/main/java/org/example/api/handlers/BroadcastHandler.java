package org.example.api.handlers;

import org.example.models.BroadcastDeal;

public abstract class BroadcastHandler {
    protected BroadcastHandler nextHandler;

    public BroadcastHandler setNext(BroadcastHandler nextHandler) {
        this.nextHandler = nextHandler;
        return nextHandler; // Allows chaining
    }

    public abstract boolean handle(BroadcastDeal deal);
}
