package org.example.api.controllers;

import org.example.api.JsonParser;
import org.example.models.MerchStand;
import org.example.models.MerchandiseItem;
import java.nio.file.*;
import java.util.*;

public class MerchStandController {
    private static final String STANDS_FILE = "src/main/java/org/example/database/Stands.json";

    // BOSS TEST: Process a sale at a specific stand and update files
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

    // --- Persistence Logic ---
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

    private static void writeStands(List<MerchStand> stands) {
        try {
            Files.writeString(Paths.get(STANDS_FILE), JsonParser.serialize(stands));
        } catch (Exception e) {
            System.out.println("Failed to save stand data.");
        }
    }
}