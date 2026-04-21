package org.example.api.controllers;

import org.example.api.JsonParser;
import org.example.models.MerchStand;
import org.example.models.MerchandiseItem;
import java.nio.file.*;
import java.util.*;

public class MerchStandController {
    private static final String STANDS_FILE = "WWE_362/src/main/java/org/example/database/MerchStands.json";

    public static void processSale(Scanner scanner) {
        try {
            List<MerchStand> stands = getAllStands();
            System.out.print("Enter Stand ID (e.g., NORTH-01): ");
            String sid = scanner.nextLine();

            // Find the specific stand
            MerchStand stand = stands.stream()
                    .filter(s -> s.getStandID().equalsIgnoreCase(sid))
                    .findFirst().orElse(null);

            if (stand == null) {
                System.out.println("ERROR: Stand not found.");
                return;
            }

            System.out.print("Enter SKU to sell: ");
            String sku = scanner.nextLine();
            System.out.print("Quantity: ");
            int qty = Integer.parseInt(scanner.nextLine());

            // Check local inventory
            int currentQty = stand.getLocalInventory().getOrDefault(sku, 0);
            if (currentQty < qty) {
                System.out.println("DENIED: Stand only has " + currentQty + " in stock.");
                return;
            }

            // Update local stock
            stand.getLocalInventory().put(sku, currentQty - qty);

            // Fetch price for receipt
            MerchandiseItem item = MerchController.findItemBySku(sku);
            if (item != null) {
                System.out.println("--- RECEIPT ---");
                System.out.printf("Item: %s | Total: $%.2f\n", item.getName(), (item.getRetailPrice() * qty));
            }

            // Save updated stands list
            writeStands(stands);
            System.out.println("Transaction complete. Files synchronized.");

        } catch (Exception e) {
            System.out.println("Error processing sale: " + e.getMessage());
        }
    }

    public static void registerStand(Scanner scanner) {
        System.out.print("Enter New Stand ID (e.g., SOUTH-CONCOURSE-02): ");
        String id = scanner.nextLine();
        System.out.print("Enter Physical Location Description: ");
        String loc = scanner.nextLine();

        List<MerchStand> stands = getAllStands();

        // Validation: Prevent duplicate Stand IDs
        if (stands.stream().anyMatch(s -> s.getStandID().equalsIgnoreCase(id))) {
            System.out.println("ERROR: A stand with that ID already exists.");
            return;
        }

        stands.add(new MerchStand(id, loc));
        writeStands(stands);
        System.out.println("SUCCESS: New Merch Stand registered in the system.");
    }

    public static List<MerchStand> getAllStands() {
        try {
            Path path = Paths.get(STANDS_FILE);
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.writeString(path, "[]");
                return new ArrayList<>();
            }
            return JsonParser.deserializeList(Files.readString(path), MerchStand.class);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void writeStands(List<MerchStand> stands) {
        try {
            Files.writeString(Paths.get(STANDS_FILE), JsonParser.serialize(stands));
        } catch (Exception e) {
            System.out.println("Failed to save stand data.");
        }
    }


    public static void viewAllStandStocks() {
    List<MerchStand> stands = getAllStands();
    
    if (stands.isEmpty()) {
        System.out.println("No stands are currently registered in the system.");
        return;
    }

    System.out.println("\n========================================");
    System.out.println("       LIVE STAND INVENTORY REPORT");
    System.out.println("========================================");

    for (MerchStand stand : stands) {
        System.out.println("\nStand ID: " + stand.getStandID());
        System.out.println("Location: " + stand.getLocation());
        System.out.println("----------------------------------------");
        
        Map<String, Integer> inventory = stand.getLocalInventory();
        if (inventory == null || inventory.isEmpty()) {
            System.out.println("  (No stock currently assigned)");
        } else {
            System.out.printf("  %-15s | %-10s\n", "SKU", "Quantity");
            inventory.forEach((sku, qty) -> {
                System.out.printf("  %-15s | %-10d\n", sku, qty);
            });
        }
    }
    System.out.println("========================================\n");
}
}