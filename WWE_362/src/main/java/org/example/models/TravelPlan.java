package org.example.models;

import java.util.ArrayList;
import java.util.List;

public class TravelPlan {

    private Event event;
    private List<WrestlerItinerary> itineraries;

    public TravelPlan(Event event) {
        this.event = event;
        this.itineraries = new ArrayList<>();
    }

    public void addItinerary(WrestlerItinerary itinerary) {
        itineraries.add(itinerary);
    }

    public Event getEvent() {
        return event;
    }

    public List<WrestlerItinerary> getItineraries() {
        return itineraries;
    }
}