package org.example.api.security;

import org.example.models.SecurityIncident;

public class EmergencyResponseHandler extends SecurityHandler {

    @Override
    public void handle(SecurityIncident incident) {
        incident.setStatus("RESOLVED");
        incident.setResolvedBy("Emergency Response");
        incident.setResolutionNotes("Escalated to emergency response.");
    }
}