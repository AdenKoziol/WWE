package org.example.api.controllers;

import org.example.api.JsonParser;
import org.example.models.BroadcastDeal;
import org.example.models.Event;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BroadcastController {

    private static final String BROADCAST_FILE = "src/main/java/org/example/database/Broadcast.json";

    public static void registerDeal(Scanner scanner) {
        try {
            System.out.println("Available Events:");
            EventController.displayAllEvents();
            
            System.out.print("\nEnter Event ID for this broadcast deal: ");
            int eventID = Integer.parseInt(scanner.nextLine());

            EventController ec = new EventController();
            Event event = ec.getEventByID(eventID);
            if (event == null) {
                System.out.println("Invalid Event ID. Deal cancelled.");
                return;
            }

            System.out.println("Broadcast Types: 1. Live PPV | 2. Delayed Cable");
            System.out.print("Choose type (1 or 2): ");
            String typeChoice = scanner.nextLine();
            String type = typeChoice.equals("1") ? "Live PPV" : "Delayed Cable";

            if (type.equals("Live PPV") && hasExclusiveLiveDeal(eventID)) {
                System.out.println("Conflict Error: Live broadcasting rights are already sold for this event.");
                return;
            }

            System.out.print("Enter Network Name (e.g., Peacock): ");
            String network = scanner.nextLine();

            System.out.print("Enter Deal Amount: $");
            double amount = Double.parseDouble(scanner.nextLine());

            BroadcastDeal deal = new BroadcastDeal(getNextID(), eventID, network, amount, type);
            
            if (deal.hasMissingInfo()) {
                System.out.println("Missing required information. Deal cancelled.");
                return;
            }

            saveDeal(deal);
            System.out.println("\nBroadcast Deal Registered Successfully!");
            System.out.println(deal);

        } catch (NumberFormatException e) {
            System.out.println("Invalid numerical input. Deal cancelled.");
        }
    }

    public static void viewAllDeals() {
        List<BroadcastDeal> deals = getAllDeals();
        if (deals.isEmpty()) {
            System.out.println("No broadcast deals recorded yet.");
            return;
        }
        for (BroadcastDeal deal : deals) {
            System.out.println(deal);
        }
    }

    public static void deleteDeal(Scanner scanner) {
        System.out.print("Enter Broadcast Deal ID to delete: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            List<BroadcastDeal> deals = getAllDeals();
            for (int i = 0; i < deals.size(); i++) {
                if (deals.get(i).getID() == id) {
                    deals.remove(i);
                    writeDeals(deals);
                    System.out.println("Deal deleted successfully.");
                    return;
                }
            }
            System.out.println("Deal not found.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        }
    }

    private static boolean hasExclusiveLiveDeal(int eventID) {
        List<BroadcastDeal> deals = getAllDeals();
        for (BroadcastDeal deal : deals) {
            if (deal.getEventID() == eventID && deal.getBroadcastType().equals("Live PPV")) {
                return true;
            }
        }
        return false;
    }

    private static void saveDeal(BroadcastDeal deal) {
        List<BroadcastDeal> deals = getAllDeals();
        deals.add(deal);
        writeDeals(deals);
    }

    private static void writeDeals(List<BroadcastDeal> deals) {
        try {
            Path path = Paths.get(BROADCAST_FILE);
            if (!Files.exists(path)) createEmptyFile(path);
            String json = JsonParser.serialize(deals);
            Files.writeString(path, json);
        } catch (IOException e) {
            System.out.println("Error saving broadcast deal.");
        }
    }

    private static List<BroadcastDeal> getAllDeals() {
        try {
            Path path = Paths.get(BROADCAST_FILE);
            if (!Files.exists(path)) {
                createEmptyFile(path);
                return new ArrayList<>();
            }
            String json = Files.readString(path).trim();
            if (json.isEmpty() || json.equals("[]")) return new ArrayList<>();
            
            List<BroadcastDeal> deals = JsonParser.deserializeList(json, BroadcastDeal.class);
            return deals != null ? deals : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private static int getNextID() {
        List<BroadcastDeal> deals = getAllDeals();
        int maxID = 0;
        for (BroadcastDeal deal : deals) {
            if (deal.getID() > maxID) maxID = deal.getID();
        }
        return maxID + 1;
    }

    private static void createEmptyFile(Path path) throws IOException {
        Path parent = path.getParent();
        if (parent != null && !Files.exists(parent)) Files.createDirectories(parent);
        Files.writeString(path, "[]");
    }
}
