package org.example.api.controllers;

import org.example.api.JsonParser;
import org.example.models.FlightBooking;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FlightBookingController {

    private static final String FLIGHT_FILE = "src/main/java/org/example/database/FlightBooking.json";

    public static void saveFlightBooking(FlightBooking flightBooking) {
        List<FlightBooking> flights = getAllFlightBookings();
        flights.add(flightBooking);
        writeFlightBookings(flights);
    }

    public static List<FlightBooking> getAllFlightBookings() {
        try {
            Path path = Paths.get(FLIGHT_FILE);

            if (!Files.exists(path)) {
                createEmptyFlightFile(path);
                return new ArrayList<>();
            }

            String json = Files.readString(path).trim();

            if (json.isEmpty()) {
                return new ArrayList<>();
            }

            List<FlightBooking> flights = JsonParser.deserializeList(json, FlightBooking.class);
            return flights != null ? flights : new ArrayList<>();

        } catch (IOException e) {
            System.out.println("Error reading flight booking file.");
            return new ArrayList<>();
        }
    }

    public static FlightBooking getFlightBookingByID(int id) {
        List<FlightBooking> flights = getAllFlightBookings();

        for (FlightBooking flight : flights) {
            if (flight.getID() == id) {
                return flight;
            }
        }

        return null;
    }

    public static boolean deleteFlightBookingByID(int id) {
        List<FlightBooking> flights = getAllFlightBookings();

        for (int i = 0; i < flights.size(); i++) {
            if (flights.get(i).getID() == id) {
                flights.remove(i);
                writeFlightBookings(flights);
                return true;
            }
        }

        return false;
    }

    public static void displayAllFlightBookings() {
        List<FlightBooking> flights = getAllFlightBookings();

        if (flights.isEmpty()) {
            System.out.println("No flight bookings found.");
            return;
        }

        for (FlightBooking flight : flights) {
            System.out.println(flight);
        }
    }

    public static int getNextID() {
        List<FlightBooking> flights = getAllFlightBookings();
        int maxID = 0;

        for (FlightBooking flight : flights) {
            if (flight.getID() > maxID) {
                maxID = flight.getID();
            }
        }

        return maxID + 1;
    }

    private static void writeFlightBookings(List<FlightBooking> flights) {
        try {
            Path path = Paths.get(FLIGHT_FILE);

            if (!Files.exists(path)) {
                createEmptyFlightFile(path);
            }

            String json = JsonParser.serialize(flights);
            Files.writeString(path, json);

        } catch (IOException e) {
            System.out.println("Error writing flight booking file.");
        }
    }

    private static void createEmptyFlightFile(Path path) throws IOException {
        Path parent = path.getParent();

        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        Files.writeString(path, "[]");
    }
}