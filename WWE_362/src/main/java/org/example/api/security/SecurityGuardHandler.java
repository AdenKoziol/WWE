package org.example.api.security;

import org.example.models.SecurityIncident;

public class SecurityGuardHandler extends SecurityHandler {

    @Override
    public void handle(SecurityIncident incident) {

        if ("OPEN".equalsIgnoreCase(incident.getStatus())) {
            incident.setStatus("RESOLVED");
            incident.setResolutionNotes("Resolved by on-site security guard.");
        }
        else if (nextHandler != null) {
            incident.setStatus("ESCALATED");
            nextHandler.handle(incident);
        }
    }
}