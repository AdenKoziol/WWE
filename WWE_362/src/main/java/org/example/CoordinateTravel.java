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
            System.out.println("4. Upgrade Wrestler Travel");
            System.out.println("5. Downgrade Wrestler Travel");
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
                case "4":
                    upgradeWrestlerTravelOption(scanner);
                    break;
                case "5":
                    downgradeWrestlerTravelOption(scanner);
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

    private static Wrestler findWrestlerByID(int wrestlerID) {
        List<Wrestler> wrestlers = WrestlerController.getAllWrestlers();

        for (Wrestler wrestler : wrestlers) {
            if (wrestler.getID() == wrestlerID) {
                return wrestler;
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

    private static void upgradeWrestlerTravelOption(Scanner scanner) {
        changeWrestlerTravelOption(scanner, true);
    }

    private static void downgradeWrestlerTravelOption(Scanner scanner) {
        changeWrestlerTravelOption(scanner, false);
    }

    private static void changeWrestlerTravelOption(Scanner scanner, boolean upgrade) {
        try {
            List<Wrestler> wrestlersWithTravel = getWrestlersWithAnyTravel();

            if (wrestlersWithTravel.isEmpty()) {
                System.out.println("No wrestlers currently have travel.");
                return;
            }

            if (upgrade) {
                System.out.println("\nUpgrade Wrestler Travel");
            } else {
                System.out.println("\nDowngrade Wrestler Travel");
            }

            System.out.println("\nWrestlers with current travel:");
            for (Wrestler wrestler : wrestlersWithTravel) {
                System.out.println(
                        wrestler.getID() + ". " +
                                wrestler.getRealName() +
                                " (" + wrestler.getStageName() + ")"
                );
            }

            System.out.print("\nEnter Wrestler ID: ");
            int wrestlerID = Integer.parseInt(scanner.nextLine());

            Wrestler selectedWrestler = findWrestlerByID(wrestlerID);

            if (selectedWrestler == null || !wrestlerHasAnyTravel(selectedWrestler)) {
                System.out.println("That wrestler does not currently have travel.");
                return;
            }

            List<Event> events = getEventsForWrestler(selectedWrestler);

            if (events.isEmpty()) {
                System.out.println("No events found for this wrestler.");
                return;
            }

            System.out.println("\nEvents for " + selectedWrestler.getRealName() + ":");

            for (Event event : events) {
                Venue venue = VenueController.getVenueByID(event.getVenueID());

                String venueName = "Unknown Venue";
                String location = "Unknown Location";

                if (venue != null) {
                    venueName = venue.getName();
                    location = venue.getLocation();
                }

                System.out.println(
                        event.getID() + ". " +
                                event.getName() +
                                " | Date: " + event.getDate() +
                                " | Venue: " + venueName +
                                " | Location: " + location
                );
            }

            System.out.print("\nEnter Event ID: ");
            int eventID = Integer.parseInt(scanner.nextLine());

            Event selectedEvent = EventController.getEventByID(eventID);

            if (selectedEvent == null) {
                System.out.println("Event not found.");
                return;
            }

            if (!eventHasWrestler(selectedEvent, selectedWrestler.getRealName())
                    && !eventHasWrestler(selectedEvent, selectedWrestler.getStageName())) {
                System.out.println("This wrestler is not competing in that event.");
                return;
            }

            updateTravelLevelForWrestlerEvent(selectedWrestler, selectedEvent, upgrade);

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. ID must be a whole number.");
        }
    }

    private static List<Wrestler> getWrestlersWithAnyTravel() {
        List<Wrestler> wrestlersWithTravel = new ArrayList<>();
        List<Wrestler> allWrestlers = WrestlerController.getAllWrestlers();

        for (Wrestler wrestler : allWrestlers) {
            if (wrestlerHasAnyTravel(wrestler) && !wrestlerListContains(wrestlersWithTravel, wrestler)) {
                wrestlersWithTravel.add(wrestler);
            }
        }

        return wrestlersWithTravel;
    }

    private static boolean wrestlerListContains(List<Wrestler> wrestlers, Wrestler wrestlerToFind) {
        for (Wrestler wrestler : wrestlers) {
            if (wrestler.getID() == wrestlerToFind.getID()) {
                return true;
            }
        }

        return false;
    }

    private static boolean wrestlerHasAnyTravel(Wrestler wrestler) {
        List<FlightBooking> flights = FlightBookingController.getAllFlightBookings();
        List<HotelBooking> hotels = HotelBookingController.getAllHotelBookings();
        List<TransportationBooking> transportationList =
                TransportationBookingController.getAllTransportationBookings();

        for (FlightBooking flight : flights) {
            if (bookingBelongsToWrestler(flight.getWrestlerName(), wrestler)) {
                return true;
            }
        }

        for (HotelBooking hotel : hotels) {
            if (bookingBelongsToWrestler(hotel.getWrestlerName(), wrestler)) {
                return true;
            }
        }

        for (TransportationBooking transportation : transportationList) {
            if (bookingBelongsToWrestler(transportation.getWrestlerName(), wrestler)) {
                return true;
            }
        }

        return false;
    }

    private static boolean bookingBelongsToWrestler(String bookedWrestlerName, Wrestler wrestler) {
        boolean sameRealName = bookedWrestlerName.equalsIgnoreCase(wrestler.getRealName());
        boolean sameStageName = bookedWrestlerName.equalsIgnoreCase(wrestler.getStageName());

        return sameRealName || sameStageName;
    }

    private static List<Event> getEventsForWrestler(Wrestler wrestler) {
        List<Event> events = EventController.getAllEvents();
        List<Event> wrestlerEvents = new ArrayList<>();

        for (Event event : events) {
            boolean hasRealName = eventHasWrestler(event, wrestler.getRealName());
            boolean hasStageName = eventHasWrestler(event, wrestler.getStageName());

            if (hasRealName || hasStageName) {
                wrestlerEvents.add(event);
            }
        }

        return wrestlerEvents;
    }

    private static void updateTravelLevelForWrestlerEvent(Wrestler wrestler, Event event, boolean upgrade) {
        Venue venue = VenueController.getVenueByID(event.getVenueID());

        if (venue == null) {
            System.out.println("Venue not found for this event.");
            return;
        }

        FlightBooking flight = findExistingFlight(wrestler, event);
        HotelBooking hotel = findExistingHotel(wrestler, event, venue);
        TransportationBooking transportation = findExistingTransportation(wrestler, venue);

        if (flight == null && hotel == null && transportation == null) {
            System.out.println("No existing travel was found for this wrestler and event.");
            return;
        }

        String flightType;
        String hotelType;
        String transportationType;

        if (upgrade) {
            flightType = "Luxury Flight";
            hotelType = "Luxury Hotel";
            transportationType = "Luxury Transportation";
        } else {
            flightType = "Standard Flight";
            hotelType = "Standard Hotel";
            transportationType = "Arena Shuttle";
        }

        if (flight != null) {
            FlightBooking updatedFlight = new FlightBooking(
                    flight.getID(),
                    wrestler.getRealName(),
                    venue.getLocation(),
                    event.getDate(),
                    flightType
            );

            if (FlightBookingController.deleteFlightBookingByID(flight.getID())) {
                FlightBookingController.saveFlightBooking(updatedFlight);
            }
        }

        if (hotel != null) {
            HotelBooking updatedHotel = new HotelBooking(
                    hotel.getID(),
                    wrestler.getRealName(),
                    venue.getLocation(),
                    event.getDate(),
                    hotelType
            );

            if (HotelBookingController.deleteHotelBookingByID(hotel.getID())) {
                HotelBookingController.saveHotelBooking(updatedHotel);
            }
        }

        if (transportation != null) {
            TransportationBooking updatedTransportation = new TransportationBooking(
                    transportation.getID(),
                    wrestler.getRealName(),
                    venue.getLocation(),
                    transportationType
            );

            if (TransportationBookingController.deleteTransportationBookingByID(transportation.getID())) {
                TransportationBookingController.saveTransportationBooking(updatedTransportation);
            }
        }

        if (upgrade) {
            System.out.println("\nTravel upgraded to luxury for " + wrestler.getRealName()
                    + " at event: " + event.getName());
        } else {
            System.out.println("\nTravel downgraded to standard for " + wrestler.getRealName()
                    + " at event: " + event.getName());
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

            if (wrestler != null && !wrestlerListContains(wrestlers, wrestler)) {
                wrestlers.add(wrestler);
            }
        }

        return wrestlers;
    }

    private static FlightBooking findExistingFlight(Wrestler wrestler, Event event) {
        List<FlightBooking> flights = FlightBookingController.getAllFlightBookings();

        for (FlightBooking flight : flights) {
            boolean sameWrestler = bookingBelongsToWrestler(flight.getWrestlerName(), wrestler);
            boolean sameDate = flight.getDate().equalsIgnoreCase(event.getDate());

            if (sameWrestler && sameDate) {
                return flight;
            }
        }

        return null;
    }

    private static HotelBooking findExistingHotel(Wrestler wrestler, Event event, Venue venue) {
        List<HotelBooking> hotels = HotelBookingController.getAllHotelBookings();

        for (HotelBooking hotel : hotels) {
            boolean sameWrestler = bookingBelongsToWrestler(hotel.getWrestlerName(), wrestler);
            boolean sameDate = hotel.getDate().equalsIgnoreCase(event.getDate());
            boolean sameLocation = hotel.getLocation().equalsIgnoreCase(venue.getLocation());

            if (sameWrestler && sameDate && sameLocation) {
                return hotel;
            }
        }

        return null;
    }

    private static TransportationBooking findExistingTransportation(Wrestler wrestler, Venue venue) {
        List<TransportationBooking> transportationList =
                TransportationBookingController.getAllTransportationBookings();

        for (TransportationBooking transportation : transportationList) {
            boolean sameWrestler = bookingBelongsToWrestler(transportation.getWrestlerName(), wrestler);
            boolean sameLocation = transportation.getLocation().equalsIgnoreCase(venue.getLocation());

            if (sameWrestler && sameLocation) {
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