package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.example.api.controllers.EventController;
import org.example.api.controllers.WrestlerController;
import org.example.api.controllers.VenueController;
import org.example.api.controllers.MatchCardController;
import org.example.api.controllers.FlightBookingController;
import org.example.api.controllers.HotelBookingController;
import org.example.api.controllers.TransportationBookingController;
import org.example.models.*;

public class CoordinateTravel {

    public static void showMenu(Scanner scanner) {
        checkDeleted();

        while (true) {
            printHeader();

            System.out.println("Travel & Logistics");
            System.out.println("1. Coordinate Travel for Event");
            System.out.println("2. View Travel for Event");
            System.out.println("3. Cancel Travel for Event");
            System.out.println("0. Back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    coordinateTravelOption(scanner);
                    break;
                case "2":
                    viewTravelOption(scanner);
                    break;
                case "3":
                    cancelTravelOption(scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
                    pause(scanner);
            }
        }
    }

    private static void checkDeleted() {
        List<FlightBooking> flights = FlightBookingController.getAllFlightBookings();
        List<HotelBooking> hotels = HotelBookingController.getAllHotelBookings();
        List<TransportationBooking> transportationList =
                TransportationBookingController.getAllTransportationBookings();

        for (FlightBooking flight : flights) {
            boolean wrestlerDeleted = findWrestlerByAnyName(flight.getWrestlerName()) == null;
            boolean travelStillValid = isFlightStillValid(flight);

            if (wrestlerDeleted || !travelStillValid) {
                FlightBookingController.deleteFlightBookingByID(flight.getID());
            }
        }

        for (HotelBooking hotel : hotels) {
            boolean wrestlerDeleted = findWrestlerByAnyName(hotel.getWrestlerName()) == null;
            boolean travelStillValid = isHotelStillValid(hotel);

            if (wrestlerDeleted || !travelStillValid) {
                HotelBookingController.deleteHotelBookingByID(hotel.getID());
            }
        }

        for (TransportationBooking transportation : transportationList) {
            boolean wrestlerDeleted = findWrestlerByAnyName(transportation.getWrestlerName()) == null;
            boolean travelStillValid = isTransportationStillValid(transportation);

            if (wrestlerDeleted || !travelStillValid) {
                TransportationBookingController.deleteTransportationBookingByID(transportation.getID());
            }
        }
    }

    private static boolean isFlightStillValid(FlightBooking flight) {
        List<Event> events = EventController.getAllEvents();

        for (Event event : events) {
            if (!event.getDate().equalsIgnoreCase(flight.getDate())) {
                continue;
            }

            Venue venue = VenueController.getVenueByID(event.getVenueID());

            if (venue == null) {
                continue;
            }

            if (!venue.getLocation().equalsIgnoreCase(flight.getDestination())) {
                continue;
            }

            if (eventHasWrestler(event, flight.getWrestlerName())) {
                return true;
            }
        }

        return false;
    }

    private static boolean isHotelStillValid(HotelBooking hotel) {
        List<Event> events = EventController.getAllEvents();

        for (Event event : events) {
            if (!event.getDate().equalsIgnoreCase(hotel.getDate())) {
                continue;
            }

            Venue venue = VenueController.getVenueByID(event.getVenueID());

            if (venue == null) {
                continue;
            }

            if (!venue.getLocation().equalsIgnoreCase(hotel.getLocation())) {
                continue;
            }

            if (eventHasWrestler(event, hotel.getWrestlerName())) {
                return true;
            }
        }

        return false;
    }

    private static boolean isTransportationStillValid(TransportationBooking transportation) {
        List<Event> events = EventController.getAllEvents();

        for (Event event : events) {
            Venue venue = VenueController.getVenueByID(event.getVenueID());

            if (venue == null) {
                continue;
            }

            if (!venue.getLocation().equalsIgnoreCase(transportation.getLocation())) {
                continue;
            }

            if (eventHasWrestler(event, transportation.getWrestlerName())) {
                return true;
            }
        }

        return false;
    }

    private static boolean eventHasWrestler(Event event, String bookedWrestlerName) {
        List<String> matchCardNames = MatchCardController.getWrestlersByEventID(event.getID());

        if (matchCardNames == null || matchCardNames.isEmpty()) {
            return false;
        }

        for (String matchCardName : matchCardNames) {
            Wrestler wrestler = findWrestlerByAnyName(matchCardName);

            if (wrestler != null) {
                boolean matchesRealName = wrestler.getRealName().equalsIgnoreCase(bookedWrestlerName);
                boolean matchesStageName = wrestler.getStageName().equalsIgnoreCase(bookedWrestlerName);

                if (matchesRealName || matchesStageName) {
                    return true;
                }
            }

            if (matchCardName.equalsIgnoreCase(bookedWrestlerName)) {
                return true;
            }
        }

        return false;
    }

    private static Wrestler findWrestlerByAnyName(String name) {
        Wrestler wrestler = WrestlerController.getWrestlerByName(name);

        if (wrestler != null) {
            return wrestler;
        }

        List<Wrestler> wrestlers = WrestlerController.getAllWrestlers();

        for (Wrestler current : wrestlers) {
            boolean realNameMatches = current.getRealName().equalsIgnoreCase(name);
            boolean stageNameMatches = current.getStageName().equalsIgnoreCase(name);

            if (realNameMatches || stageNameMatches) {
                return current;
            }
        }

        return null;
    }

    private static void coordinateTravelOption(Scanner scanner) {
        try {
            System.out.println("\nEvents:");
            EventController.displayAllEvents();

            System.out.print("\nEnter Event ID: ");
            int eventID = Integer.parseInt(scanner.nextLine());

            TravelPlan plan = coordinateTravelForEvent(eventID, scanner);

            if (plan != null) {
                displayTravelPlan(plan);
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Event ID must be a whole number.");
        }
    }

    private static void viewTravelOption(Scanner scanner) {
        try {
            System.out.println("\nEvents:");
            EventController.displayAllEvents();

            System.out.print("\nEnter Event ID to view travel: ");
            int eventID = Integer.parseInt(scanner.nextLine());

            TravelPlan plan = viewTravelForEvent(eventID);

            if (plan != null) {
                displayTravelPlan(plan);
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Event ID must be a whole number.");
        }
    }

    private static void cancelTravelOption(Scanner scanner) {
        try {
            System.out.println("\nEvents:");
            EventController.displayAllEvents();

            System.out.print("\nEnter Event ID to cancel travel for: ");
            int eventID = Integer.parseInt(scanner.nextLine());

            cancelTravelForEvent(eventID, scanner);

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Event ID must be a whole number.");
        }
    }

    public static TravelPlan coordinateTravelForEvent(int eventID, Scanner scanner) {
        Event event = EventController.getEventByID(eventID);

        if (event == null) {
            System.out.println("Event not found.");
            return null;
        }

        Venue venue = VenueController.getVenueByID(event.getVenueID());

        if (venue == null) {
            System.out.println("Venue not found for this event.");
            return null;
        }

        List<Wrestler> wrestlers = getWrestlersForEvent(eventID);

        if (wrestlers.isEmpty()) {
            System.out.println("No valid wrestlers found for this event.");
            return null;
        }

        TravelPlan travelPlan = new TravelPlan(event);

        System.out.println("\nEvent: " + event.getName());
        System.out.println("Date: " + event.getDate());
        System.out.println("Venue: " + venue.getName());
        System.out.println("Location: " + venue.getLocation());

        System.out.print("Wrestlers: ");
        for (int i = 0; i < wrestlers.size(); i++) {
            System.out.print(wrestlers.get(i).getRealName());

            if (i < wrestlers.size() - 1) {
                System.out.print(", ");
            }
        }

        System.out.print("\n\nDoes this look correct? (yes/no): ");
        String input = scanner.nextLine();

        if (!input.equalsIgnoreCase("yes")) {
            System.out.println("Travel coordination cancelled.");
            return null;
        }

        for (Wrestler wrestler : wrestlers) {
            FlightBooking flight = findExistingFlight(wrestler, event);
            HotelBooking hotel = findExistingHotel(wrestler, event, venue);
            TransportationBooking transportation = findExistingTransportation(wrestler, venue);

            boolean isNew = false;

            if (flight == null) {
                flight = bookFlight(wrestler, event, venue);
                FlightBookingController.saveFlightBooking(flight);
                isNew = true;
            }

            if (hotel == null) {
                hotel = bookHotel(wrestler, event, venue);
                HotelBookingController.saveHotelBooking(hotel);
                isNew = true;
            }

            if (transportation == null) {
                transportation = bookTransportation(wrestler, venue);
                TransportationBookingController.saveTransportationBooking(transportation);
                isNew = true;
            }

            if (isNew) {
                System.out.println("Created travel for: " + wrestler.getRealName());
            } else {
                System.out.println("Reused existing travel for: " + wrestler.getRealName());
            }

            WrestlerItinerary itinerary = new WrestlerItinerary(
                    wrestler,
                    flight,
                    hotel,
                    transportation
            );

            travelPlan.addItinerary(itinerary);
        }

        System.out.println("\nTravel coordination complete for event: " + event.getName());
        return travelPlan;
    }

    public static TravelPlan viewTravelForEvent(int eventID) {
        Event event = EventController.getEventByID(eventID);

        if (event == null) {
            System.out.println("Event not found.");
            return null;
        }

        Venue venue = VenueController.getVenueByID(event.getVenueID());

        if (venue == null) {
            System.out.println("Venue not found for this event.");
            return null;
        }

        List<Wrestler> wrestlers = getWrestlersForEvent(eventID);

        if (wrestlers.isEmpty()) {
            System.out.println("No valid wrestlers found for this event.");
            return null;
        }

        TravelPlan travelPlan = new TravelPlan(event);
        boolean foundAnyTravel = false;

        for (Wrestler wrestler : wrestlers) {
            FlightBooking flight = findExistingFlight(wrestler, event);
            HotelBooking hotel = findExistingHotel(wrestler, event, venue);
            TransportationBooking transportation = findExistingTransportation(wrestler, venue);

            if (flight != null || hotel != null || transportation != null) {
                WrestlerItinerary itinerary = new WrestlerItinerary(
                        wrestler,
                        flight,
                        hotel,
                        transportation
                );

                travelPlan.addItinerary(itinerary);
                foundAnyTravel = true;
            }
        }

        if (!foundAnyTravel) {
            System.out.println("No travel bookings were found for this event.");
            return null;
        }

        return travelPlan;
    }

    public static void cancelTravelForEvent(int eventID, Scanner scanner) {
        Event event = EventController.getEventByID(eventID);

        if (event == null) {
            System.out.println("Event not found.");
            return;
        }

        Venue venue = VenueController.getVenueByID(event.getVenueID());

        if (venue == null) {
            System.out.println("Venue not found for this event.");
            return;
        }

        List<Wrestler> wrestlers = getWrestlersForEvent(eventID);
        List<Wrestler> wrestlersWithTravel = new ArrayList<>();

        for (Wrestler wrestler : wrestlers) {
            FlightBooking flight = findExistingFlight(wrestler, event);
            HotelBooking hotel = findExistingHotel(wrestler, event, venue);
            TransportationBooking transportation = findExistingTransportation(wrestler, venue);

            if (flight != null || hotel != null || transportation != null) {
                wrestlersWithTravel.add(wrestler);
            }
        }

        if (wrestlersWithTravel.isEmpty()) {
            System.out.println("No travel bookings were found for this event.");
            return;
        }

        System.out.println("\nCancel travel for event: " + event.getName());
        System.out.println("Date: " + event.getDate());
        System.out.println("Venue: " + venue.getName());
        System.out.println("Location: " + venue.getLocation());

        System.out.print("Wrestlers with travel: ");
        for (int i = 0; i < wrestlersWithTravel.size(); i++) {
            System.out.print(wrestlersWithTravel.get(i).getRealName());

            if (i < wrestlersWithTravel.size() - 1) {
                System.out.print(", ");
            }
        }

        System.out.print("\n\nAre you sure you want to cancel this travel? (yes/no): ");
        String input = scanner.nextLine();

        if (!input.equalsIgnoreCase("yes")) {
            System.out.println("Travel cancellation cancelled.");
            return;
        }

        for (Wrestler wrestler : wrestlersWithTravel) {
            FlightBooking flight = findExistingFlight(wrestler, event);
            HotelBooking hotel = findExistingHotel(wrestler, event, venue);
            TransportationBooking transportation = findExistingTransportation(wrestler, venue);

            if (flight != null) {
                if (FlightBookingController.deleteFlightBookingByID(flight.getID())) {
                    System.out.println("Cancelled flight for: " + wrestler.getRealName());
                }
            }

            if (hotel != null) {
                if (HotelBookingController.deleteHotelBookingByID(hotel.getID())) {
                    System.out.println("Cancelled hotel for: " + wrestler.getRealName());
                }
            }

            if (transportation != null) {
                if (TransportationBookingController.deleteTransportationBookingByID(transportation.getID())) {
                    System.out.println("Cancelled transportation for: " + wrestler.getRealName());
                }
            }
        }

        System.out.println("\nTravel cancelled for event: " + event.getName());
    }

    private static List<Wrestler> getWrestlersForEvent(int eventID) {
        List<String> wrestlerNames = MatchCardController.getWrestlersByEventID(eventID);
        List<Wrestler> wrestlers = new ArrayList<>();

        if (wrestlerNames == null || wrestlerNames.isEmpty()) {
            return wrestlers;
        }

        for (String wrestlerName : wrestlerNames) {
            Wrestler wrestler = findWrestlerByAnyName(wrestlerName);

            if (wrestler != null) {
                wrestlers.add(wrestler);
            }
        }

        return wrestlers;
    }

    private static FlightBooking findExistingFlight(Wrestler wrestler, Event event) {
        List<FlightBooking> flights = FlightBookingController.getAllFlightBookings();

        for (FlightBooking flight : flights) {
            boolean sameRealName = flight.getWrestlerName().equalsIgnoreCase(wrestler.getRealName());
            boolean sameStageName = flight.getWrestlerName().equalsIgnoreCase(wrestler.getStageName());
            boolean sameDate = flight.getDate().equalsIgnoreCase(event.getDate());

            if ((sameRealName || sameStageName) && sameDate) {
                return flight;
            }
        }

        return null;
    }

    private static HotelBooking findExistingHotel(Wrestler wrestler, Event event, Venue venue) {
        List<HotelBooking> hotels = HotelBookingController.getAllHotelBookings();

        for (HotelBooking hotel : hotels) {
            boolean sameRealName = hotel.getWrestlerName().equalsIgnoreCase(wrestler.getRealName());
            boolean sameStageName = hotel.getWrestlerName().equalsIgnoreCase(wrestler.getStageName());
            boolean sameDate = hotel.getDate().equalsIgnoreCase(event.getDate());
            boolean sameLocation = hotel.getLocation().equalsIgnoreCase(venue.getLocation());

            if ((sameRealName || sameStageName) && sameDate && sameLocation) {
                return hotel;
            }
        }

        return null;
    }

    private static TransportationBooking findExistingTransportation(Wrestler wrestler, Venue venue) {
        List<TransportationBooking> transportationList =
                TransportationBookingController.getAllTransportationBookings();

        for (TransportationBooking transportation : transportationList) {
            boolean sameRealName = transportation.getWrestlerName().equalsIgnoreCase(wrestler.getRealName());
            boolean sameStageName = transportation.getWrestlerName().equalsIgnoreCase(wrestler.getStageName());
            boolean sameLocation = transportation.getLocation().equalsIgnoreCase(venue.getLocation());

            if ((sameRealName || sameStageName) && sameLocation) {
                return transportation;
            }
        }

        return null;
    }

    private static FlightBooking bookFlight(Wrestler wrestler, Event event, Venue venue) {
        return new FlightBooking(
                FlightBookingController.getNextID(),
                wrestler.getRealName(),
                venue.getLocation(),
                event.getDate(),
                "Standard Flight"
        );
    }

    private static HotelBooking bookHotel(Wrestler wrestler, Event event, Venue venue) {
        return new HotelBooking(
                HotelBookingController.getNextID(),
                wrestler.getRealName(),
                venue.getLocation(),
                event.getDate(),
                "Standard Hotel"
        );
    }

    private static TransportationBooking bookTransportation(Wrestler wrestler, Venue venue) {
        return new TransportationBooking(
                TransportationBookingController.getNextID(),
                wrestler.getRealName(),
                venue.getLocation(),
                "Arena Shuttle"
        );
    }

    private static void displayTravelPlan(TravelPlan plan) {
        System.out.println("\nTravel Plan for Event: " + plan.getEvent().getName());

        for (WrestlerItinerary itinerary : plan.getItineraries()) {
            System.out.println("---------------------------------");
            System.out.println("Wrestler: " + itinerary.getWrestler().getRealName());
            System.out.println("Flight: " + itinerary.getFlight());
            System.out.println("Hotel: " + itinerary.getHotel());
            System.out.println("Transportation: " + itinerary.getTransportation());
        }
    }

    private static void printHeader() {
        System.out.println("\n==============================");
        System.out.println("            WWE");
        System.out.println("==============================\n");
    }

    private static void pause(Scanner scanner) {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}