package org.example.api.controllers;

import org.example.api.JsonParser;
import org.example.models.InventoryEntry;
import org.example.models.MerchStand;
import org.example.models.MerchandiseItem;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class MerchController {
    private static final String MERCH_FILE = "WWE_362/src/main/java/org/example/database/Merch.json";

    public static void registerNewItem(Scanner scanner) {
        try {
            System.out.print("Enter Item Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter SKU: ");
            String sku = scanner.nextLine();

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
    System.out.print("Enter Merch ID: ");
    int merchID = Integer.parseInt(scanner.nextLine());

    MerchandiseItem item = inventory.stream()
            .filter(i -> i.getID() == merchID).findFirst().orElse(null);

    if (item == null) return;

    List<MerchStand> allStands = MerchStandController.getAllStands();
    allStands.removeIf(Objects::isNull); 

    System.out.print("Enter Stand ID: ");
    String sid = scanner.nextLine();

    MerchStand targetStand = allStands.stream()
            .filter(s -> s != null && s.getStandID().equalsIgnoreCase(sid))
            .findFirst().orElse(null);

    if (targetStand == null) {
        System.out.println("Stand not found.");
        return;
    }

    System.out.print("Quantity: ");
    int transferQty = Integer.parseInt(scanner.nextLine());

    if (transferQty > item.getGlobalQuantity()) return;

    InventoryEntry entry = targetStand.findEntry(item.getSku());
    if (entry != null) {
        entry.setQuantity(entry.getQuantity() + transferQty);
    } else {
        targetStand.getLocalInventory().add(new InventoryEntry(item.getSku(), transferQty));
    }

    item.setGlobalQuantity(item.getGlobalQuantity() - transferQty);

    writeItems(inventory);
    MerchStandController.writeStands(allStands);

    System.out.println("Transfer successful! " + item.getSku() + " moved to " + sid);
}

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
            if (json.isEmpty() || json.equals("[]"))
                return new ArrayList<>();

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

public static void deleteMerchandise(Scanner scanner) {
    List<MerchandiseItem> inventory = getAllItems();
    System.out.print("Enter Merchandise ID to DELETE: ");
    int id = Integer.parseInt(scanner.nextLine());

    MerchandiseItem itemToDelete = inventory.stream()
            .filter(i -> i.getID() == id)
            .findFirst().orElse(null);

    if (itemToDelete == null) {
        System.out.println("ERROR: Item ID not found.");
        return;
    }

    String skuToDelete = itemToDelete.getSku();

    System.out.print("CRITICAL: Deleting '" + itemToDelete.getName() + 
                     "' will remove it from ALL stands. Proceed? (y/n): ");
    
    if (scanner.nextLine().equalsIgnoreCase("y")) {
        inventory.removeIf(i -> i.getID() == id);
        writeItems(inventory);

        List<MerchStand> allStands = MerchStandController.getAllStands();
        boolean removedFromStands = false;

        for (MerchStand stand : allStands) {
            boolean removed = stand.getLocalInventory().removeIf(entry -> 
                entry.getSku().equalsIgnoreCase(skuToDelete)
            );
            if (removed) removedFromStands = true;
        }

        if (removedFromStands) {
            MerchStandController.writeStands(allStands);
            System.out.println("CLEANUP: Item removed from all booth manifests.");
        }

        System.out.println("SUCCESS: Item " + skuToDelete + " fully purged from system.");
    } else {
        System.out.println("Deletion cancelled.");
    }
}


    public static void saveItem(MerchandiseItem item) {
        List<MerchandiseItem> items = getAllItems();
        items.add(item);
        writeItems(items);
    }

    private static void createEmptyFile(Path path) throws IOException {
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        Files.writeString(path, "[]"); 
    }
}
