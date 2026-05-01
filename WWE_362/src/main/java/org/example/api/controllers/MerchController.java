package org.example.api.controllers;

import org.example.api.JsonParser;
import org.example.models.InventoryEntry;
import org.example.models.MerchStand;
import org.example.models.MerchandiseItem;
import org.example.models.BasicMerchandiseItem;
import org.example.models.AutographedDecorator;
import org.example.models.LimitedEditionDecorator;
import org.example.models.DiscountDecorator;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * DECORATOR PATTERN - Controller Integration
 *
 * MerchController is largely unchanged. The only update is in registerNewItem(),
 * which now asks whether the item is autographed or limited edition, and wraps
 * the BasicMerchandiseItem in the appropriate decorator before saving.
 *
 * All other methods (viewAllInventory, searchBySku, etc.) type against the
 * MerchandiseItem interface — they work identically with decorated or plain items,
 * since decorators implement the same interface and delegate transparently.
 *
 * This is the key benefit of the Decorator pattern: zero changes required
 * in the rest of the system to support new item "types."
 */
public class MerchController {
    private static final String MERCH_FILE = "src/main/java/org/example/database/Merch.json";

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

            // --- DECORATOR PATTERN: Build base item, then wrap with decorators ---
            MerchandiseItem item = new BasicMerchandiseItem(getNextID(), name, sku, cost, price, qty);

