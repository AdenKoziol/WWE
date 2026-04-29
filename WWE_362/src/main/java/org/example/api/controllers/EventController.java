package org.example.api.controllers;

import org.example.api.JsonParser;
import org.example.models.Event;
import org.example.models.Venue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EventController {

    private static final String EVENT_FILE = "src/main/java/org/example/database/Event.json";

    public static void scheduleEvent() {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter event name: ");
            String name = scanner.nextLine();

            System.out.print("Enter event date: ");
            String date = scanner.nextLine();

            System.out.print("Enter event time: ");
            String time = scanner.nextLine();

            System.out.println("Venues:");
            VenueController.displayAllVenues();
            System.out.print("Enter venue ID: ");
            int venueID = Integer.parseInt(scanner.nextLine());

            Venue venue = VenueController.getVenueByID(venueID);

            Event event = new Event(getNextID(), name, date, time, venueID, venue.getName());

            if (event.hasMissingInfo()) {
                System.out.println("Event could not be created. Missing required information.");
                return;
            }

            if (!isVenueAvailable(event)) {
                System.out.println("That venue is already booked for that date and time.");
                return;
            }

            saveEvent(event);
            System.out.println("Event scheduled successfully.");
            System.out.println(event);

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Event ID and Venue ID must be whole numbers.");
        }
    }

    public static boolean isVenueAvailable(Event newEvent) {
        List<Event> events = getAllEvents();

        for (Event existingEvent : events) {
            boolean sameVenue = existingEvent.getVenueID() == newEvent.getVenueID();

            boolean sameDate = existingEvent.getDate() != null &&
                    existingEvent.getDate().equalsIgnoreCase(newEvent.getDate());

            boolean sameTime = existingEvent.getTime() != null &&
                    existingEvent.getTime().equalsIgnoreCase(newEvent.getTime());

            if (sameVenue && sameDate && sameTime) {
                return false;
            }
        }

        return true;
    }

    public static void saveEvent(Event event) {
        List<Event> events = getAllEvents();
        events.add(event);
        writeEvents(events);
    }

    public static int getNextID() {
        List<Event> events = getAllEvents();

        int maxID = 0;

        for (Event event : events) {
            if (event.getID() > maxID) {
                maxID = event.getID();
            }
        }

        return maxID + 1;
    }

    public static List<Event> getAllEvents() {
        try {
            Path path = Paths.get(EVENT_FILE);

            if (!Files.exists(path)) {
                createEmptyEventFile(path);
                return new ArrayList<>();
            }

            String json = Files.readString(path).trim();

            if (json.isEmpty()) {
                return new ArrayList<>();
            }

            List<Event> events = JsonParser.deserializeList(json, Event.class);
            return events != null ? events : new ArrayList<>();

        } catch (IOException e) {
            System.out.println("Error reading event file.");
            return new ArrayList<>();
        }
    }

    public static Event getEventByID(int id) {
        List<Event> events = getAllEvents();

        for (Event event : events) {
            if (event.getID() == id) {
                return event;
            }
        }

        return null;
    }

    public static boolean deleteEventByID(int id) {
        List<Event> events = getAllEvents();

        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);

            if (event.getID() == id) {
                events.remove(i);
                writeEvents(events);
                return true;
            }
        }

        return false;
    }

    public static void displayAllEvents() {
        List<Event> events = getAllEvents();

        if (events.isEmpty()) {
            System.out.println("No events found.");
            return;
        }

        for (Event event : events) {
            System.out.println(event);
        }
    }

    private static void writeEvents(List<Event> events) {
        try {
            Path path = Paths.get(EVENT_FILE);

            if (!Files.exists(path)) {
                createEmptyEventFile(path);
            }

            String json = JsonParser.serialize(events);
            Files.writeString(path, json);

        } catch (IOException e) {
            System.out.println("Error writing event file.");
        }
    }

    private static void createEmptyEventFile(Path path) throws IOException {
        Path parent = path.getParent();

        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        Files.writeString(path, "[]");
    }
}