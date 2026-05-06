package org.example.api.security;

import org.example.models.SecurityIncident;

public class SecuritySupervisorHandler extends SecurityHandler {

    @Override
    public void handle(SecurityIncident incident) {

        if ("ESCALATED".equalsIgnoreCase(incident.getStatus())) {
            incident.setStatus("RESOLVED");
            incident.setResolutionNotes("Resolved by security supervisor.");
        }
        else if (nextHandler != null) {
            nextHandler.handle(incident);
        }
    }
}