            System.out.print("Is this item autographed? (y/n): ");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                System.out.print("Signed by (wrestler name): ");
                String signedBy = scanner.nextLine();
                System.out.print("Autograph surcharge ($): ");
                double surcharge = Double.parseDouble(scanner.nextLine());
                item = new AutographedDecorator(item, signedBy, surcharge);
            }

            System.out.print("Is this a limited edition item? (y/n): ");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                System.out.print("Limited edition scarcity markup ($): ");
                double markup = Double.parseDouble(scanner.nextLine());
                item = new LimitedEditionDecorator(item, markup);
            }

            System.out.print("Apply a discount to this item? (y/n): ");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                System.out.print("Discount percentage (e.g. 20 for 20% off): ");
                double discount = Double.parseDouble(scanner.nextLine());
                if (discount <= 0 || discount >= 100) {
                    System.out.println("WARNING: Discount must be between 0 and 100%. Skipping discount.");
                } else {
                    item = new DiscountDecorator(item, discount);
                }
            }
            // --- End of Decorator wrapping ---

            if (item.isLowMargin()) {
                System.out.printf("WARNING: Profit margin is only %.2f%%. Proceed? (y/n): ", item.getProfitMargin());
                if (!scanner.nextLine().equalsIgnoreCase("y"))
                    return;
            }

            saveItem(item);
            System.out.println("SUCCESS: " + item.getName() + " registered and added to Global Inventory.");

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
        System.out.printf("%-5s | %-35s | %-10s | %-8s | %-5s\n", "ID", "Name", "SKU", "Price", "Qty");
        System.out.println("------------------------------------------------------------------------");
        for (MerchandiseItem item : items) {
            // getName() and getRetailPrice() automatically return decorated values
            System.out.printf("%-5d | %-35s | %-10s | $%-7.2f | %-5d\n",
                    item.getID(), item.getName(), item.getSku(),
                    item.getRetailPrice(), item.getGlobalQuantity());
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
            // getRetailPrice() on a decorator returns the marked-up price automatically
            totalWholesale      += (item.getWholesaleCost() * item.getGlobalQuantity());
            totalPotentialRetail += (item.getRetailPrice()  * item.getGlobalQuantity());
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
        return getAllItems().stream()
                .filter(i -> i.getSku().equals(sku))
                .findFirst().orElse(null);
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

            List<BasicMerchandiseItem> raw = JsonParser.deserializeList(json, BasicMerchandiseItem.class);
            raw.removeIf(i -> i == null || i.getSku() == null);
            return new ArrayList<>(raw);
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
                        entry.getSku().equalsIgnoreCase(skuToDelete));
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
        // Flatten the decorator chain into a plain BasicMerchandiseItem before persisting.
        // Decorators wrap each other in memory but cannot round-trip through JSON --
        // the final computed name and retail price are saved directly on the base object.
        BasicMerchandiseItem flat = new BasicMerchandiseItem(
                item.getID(),
                item.getName(),         // decorated name e.g. "[LIMITED EDITION] [AUTOGRAPHED by X] ..."
                item.getSku(),
                item.getWholesaleCost(),
                item.getRetailPrice(),  // decorated price with all markups/discounts applied
                item.getGlobalQuantity()
        );
        items.add(flat);
        writeItems(items);
    }

    public static void redecoratItem(Scanner scanner) {
        List<MerchandiseItem> inventory = getAllItems();

        System.out.println("--- REDECORATE ITEM ---");
        System.out.printf("%-5s | %-35s | %-10s | %-8s%n", "ID", "Name", "SKU", "Price");
        System.out.println("------------------------------------------------------------");
        for (MerchandiseItem i : inventory) {
            System.out.printf("%-5d | %-35s | %-10s | $%.2f%n",
                    i.getID(), i.getName(), i.getSku(), i.getRetailPrice());
        }

        System.out.print("\nEnter Item ID to redecorate: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
            return;
        }

        MerchandiseItem target = inventory.stream()
                .filter(i -> i.getID() == id)
                .findFirst().orElse(null);

        if (target == null) {
            System.out.println("ERROR: Item ID not found.");
            return;
        }

        System.out.println("Current: " + target.getName() + " @ $" + String.format("%.2f", target.getRetailPrice()));
        System.out.println("Wholesale cost (locked): $" + String.format("%.2f", target.getWholesaleCost()));
        System.out.println("Note: decorations will be applied on top of the current retail price.");

        // Wrap with decorators exactly as in registerNewItem
        MerchandiseItem updated = target;

        System.out.print("Apply autograph? (y/n): ");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            System.out.print("Signed by (wrestler name): ");
            String signedBy = scanner.nextLine();
            System.out.print("Autograph surcharge ($): ");
            try {
                double surcharge = Double.parseDouble(scanner.nextLine());
                updated = new AutographedDecorator(updated, signedBy, surcharge);
            } catch (NumberFormatException e) {
                System.out.println("Invalid surcharge. Skipping autograph.");
            }
        }

        System.out.print("Mark as limited edition? (y/n): ");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            System.out.print("Limited edition scarcity markup ($): ");
            try {
                double markup = Double.parseDouble(scanner.nextLine());
                updated = new LimitedEditionDecorator(updated, markup);
            } catch (NumberFormatException e) {
                System.out.println("Invalid markup. Skipping limited edition.");
            }
        }

        System.out.print("Apply a discount? (y/n): ");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            System.out.print("Discount percentage (e.g. 20 for 20% off): ");
            try {
                double discount = Double.parseDouble(scanner.nextLine());
                if (discount <= 0 || discount >= 100) {
                    System.out.println("WARNING: Discount must be between 0 and 100%. Skipping discount.");
                } else {
                    updated = new DiscountDecorator(updated, discount);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid discount. Skipping.");
            }
        }

        if (updated == target) {
            System.out.println("No decorations applied. Item unchanged.");
            return;
        }

        if (updated.isLowMargin()) {
            System.out.printf("WARNING: New margin is only %.2f%%. Proceed? (y/n): ", updated.getProfitMargin());
            if (!scanner.nextLine().equalsIgnoreCase("y")) {
                System.out.println("Redecoration cancelled.");
                return;
            }
        }

        // Flatten and overwrite the entry in the inventory list
        final int targetId = id;
        inventory.removeIf(i -> i.getID() == targetId);
        BasicMerchandiseItem flat = new BasicMerchandiseItem(
                updated.getID(),
                updated.getName(),
                updated.getSku(),
                updated.getWholesaleCost(),
                updated.getRetailPrice(),
                updated.getGlobalQuantity()
        );
        inventory.add(flat);
        writeItems(inventory);

        System.out.println("SUCCESS: Item updated.");
        System.out.println("  Name : " + flat.getName());
        System.out.printf ("  Price: $%.2f%n", flat.getRetailPrice());
        System.out.printf ("  Margin: %.2f%%%n", flat.getProfitMargin());
    }

    private static void createEmptyFile(Path path) throws IOException {
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        Files.writeString(path, "[]");
    }
}