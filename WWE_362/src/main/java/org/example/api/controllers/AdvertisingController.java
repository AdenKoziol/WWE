package org.example.api.controllers;

import org.example.api.JsonParser;
import org.example.models.AdvertisingDeal;
import org.example.models.BroadcastDeal;
import org.example.models.Event;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AdvertisingController {

    private static final String ADVERTISING_FILE = "src/main/java/org/example/database/AdvertisingDeal.json";
    private static final double LIVE_PPV_AD_RATE = 1000.0;

    public static void registerAdvertisingDeal(Scanner scanner) {
        try {
            System.out.println("Live PPV Broadcast Deals Available for Advertising:");
            BroadcastController.viewLivePpvDeals();

            System.out.print("\nEnter Event ID for this advertising deal: ");
            int eventID = Integer.parseInt(scanner.nextLine());

            Event event = EventController.getEventByID(eventID);
            if (event == null) {
                System.out.println("Invalid Event ID. Advertising deal cancelled.");
                return;
            }

            BroadcastDeal livePpvDeal = BroadcastController.findLivePpvDealForEvent(eventID);
            if (livePpvDeal == null) {
                System.out.println("Advertising Rejected: This event does not have an approved Live PPV broadcast deal.");
                return;
            }

            System.out.print("Enter Advertiser Name: ");
            String advertiserName = scanner.nextLine();

            System.out.print("Enter Ad Minutes Requested: ");
            int adMinutes = Integer.parseInt(scanner.nextLine());
            if (adMinutes <= 0) {
                System.out.println("Advertising Rejected: Ad minutes must be greater than zero.");
                return;
            }

            System.out.println("Placements: 1. Pre-Show | 2. Mid-Event | 3. Main Event Break");
            System.out.print("Choose placement (1, 2, or 3): ");
            String placementChoice = scanner.nextLine();
            String placement = getPlacementName(placementChoice);

            AdvertisingDeal pendingDeal = new AdvertisingDeal(
                    getNextID(),
                    eventID,
                    livePpvDeal.getID(),
                    advertiserName,
                    adMinutes,
                    LIVE_PPV_AD_RATE,
                    placement
            );

            if (pendingDeal.hasMissingInfo()) {
                System.out.println("Missing required information. Advertising deal cancelled.");
                return;
            }

            saveAdvertisingDeal(pendingDeal);
            System.out.println("Advertising Deal Registered Successfully!");
            System.out.println("Rate Applied: $" + String.format("%.2f", LIVE_PPV_AD_RATE) + " per minute for Live PPV.");
            System.out.println(pendingDeal);

        } catch (NumberFormatException e) {
            System.out.println("Invalid numerical input. Advertising deal cancelled.");
        }
    }

    public static void viewAllAdvertisingDeals() {
        List<AdvertisingDeal> deals = getAllAdvertisingDeals();
        if (deals.isEmpty()) {
            System.out.println("No advertising deals recorded yet.");
            return;
        }

        for (AdvertisingDeal deal : deals) {
            System.out.println(deal);
        }
    }

    public static void deleteAdvertisingDeal(Scanner scanner) {
        System.out.print("Enter Advertising Deal ID to delete: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            List<AdvertisingDeal> deals = getAllAdvertisingDeals();

            for (int i = 0; i < deals.size(); i++) {
                if (deals.get(i).getID() == id) {
                    deals.remove(i);
                    writeAdvertisingDeals(deals);
                    System.out.println("Advertising deal deleted successfully.");
                    return;
                }
            }

            System.out.println("Advertising deal not found.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        }
    }

    private static String getPlacementName(String choice) {
        if (choice.equals("1")) {
            return "Pre-Show";
        }
        if (choice.equals("2")) {
            return "Mid-Event";
        }
        if (choice.equals("3")) {
            return "Main Event Break";
        }
        return "Pre-Show";
    }

    private static void saveAdvertisingDeal(AdvertisingDeal deal) {
        List<AdvertisingDeal> deals = getAllAdvertisingDeals();
        deals.add(deal);
        writeAdvertisingDeals(deals);
    }

    private static void writeAdvertisingDeals(List<AdvertisingDeal> deals) {
        try {
            Path path = Paths.get(ADVERTISING_FILE);
            if (!Files.exists(path)) createEmptyFile(path);
            String json = JsonParser.serialize(deals);
            Files.writeString(path, json);
        } catch (IOException e) {
            System.out.println("Error saving advertising deal.");
        }
    }

    private static List<AdvertisingDeal> getAllAdvertisingDeals() {
        try {
            Path path = Paths.get(ADVERTISING_FILE);
            if (!Files.exists(path)) {
                createEmptyFile(path);
                return new ArrayList<>();
            }
            String json = Files.readString(path).trim();
            if (json.isEmpty() || json.equals("[]")) return new ArrayList<>();

            List<AdvertisingDeal> deals = JsonParser.deserializeList(json, AdvertisingDeal.class);
            return deals != null ? deals : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private static int getNextID() {
        List<AdvertisingDeal> deals = getAllAdvertisingDeals();
        int maxID = 0;
        for (AdvertisingDeal deal : deals) {
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
