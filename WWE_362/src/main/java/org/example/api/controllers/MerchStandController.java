package org.example.api.controllers;

import org.example.api.JsonParser;
import org.example.models.MerchStand;
import org.example.models.InventoryEntry;
import org.example.models.MerchandiseItem;

import java.nio.file.*;
import java.util.*;

public class MerchStandController {
    private static final String STANDS_FILE = "WWE_362/src/main/java/org/example/database/MerchStands.json";

    public static void registerStand(Scanner scanner) {
        System.out.print("Enter Stand ID (e.g. GATE-A-01): ");
        String id = scanner.nextLine();
        System.out.print("Enter Location Name: ");
        String loc = scanner.nextLine();

        List<MerchStand> stands = getAllStands();
        stands.add(new MerchStand(id, loc));
        writeStands(stands);
        
        System.out.println("SUCCESS: Stand " + id + " registered.");
    }

    public static void processSale(Scanner scanner) {
        List<MerchStand> stands = getAllStands();
        System.out.print("Enter Stand ID: ");
        String sid = scanner.nextLine();

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

        InventoryEntry entry = stand.findEntry(sku);
        if (entry != null && entry.getQuantity() >= qty) {
            entry.setQuantity(entry.getQuantity() - qty);
            
            MerchandiseItem item = MerchController.findItemBySku(sku);
            if (item != null) {
                double saleAmount = item.getRetailPrice() * qty;
                stand.makeSale(saleAmount);
                System.out.printf("SALE RECORDED: Total $%.2f\n", (item.getRetailPrice() * qty));
            }
            
            writeStands(stands);
        } else {
            System.out.println("DENIED: Insufficient stock at this location.");
        }
    }

    public static void viewAllStandStocks() {
        List<MerchStand> stands = getAllStands();
        System.out.println("\n--- CURRENT STAND INVENTORY MANIFESTS ---");
        for (MerchStand s : stands) {
            
            System.out.println("Stand: " + s.getStandID() + " [" + s.getLocation() + "] " + "Total Profit: $" + s.getProfit());
            if (s.getLocalInventory().isEmpty()) {
                System.out.println("  (Empty)");
            } else {
                for (InventoryEntry e : s.getLocalInventory()) {
                    System.out.println("  - " + e.getSku() + ": " + e.getQuantity());
                }
            }
        }
    }

    public static List<MerchStand> getAllStands() {
        try {
            Path path = Paths.get(STANDS_FILE);
            if (!Files.exists(path)) return new ArrayList<>();
            String json = Files.readString(path).trim();
            if (json.isEmpty() || json.equals("[]")) return new ArrayList<>();

            List<MerchStand> list = JsonParser.deserializeList(json, MerchStand.class);
            list.removeIf(Objects::isNull); 
            return list;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void writeStands(List<MerchStand> stands) {
        try {
            stands.removeIf(Objects::isNull);
            String json = JsonParser.serialize(stands);
            Files.writeString(Paths.get(STANDS_FILE), json);
        } catch (Exception e) {
            System.out.println("Persistence Error.");
        }
    }

    public static void deleteStand(Scanner scanner) {
    List<MerchStand> stands = getAllStands();
    System.out.print("Enter Stand ID to DELETE (e.g., MAIN-ENT-01): ");
    String sid = scanner.nextLine();

    MerchStand standToDelete = stands.stream()
            .filter(s -> s.getStandID().equalsIgnoreCase(sid))
            .findFirst().orElse(null);

    if (standToDelete == null) {
        System.out.println("ERROR: Stand ID not found.");
        return;
    }

    if (!standToDelete.getLocalInventory().isEmpty()) {
        System.out.println("WARNING: This stand still has inventory assigned!");
        System.out.print("Force delete and lose stock records? (y/n): ");
    } else {
        System.out.print("Confirm deletion of " + sid + "? (y/n): ");
    }

    if (scanner.nextLine().equalsIgnoreCase("y")) {
        stands.removeIf(s -> s.getStandID().equalsIgnoreCase(sid));
        writeStands(stands);
        System.out.println("SUCCESS: Stand decommissioned.");
    } else {
        System.out.println("Deletion cancelled.");
    }
}
}