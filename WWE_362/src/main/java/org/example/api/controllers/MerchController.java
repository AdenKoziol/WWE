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

    // Items whose global warehouse qty is at or below this threshold trigger a restock alert.
    private static final int RESTOCK_THRESHOLD = 20;

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

    // =========================================================================
    // FEATURE #1 — SALE REPORT
    // =========================================================================

    /**
     * Prints a consolidated sale/revenue report across all merch stands.
     *
     * For each stand, shows the revenue accumulated via processSale().
     * At the bottom, cross-references global items to show revenue potential
     * vs actual revenue collected — a useful management snapshot.
     */
    public static void viewSaleReport() {
        List<MerchStand> stands = MerchStandController.getAllStands();
        List<MerchandiseItem> items = getAllItems();

        System.out.println("\n========================================");
        System.out.println("        WWE MERCH SALES REPORT");
        System.out.println("========================================");

        if (stands.isEmpty()) {
            System.out.println("No stands registered.");
            return;
        }

        double grandTotal = 0.0;

        System.out.printf("%-20s | %-30s | %s%n", "Stand ID", "Location", "Revenue");
        System.out.println("-".repeat(68));

        for (MerchStand stand : stands) {
            double rev = stand.getProfit();
            grandTotal += rev;
            System.out.printf("%-20s | %-30s | $%.2f%n",
                    stand.getStandID(), stand.getLocation(), rev);
        }

        System.out.println("-".repeat(68));
        System.out.printf("%-53s $%.2f%n", "TOTAL REVENUE COLLECTED:", grandTotal);

        // --- Potential revenue still on the shelves (stand inventory) ---
        double standPotential = 0.0;
        for (MerchStand stand : stands) {
            for (InventoryEntry entry : stand.getLocalInventory()) {
                MerchandiseItem globalItem = items.stream()
                        .filter(i -> i.getSku().equalsIgnoreCase(entry.getSku()))
                        .findFirst().orElse(null);
                if (globalItem != null) {
                    standPotential += globalItem.getRetailPrice() * entry.getQuantity();
                }
            }
        }

        // --- Potential revenue still in the warehouse (global inventory) ---
        double warehousePotential = items.stream()
                .mapToDouble(i -> i.getRetailPrice() * i.getGlobalQuantity())
                .sum();

        System.out.println();
        System.out.printf("Unsold Stock (Stand Shelves):  $%.2f%n", standPotential);
        System.out.printf("Unsold Stock (Warehouse):      $%.2f%n", warehousePotential);
        System.out.printf("Total Remaining Potential:     $%.2f%n", standPotential + warehousePotential);
        System.out.println("========================================");
    }

    // =========================================================================
    // FEATURE #2 — RESTOCK ALERTS
    // =========================================================================

    /**
     * Flags items whose warehouse (global) quantity is at or below RESTOCK_THRESHOLD.
     *
     * Also shows total stock including stand copies so managers can decide
     * whether the alert is urgent (near zero everywhere) or merely a warehouse
     * replenishment call.
     */
    public static void viewRestockAlerts() {
        List<MerchandiseItem> items = getAllItems();
        List<MerchStand> stands = MerchStandController.getAllStands();

        System.out.println("\n========================================");
        System.out.println("       RESTOCK ALERT REPORT");
        System.out.printf ("       (Warehouse qty <= %d units)%n", RESTOCK_THRESHOLD);
        System.out.println("========================================");

        List<MerchandiseItem> lowStock = new ArrayList<>();
        for (MerchandiseItem item : items) {
            if (item.getGlobalQuantity() <= RESTOCK_THRESHOLD) {
                lowStock.add(item);
            }
        }

        if (lowStock.isEmpty()) {
            System.out.println("All items are sufficiently stocked. No alerts.");
            System.out.println("========================================");
            return;
        }

        System.out.printf("%-5s | %-30s | %-10s | %-12s | %-12s | %s%n",
                "ID", "Name", "SKU", "Warehouse Qty", "Stand Total", "STATUS");
        System.out.println("-".repeat(86));

        for (MerchandiseItem item : lowStock) {
            // Sum up all stand copies of this SKU
            int standTotal = stands.stream()
                    .flatMap(s -> s.getLocalInventory().stream())
                    .filter(e -> e.getSku().equalsIgnoreCase(item.getSku()))
                    .mapToInt(InventoryEntry::getQuantity)
                    .sum();

            int warehouseQty = item.getGlobalQuantity();
            String status;
            if (warehouseQty == 0 && standTotal == 0) {
                status = "*** OUT OF STOCK ***";
            } else if (warehouseQty == 0) {
                status = "! WAREHOUSE EMPTY";
            } else {
                status = "LOW — ORDER SOON";
            }

            System.out.printf("%-5d | %-30s | %-10s | %-13d | %-12d | %s%n",
                    item.getID(), truncate(item.getName(), 30), item.getSku(),
                    warehouseQty, standTotal, status);
        }

        System.out.println("========================================");
    }

    // =========================================================================
    // FEATURE #3 — STRIP DECORATIONS
    // =========================================================================

    /**
     * Strips all decorator labels from an item's name and resets its retail price
     * to its wholesale cost plus a user-supplied base margin, saving it as a
     * plain BasicMerchandiseItem.
     *
     * Because decorators are flattened to JSON (names/prices baked in), stripping
     * works by: (1) parsing known decorator prefixes from the name string, and
     * (2) asking the user to confirm or override the restored base retail price.
     *
     * This is the deliberate trade-off of our flatten-on-save persistence strategy:
     * decorator metadata isn't stored separately, so stripping is done on the
     * rendered name and we let the manager correct the base price manually.
     */
    public static void stripDecorations(Scanner scanner) {
        List<MerchandiseItem> inventory = getAllItems();

        System.out.println("\n--- STRIP DECORATIONS ---");
        System.out.println("This removes all decorator labels (AUTOGRAPHED, LIMITED EDITION,");
        System.out.println("discount tags) from an item and resets it to a plain base item.");
        System.out.println();

        // Show only items that appear to have decorator prefixes
        List<MerchandiseItem> decorated = new ArrayList<>();
        for (MerchandiseItem item : inventory) {
            if (hasDecoratorPrefix(item.getName())) {
                decorated.add(item);
            }
        }

        if (decorated.isEmpty()) {
            System.out.println("No decorated items found in inventory.");
            return;
        }

        System.out.printf("%-5s | %-40s | %-10s | %-8s%n", "ID", "Current Name", "SKU", "Price");
        System.out.println("-".repeat(68));
        for (MerchandiseItem item : decorated) {
            System.out.printf("%-5d | %-40s | %-10s | $%.2f%n",
                    item.getID(), truncate(item.getName(), 40), item.getSku(), item.getRetailPrice());
        }

        System.out.print("\nEnter Item ID to strip: ");
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

        if (!hasDecoratorPrefix(target.getName())) {
            System.out.println("Item '" + target.getName() + "' has no decorator labels to strip.");
            return;
        }

        // Strip all known decorator prefixes from the name
        String strippedName = stripAllPrefixes(target.getName());
        System.out.println();
        System.out.println("  Current name  : " + target.getName());
        System.out.println("  Stripped name : " + strippedName);
        System.out.printf ("  Current price : $%.2f%n", target.getRetailPrice());
        System.out.printf ("  Wholesale cost: $%.2f%n", target.getWholesaleCost());
        System.out.println();
        System.out.println("NOTE: Decorator price adjustments are baked into the current price.");
        System.out.printf ("Enter the restored base retail price (or press Enter to keep $%.2f): ",
                target.getWholesaleCost());

        double restoredPrice;
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            restoredPrice = target.getWholesaleCost();
        } else {
            try {
                restoredPrice = Double.parseDouble(input);
                if (restoredPrice <= 0) {
                    System.out.println("Price must be positive. Aborting.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid price. Aborting.");
                return;
            }
        }

        // Build the clean BasicMerchandiseItem and overwrite
        BasicMerchandiseItem stripped = new BasicMerchandiseItem(
                target.getID(),
                strippedName,
                target.getSku(),
                target.getWholesaleCost(),
                restoredPrice,
                target.getGlobalQuantity()
        );

        if (stripped.isLowMargin()) {
            System.out.printf("WARNING: Restored margin is %.2f%%. Proceed? (y/n): ",
                    stripped.getProfitMargin());
            if (!scanner.nextLine().equalsIgnoreCase("y")) {
                System.out.println("Strip cancelled.");
                return;
            }
        }

        final int targetId = id;
        inventory.removeIf(i -> i.getID() == targetId);
        inventory.add(stripped);
        writeItems(inventory);

        System.out.println("SUCCESS: Decorations stripped.");
        System.out.println("  Name  : " + stripped.getName());
        System.out.printf ("  Price : $%.2f%n", stripped.getRetailPrice());
        System.out.printf ("  Margin: %.2f%%%n", stripped.getProfitMargin());
    }

    /**
     * Returns true if the item name starts with any known decorator bracket prefix.
     */
    private static boolean hasDecoratorPrefix(String name) {
        if (name == null) return false;
        return name.startsWith("[AUTOGRAPHED")
                || name.startsWith("[LIMITED EDITION]")
                || name.matches("^\\[\\d+(\\.\\d+)?% OFF].*");
    }

    /**
     * Iteratively removes all leading decorator prefixes from a name string.
     * Handles stacked decorators in any order.
     *
     * Known prefixes:
     *   [AUTOGRAPHED by <name>]
     *   [LIMITED EDITION]
     *   [<N>% OFF]
     */
    private static String stripAllPrefixes(String name) {
        String result = name.trim();
        boolean changed = true;
        while (changed) {
            changed = false;

            // Strip [AUTOGRAPHED by ...] prefix
            if (result.startsWith("[AUTOGRAPHED by ")) {
                int close = result.indexOf(']');
                if (close != -1) {
                    result = result.substring(close + 1).trim();
                    changed = true;
                }
            }

            // Strip [LIMITED EDITION] prefix
            if (result.startsWith("[LIMITED EDITION]")) {
                result = result.substring("[LIMITED EDITION]".length()).trim();
                changed = true;
            }

            // Strip [<N>% OFF] prefix — e.g. [30% OFF] or [20.5% OFF]
            if (result.matches("^\\[\\d+(\\.\\d+)?% OFF].*")) {
                int close = result.indexOf(']');
                if (close != -1) {
                    result = result.substring(close + 1).trim();
                    changed = true;
                }
            }
        }
        return result;
    }

    /** Truncates a string to maxLen chars, appending "…" if cut. */
    private static String truncate(String s, int maxLen) {
        if (s == null) return "";
        return s.length() <= maxLen ? s : s.substring(0, maxLen - 1) + "…";
    }

    // =========================================================================
    // Existing helpers — unchanged
    // =========================================================================

    public static MerchandiseItem findItemBySku(String sku) {
        return getAllItems().stream()
                .filter(i -> i.getSku().equalsIgnoreCase(sku))
                .findFirst().orElse(null);
    }

    public static List<MerchandiseItem> getAllItems() {
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
        BasicMerchandiseItem flat = new BasicMerchandiseItem(
                item.getID(),
                item.getName(),
                item.getSku(),
                item.getWholesaleCost(),
                item.getRetailPrice(),
                item.getGlobalQuantity()
        );
        items.add(flat);
        writeItems(items);
    }

    public static void redecorateItem(Scanner scanner) {
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

    private static int getNextID() {
        List<MerchandiseItem> items = getAllItems();
        return items.stream().mapToInt(MerchandiseItem::getID).max().orElse(0) + 1;
    }

    private static void createEmptyFile(Path path) throws IOException {
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        Files.writeString(path, "[]");
    }
}