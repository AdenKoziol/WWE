package org.example.models;

public class WrestlerItinerary {

    private Wrestler wrestler;
    private FlightBooking flight;
    private HotelBooking hotel;
    private TransportationBooking transportation;

    public WrestlerItinerary(Wrestler wrestler, FlightBooking flight,
                             HotelBooking hotel,
                             TransportationBooking transportation) {
        this.wrestler = wrestler;
        this.flight = flight;
        this.hotel = hotel;
        this.transportation = transportation;
    }

    public Wrestler getWrestler() {
        return wrestler;
    }

    public FlightBooking getFlight() {
        return flight;
    }

    public HotelBooking getHotel() {
        return hotel;
    }

    public TransportationBooking getTransportation() {
        return transportation;
    }
}