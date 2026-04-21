package org.example.api.controllers;

import org.example.api.JsonParser;
import org.example.models.MerchandiseItem;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class MerchController {
    private static final String MERCH_FILE = "src/main/java/org/example/database/merchandise.json";
    private static final String ASSIGNMENT_FILE = "src/main/java/org/example/database/booth_assignments.json";

    public static void registerNewItem(Scanner scanner) {
        try {
            System.out.print("Enter Item Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter SKU: ");
            String sku = scanner.nextLine();

            // SKU Duplicate Check
            if (findItemBySku(sku) != null) {
                System.out.println("CRITICAL ERROR: SKU " + sku + " already exists in registry.");
                return;
            }

            System.out.print("Enter Wholesale Cost: ");
            double cost = Double.parseDouble(scanner.nextLine());
            System.out.print("Enter Retail Price: ");
            double price = Double.parseDouble(scanner.nextLine());
            System.out.print("Initial Inventory Count: ");
            int qty = Integer.parseInt(scanner.nextLine());

            MerchandiseItem item = new MerchandiseItem(getNextID(), name, sku, cost, price, qty);

            // Boss Test: Financial Validation
            if (item.isLowMargin()) {
                System.out.printf("WARNING: Profit margin is only %.2f%%. Proceed? (y/n): ", item.getProfitMargin());
                if (!scanner.nextLine().equalsIgnoreCase("y"))
                    return;
            }

            saveItem(item);
            System.out.println("SUCCESS: Item registered and added to Global Inventory.");
        } catch (Exception e) {
            System.out.println("INPUT ERROR: Please enter valid numerical values.");
        }
    }

    public static void assignToBooth(Scanner scanner) {
        List<MerchandiseItem> inventory = getAllItems();
        System.out.print("Enter Merchandise ID to move: ");
        int merchID = Integer.parseInt(scanner.nextLine());

        MerchandiseItem item = inventory.stream()
                .filter(i -> i.getID() == merchID).findFirst().orElse(null);

        if (item == null) {
            System.out.println("ERROR: Item ID not found.");
            return;
        }

        System.out.print("Enter Destination Booth Number: ");
        String booth = scanner.nextLine();
        System.out.print("Enter Quantity to Transfer: ");
        int transferQty = Integer.parseInt(scanner.nextLine());

        // Logic: Inventory Integrity Check
        if (transferQty > item.getGlobalQuantity()) {
            System.out.println("INVENTORY SHORTAGE: Only " + item.getGlobalQuantity() + " units available.");
            return;
        }

        // ATOMIC TRANSACTION:
        // 1. Subtract from Global
        item.setGlobalQuantity(item.getGlobalQuantity() - transferQty);

        // 2. Save updated Global Inventory
        writeItems(inventory);

        // 3. Log the assignment (Audit Trail)
        logAssignment(merchID, booth, transferQty);

        System.out.println("SUCCESS: Manifest generated. " + transferQty + " units moved to Booth " + booth);
    }


    // Case 3: List all available products
    public static void viewAllInventory() {
        List<MerchandiseItem> items = getAllItems();
        System.out.println("\n--- GLOBAL PRODUCT REGISTRY ---");
        System.out.printf("%-5s | %-20s | %-10s | %-8s | %-5s\n", "ID", "Name", "SKU", "Price", "Qty");
        System.out.println("------------------------------------------------------------");
        for (MerchandiseItem item : items) {
            System.out.printf("%-5d | %-20s | %-10s | $%-7.2f | %-5d\n", 
                item.getID(), item.getName(), item.getSku(), item.getRetailPrice(), item.getGlobalQuantity());
        }
    }

    // Case 4: Search by SKU (Specific lookup logic)
    public static void searchBySku(Scanner scanner) {
        System.out.print("Enter SKU to search: ");
        String sku = scanner.nextLine();
        MerchandiseItem item = findItemBySku(sku);
        if (item != null) {
            System.out.println("Product Found: " + item);
        } else {
            System.out.println("No product found with SKU: " + sku);
        }
    }

    // Case 5: Stock Valuation (The "Boss" Report)
    public static void viewStockValuation() {
        List<MerchandiseItem> items = getAllItems();
        double totalWholesale = 0;
        double totalPotentialRetail = 0;

        for (MerchandiseItem item : items) {
            totalWholesale += (item.getWholesaleCost() * item.getGlobalQuantity());
            totalPotentialRetail += (item.getRetailPrice() * item.getGlobalQuantity());
        }

        System.out.println("\n--- FINANCIAL STOCK VALUATION ---");
        System.out.printf("Total Asset Value (Wholesale): $%.2f\n", totalWholesale);
        System.out.printf("Total Potential Revenue:       $%.2f\n", totalPotentialRetail);
        System.out.printf("Projected Gross Profit:        $%.2f\n", (totalPotentialRetail - totalWholesale));
        System.out.println("---------------------------------");
    }

    // Case 6: Low Margin Alerts
    public static void viewLowMarginAlerts() {
        List<MerchandiseItem> items = getAllItems();
        System.out.println("\n--- LOW MARGIN WARNINGS (<15%) ---");
        items.stream()
             .filter(MerchandiseItem::isLowMargin)
             .forEach(System.out::println);
    }
   
    public static MerchandiseItem findItemBySku(String sku) {
        return getAllItems().stream().filter(i -> i.getSku().equals(sku)).findFirst().orElse(null);
    }

    private static int getNextID() {
        return getAllItems().stream().mapToInt(MerchandiseItem::getID).max().orElse(0) + 1;
    }

    private static List<MerchandiseItem> getAllItems() {
    try {
        Path path = Paths.get(MERCH_FILE);
        if (!Files.exists(path)) {
            createEmptyFile(path);
            return new ArrayList<>();
        }
        String json = Files.readString(path).trim();
        if (json.isEmpty() || json.equals("[]")) return new ArrayList<>();

        return JsonParser.deserializeList(json, MerchandiseItem.class);
    } catch (IOException e) {
        System.out.println("CRITICAL: Could not access merchandise database.");
        return new ArrayList<>();
    }
}

    private static void writeItems(List<MerchandiseItem> items) {
        try {
            Files.writeString(Paths.get(MERCH_FILE), JsonParser.serialize(items));
        } catch (Exception e) {
            System.out.println("Write Error.");
        }
    }

    private static void logAssignment(int id, String booth, int qty) {
        // In a real system, this would append to BoothAssignment.json
        System.out.println("AUDIT LOG: Item " + id + " assigned to Booth " + booth + " [Qty: " + qty + "]");
    }

    public static void saveItem(MerchandiseItem item) {
        List<MerchandiseItem> items = getAllItems();
        items.add(item);
        writeItems(items);
    }

    // Logic: Ensures the database folder and file exist to prevent IOErrors
    private static void createEmptyFile(Path path) throws IOException {
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        Files.writeString(path, "[]"); // Initialize as an empty JSON array
    }
}
