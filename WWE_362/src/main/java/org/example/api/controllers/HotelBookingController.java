package org.example.api.controllers;

import org.example.api.JsonParser;
import org.example.models.HotelBooking;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class HotelBookingController {

    private static final String HOTEL_FILE = "src/main/java/org/example/database/HotelBooking.json";

    public static void saveHotelBooking(HotelBooking hotelBooking) {
        List<HotelBooking> hotels = getAllHotelBookings();
        hotels.add(hotelBooking);
        writeHotelBookings(hotels);
    }

    public static List<HotelBooking> getAllHotelBookings() {
        try {
            Path path = Paths.get(HOTEL_FILE);

            if (!Files.exists(path)) {
                createEmptyHotelFile(path);
                return new ArrayList<>();
            }

            String json = Files.readString(path).trim();

            if (json.isEmpty()) {
                return new ArrayList<>();
            }

            List<HotelBooking> hotels = JsonParser.deserializeList(json, HotelBooking.class);
            return hotels != null ? hotels : new ArrayList<>();

        } catch (IOException e) {
            System.out.println("Error reading hotel booking file.");
            return new ArrayList<>();
        }
    }

    public static HotelBooking getHotelBookingByID(int id) {
        List<HotelBooking> hotels = getAllHotelBookings();

        for (HotelBooking hotel : hotels) {
            if (hotel.getID() == id) {
                return hotel;
            }
        }

        return null;
    }

    public static boolean deleteHotelBookingByID(int id) {
        List<HotelBooking> hotels = getAllHotelBookings();

        for (int i = 0; i < hotels.size(); i++) {
            if (hotels.get(i).getID() == id) {
                hotels.remove(i);
                writeHotelBookings(hotels);
                return true;
            }
        }

        return false;
    }

    public static void displayAllHotelBookings() {
        List<HotelBooking> hotels = getAllHotelBookings();

        if (hotels.isEmpty()) {
            System.out.println("No hotel bookings found.");
            return;
        }

        for (HotelBooking hotel : hotels) {
            System.out.println(hotel);
        }
    }

    public static int getNextID() {
        List<HotelBooking> hotels = getAllHotelBookings();
        int maxID = 0;

        for (HotelBooking hotel : hotels) {
            if (hotel.getID() > maxID) {
                maxID = hotel.getID();
            }
        }

        return maxID + 1;
    }

    private static void writeHotelBookings(List<HotelBooking> hotels) {
        try {
            Path path = Paths.get(HOTEL_FILE);

            if (!Files.exists(path)) {
                createEmptyHotelFile(path);
            }

            String json = JsonParser.serialize(hotels);
            Files.writeString(path, json);

        } catch (IOException e) {
            System.out.println("Error writing hotel booking file.");
        }
    }

    private static void createEmptyHotelFile(Path path) throws IOException {
        Path parent = path.getParent();

        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        Files.writeString(path, "[]");
    }
}