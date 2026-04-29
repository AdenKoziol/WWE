package org.example.api.controllers;

import org.example.api.JsonParser;
import org.example.models.TransportationBooking;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TransportationBookingController {

    private static final String TRANSPORTATION_FILE = "src/main/java/org/example/database/TransportationBooking.json";

    public static void saveTransportationBooking(TransportationBooking transportationBooking) {
        List<TransportationBooking> transportationList = getAllTransportationBookings();
        transportationList.add(transportationBooking);
        writeTransportationBookings(transportationList);
    }

    public static List<TransportationBooking> getAllTransportationBookings() {
        try {
            Path path = Paths.get(TRANSPORTATION_FILE);

            if (!Files.exists(path)) {
                createEmptyTransportationFile(path);
                return new ArrayList<>();
            }

            String json = Files.readString(path).trim();

            if (json.isEmpty()) {
                return new ArrayList<>();
            }

            List<TransportationBooking> transportationList =
                    JsonParser.deserializeList(json, TransportationBooking.class);

            return transportationList != null ? transportationList : new ArrayList<>();

        } catch (IOException e) {
            System.out.println("Error reading transportation booking file.");
            return new ArrayList<>();
        }
    }

    public static TransportationBooking getTransportationBookingByID(int id) {
        List<TransportationBooking> transportationList = getAllTransportationBookings();

        for (TransportationBooking transportation : transportationList) {
            if (transportation.getID() == id) {
                return transportation;
            }
        }

        return null;
    }

    public static boolean deleteTransportationBookingByID(int id) {
        List<TransportationBooking> transportationList = getAllTransportationBookings();

        for (int i = 0; i < transportationList.size(); i++) {
            if (transportationList.get(i).getID() == id) {
                transportationList.remove(i);
                writeTransportationBookings(transportationList);
                return true;
            }
        }

        return false;
    }

    public static void displayAllTransportationBookings() {
        List<TransportationBooking> transportationList = getAllTransportationBookings();

        if (transportationList.isEmpty()) {
            System.out.println("No transportation bookings found.");
            return;
        }

        for (TransportationBooking transportation : transportationList) {
            System.out.println(transportation);
        }
    }

    public static int getNextID() {
        List<TransportationBooking> transportationList = getAllTransportationBookings();
        int maxID = 0;

        for (TransportationBooking transportation : transportationList) {
            if (transportation.getID() > maxID) {
                maxID = transportation.getID();
            }
        }

        return maxID + 1;
    }

    private static void writeTransportationBookings(List<TransportationBooking> transportationList) {
        try {
            Path path = Paths.get(TRANSPORTATION_FILE);

            if (!Files.exists(path)) {
                createEmptyTransportationFile(path);
            }

            String json = JsonParser.serialize(transportationList);
            Files.writeString(path, json);

        } catch (IOException e) {
            System.out.println("Error writing transportation booking file.");
        }
    }

    private static void createEmptyTransportationFile(Path path) throws IOException {
        Path parent = path.getParent();

        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        Files.writeString(path, "[]");
    }
}