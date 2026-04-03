package org.example.api.controllers;

import org.example.api.JsonParser;
import org.example.models.Venue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class VenueController {

    private static final String VENUE_FILE = "src/main/java/org/example/database/Venue.json";

    public void createVenue() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Create Venue");
        System.out.print("Enter venue ID: ");
        String id = scanner.nextLine();

        System.out.print("Enter venue name: ");
        String name = scanner.nextLine();

        System.out.print("Enter venue location: ");
        String location = scanner.nextLine();

        if (isBlank(id) || isBlank(name) || isBlank(location)) {
            System.out.println("Venue could not be created. Missing required information.");
            return;
        }

        if (getVenueByID(id) != null) {
            System.out.println("A venue with that ID already exists.");
            return;
        }

        Venue venue = new Venue(id, name, location);
        saveVenue(venue);

        System.out.println("Venue created successfully.");
        System.out.println(venue);
    }

    public void saveVenue(Venue venue) {
        List<Venue> venues = getAllVenues();
        venues.add(venue);
        writeVenues(venues);
    }

    public List<Venue> getAllVenues() {
        try {
            Path path = Paths.get(VENUE_FILE);

            if (!Files.exists(path)) {
                createEmptyVenueFile(path);
                return new ArrayList<>();
            }

            String json = Files.readString(path).trim();

            if (json.isEmpty() || json.equals("[]")) {
                return new ArrayList<>();
            }

            List<Venue> venues = JsonParser.deserializeList(json, Venue.class);
            return venues != null ? venues : new ArrayList<>();

        } catch (IOException e) {
            System.out.println("Error reading venue file.");
            return new ArrayList<>();
        }
    }

    public Venue getVenueByID(String id) {
        List<Venue> venues = getAllVenues();

        for (Venue venue : venues) {
            if (venue.getID() != null && venue.getID().equalsIgnoreCase(id)) {
                return venue;
            }
        }

        return null;
    }

    public boolean deleteVenueByID(String id) {
        List<Venue> venues = getAllVenues();

        for (int i = 0; i < venues.size(); i++) {
            Venue venue = venues.get(i);

            if (venue.getID() != null && venue.getID().equalsIgnoreCase(id)) {
                venues.remove(i);
                writeVenues(venues);
                return true;
            }
        }

        return false;
    }

    public boolean updateVenue(String id, String newName, String newLocation) {
        List<Venue> venues = getAllVenues();

        for (Venue venue : venues) {
            if (venue.getID() != null && venue.getID().equalsIgnoreCase(id)) {
                if (!isBlank(newName)) {
                    venue.setName(newName);
                }

                if (!isBlank(newLocation)) {
                    venue.setLocation(newLocation);
                }

                writeVenues(venues);
                return true;
            }
        }

        return false;
    }

    public void displayAllVenues() {
        List<Venue> venues = getAllVenues();

        if (venues.isEmpty()) {
            System.out.println("No venues found.");
            return;
        }

        for (Venue venue : venues) {
            System.out.println(venue);
        }
    }

    private void writeVenues(List<Venue> venues) {
        try {
            Path path = Paths.get(VENUE_FILE);

            if (!Files.exists(path)) {
                createEmptyVenueFile(path);
            }

            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < venues.size(); i++) {
                json.append(JsonParser.serialize(venues.get(i)));
                if (i < venues.size() - 1) {
                    json.append(",");
                }
            }
            json.append("]");

            Files.writeString(path, json.toString());

        } catch (IOException e) {
            System.out.println("Error writing venue file.");
        }
    }

    private void createEmptyVenueFile(Path path) throws IOException {
        Path parent = path.getParent();

        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        Files.writeString(path, "[]");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}