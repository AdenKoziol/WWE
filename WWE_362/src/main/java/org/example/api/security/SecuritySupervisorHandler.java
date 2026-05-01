package org.example.api.security;

import org.example.models.SecurityIncident;

public class SecuritySupervisorHandler extends SecurityHandler {

    @Override
    public void handle(SecurityIncident incident) {
        if (incident.getSeverity().equalsIgnoreCase("Moderate")) {
            incident.setStatus("RESOLVED");
            incident.setResolvedBy("Security Supervisor");
            incident.setResolutionNotes("Resolved by security supervisor.");
        } else if (nextHandler != null) {
            incident.setStatus("ESCALATED");
            nextHandler.handle(incident);
        }
    }
}