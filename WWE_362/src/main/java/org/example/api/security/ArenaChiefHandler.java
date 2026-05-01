package org.example.api.security;

import org.example.models.SecurityIncident;

public class ArenaChiefHandler extends SecurityHandler {

    @Override
    public void handle(SecurityIncident incident) {
        if (incident.getSeverity().equalsIgnoreCase("High")) {
            incident.setStatus("RESOLVED");
            incident.setResolvedBy("Arena Security Chief");
            incident.setResolutionNotes("Resolved by arena security chief.");
        } else if (nextHandler != null) {
            incident.setStatus("ESCALATED");
            nextHandler.handle(incident);
        }
    }
}