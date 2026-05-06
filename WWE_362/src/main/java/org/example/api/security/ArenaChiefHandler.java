package org.example.api.security;

import org.example.models.SecurityIncident;

public class ArenaChiefHandler extends SecurityHandler {

    @Override
    public void handle(SecurityIncident incident) {

        if (!"RESOLVED".equalsIgnoreCase(incident.getStatus())) {
            incident.setStatus("RESOLVED");
            incident.setResolutionNotes("Resolved by arena security chief.");
        }
        else if (nextHandler != null) {
            nextHandler.handle(incident);
        }
    }
}