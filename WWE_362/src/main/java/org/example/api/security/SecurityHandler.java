package org.example.api.security;

import org.example.models.SecurityIncident;

public abstract class SecurityHandler {

    protected SecurityHandler nextHandler;

    public void setNextHandler(SecurityHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    public abstract void handle(SecurityIncident incident);
}