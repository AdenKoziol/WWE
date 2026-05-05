package org.example.api.controllers;

import org.example.api.JsonParser;
import org.example.models.*;

import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * BundleController manages the lifecycle of merchandise bundles.
 *
 * Bundles are stored in two places:
 *   1. Bundles.json  — stores { bundleSku, memberSkus[] } so that sale decrementing
 *                      knows which member SKUs to touch at runtime.
 *   2. Merch.json    — stores the bundle as a flattened BasicMerchandiseItem (name
 *                      and price baked in) so it appears in all existing inventory,
 *                      valuation, and restock reports without any changes to those methods.
 *
 * This dual-write strategy is the minimal extension of the existing flatten-on-save
 * approach: Merch.json stays simple and uniform, while Bundles.json provides the
 * structural metadata needed for correct multi-SKU sale processing.
 */
public class BundleController {

    private static final String BUNDLES_FILE = "src/main/java/org/example/database/Bundles.json";

    // =========================================================================
    // Bundle record — stored in Bundles.json
    // =========================================================================

    /**
     * Lightweight record persisted to Bundles.json.
     * Holds only the bundle's SKU and the list of member SKUs —
     * everything else (name, price, qty) lives in Merch.json.
     */
    public static class BundleRecord {
        public String bundleSku;
        public List<String> memberSkus;

        public BundleRecord() {}

        public BundleRecord(String bundleSku, List<String> memberSkus) {
            this.bundleSku = bundleSku;
            this.memberSkus = memberSkus;
        }
    }

    // =========================================================================
    // Create
    // =========================================================================

    public static void createBundle(Scanner scanner) {
        List<MerchandiseItem> allItems = MerchController.getAllItems();

        System.out.println("\n--- CREATE MERCHANDISE BUNDLE ---");
        System.out.println("Select items to include in the bundle (enter IDs one at a time, blank to finish):\n");
        System.out.printf("%-5s | %-35s | %-12s | %-8s | %s%n", "ID", "Name", "SKU", "Price", "Qty");
        System.out.println("-".repeat(72));
        for (MerchandiseItem item : allItems) {
            // Exclude items that are already bundles to avoid nesting bundles
            if (!item.getName().startsWith("[BUNDLE]")) {
                System.out.printf("%-5d | %-35s | %-12s | $%-7.2f | %d%n",
                        item.getID(), truncate(item.getName(), 35),
                        item.getSku(), item.getRetailPrice(), item.getGlobalQuantity());
            }
        }

        List<MerchandiseItem> selectedMembers = new ArrayList<>();
        List<String> memberSkus = new ArrayList<>();

        while (true) {
            System.out.print("Add Item ID (or blank to finish): ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                if (selectedMembers.size() < 2) {
                    System.out.println("A bundle must contain at least 2 items.");
                    continue;
                }
                break;
            }

            int memberId;
            try {
                memberId = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid ID.");
                continue;
            }

            MerchandiseItem member = allItems.stream()
                    .filter(i -> i.getID() == memberId && !i.getName().startsWith("[BUNDLE]"))
                    .findFirst().orElse(null);

            if (member == null) {
                System.out.println("Item not found or is itself a bundle.");
                continue;
            }
            if (memberSkus.contains(member.getSku())) {
                System.out.println("Item already added to this bundle.");
                continue;
            }

            selectedMembers.add(member);
            memberSkus.add(member.getSku());
            System.out.println("  Added: " + member.getName() + " ($" +
                    String.format("%.2f", member.getRetailPrice()) + ")");
        }

        // Show bundle price preview
        double totalRetail = selectedMembers.stream().mapToDouble(MerchandiseItem::getRetailPrice).sum();
        double totalWholesale = selectedMembers.stream().mapToDouble(MerchandiseItem::getWholesaleCost).sum();
        System.out.printf("%nBundle subtotal: $%.2f retail  |  $%.2f wholesale%n", totalRetail, totalWholesale);

        // Optional bundle-level discount
        double discountPercent = 0.0;
        System.out.print("Apply a bundle discount? (y/n): ");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            System.out.print("Discount percentage (e.g. 10 for 10% off): ");
            try {
                discountPercent = Double.parseDouble(scanner.nextLine());
                if (discountPercent <= 0 || discountPercent >= 100) {
                    System.out.println("Invalid discount. No discount applied.");
                    discountPercent = 0.0;
                } else {
                    System.out.printf("Discounted price: $%.2f%n", totalRetail * (1 - discountPercent / 100.0));
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. No discount applied.");
            }
        }

        System.out.print("Enter Bundle SKU (e.g. BUNDLE-JC-001): ");
        String bundleSku = scanner.nextLine().trim();
        if (bundleSku.isEmpty()) {
            System.out.println("SKU cannot be empty. Aborting.");
            return;
        }
        if (MerchController.findItemBySku(bundleSku) != null) {
            System.out.println("ERROR: SKU " + bundleSku + " already exists.");
            return;
        }

        // Determine bundle quantity = min available qty across all member items
        int bundleQty = selectedMembers.stream()
                .mapToInt(MerchandiseItem::getGlobalQuantity)
                .min()
                .orElse(0);
        System.out.printf("Maximum bundle quantity (limited by lowest-stock member): %d%n", bundleQty);
        System.out.printf("Enter bundle quantity to register (1-%d): ", bundleQty);
        int qty;
        try {
            qty = Integer.parseInt(scanner.nextLine().trim());
            if (qty <= 0 || qty > bundleQty) {
                System.out.printf("ERROR: Must be between 1 and %d.%n", bundleQty);
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity.");
            return;
        }

        // Build the BundleDecorator to get composed name, price, and margin
        int newId = MerchController.getNextIDPublic();
        BasicMerchandiseItem handle = new BasicMerchandiseItem(newId, "", bundleSku,
                totalWholesale, totalRetail, qty);
        MerchandiseItem bundle = new BundleDecorator(handle, selectedMembers);

        // Stack a DiscountDecorator if a discount was chosen
        if (discountPercent > 0) {
            bundle = new DiscountDecorator(bundle, discountPercent);
        }

        if (bundle.isLowMargin()) {
            System.out.printf("WARNING: Bundle margin is only %.2f%%. Proceed? (y/n): ",
                    bundle.getProfitMargin());
            if (!scanner.nextLine().equalsIgnoreCase("y")) {
                System.out.println("Bundle creation cancelled.");
                return;
            }
        }

        // Flatten and save to Merch.json
        BasicMerchandiseItem flat = new BasicMerchandiseItem(
                newId,
                bundle.getName(),
                bundleSku,
                bundle.getWholesaleCost(),
                bundle.getRetailPrice(),
                qty
        );
        MerchController.saveItem(flat);

        // Save structural record to Bundles.json
        List<BundleRecord> records = getAllBundleRecords();
        records.add(new BundleRecord(bundleSku, memberSkus));
        writeBundleRecords(records);

        System.out.println("\nSUCCESS: Bundle created.");
        System.out.println("  Name   : " + flat.getName());
        System.out.println("  SKU    : " + flat.getSku());
        System.out.printf ("  Price  : $%.2f%n", flat.getRetailPrice());
        System.out.printf ("  Margin : %.2f%%%n", flat.getProfitMargin());
        System.out.printf ("  Qty    : %d%n", flat.getGlobalQuantity());
        System.out.println("  Members: " + String.join(", ", memberSkus));
    }

    // =========================================================================
    // View
    // =========================================================================

    public static void viewBundles() {
        List<BundleRecord> records = getAllBundleRecords();
        List<MerchandiseItem> allItems = MerchController.getAllItems();

        System.out.println("\n========================================");
        System.out.println("       REGISTERED BUNDLES");
        System.out.println("========================================");

        if (records.isEmpty()) {
            System.out.println("No bundles registered.");
            return;
        }

        for (BundleRecord record : records) {
            MerchandiseItem bundleItem = allItems.stream()
                    .filter(i -> i.getSku().equalsIgnoreCase(record.bundleSku))
                    .findFirst().orElse(null);

            if (bundleItem == null) {
                System.out.println("Bundle SKU " + record.bundleSku + " — not found in inventory (may have been deleted).");
                continue;
            }

            System.out.println("\nBundle SKU : " + record.bundleSku);
            System.out.println("Name       : " + bundleItem.getName());
            System.out.printf ("Price      : $%.2f%n", bundleItem.getRetailPrice());
            System.out.printf ("Margin     : %.2f%%%n", bundleItem.getProfitMargin());
            System.out.printf ("Qty        : %d%n", bundleItem.getGlobalQuantity());
            System.out.println("Members    :");

            for (String memberSku : record.memberSkus) {
                MerchandiseItem member = allItems.stream()
                        .filter(i -> i.getSku().equalsIgnoreCase(memberSku))
                        .findFirst().orElse(null);
                if (member != null) {
                    System.out.printf("  %-12s | %-35s | $%.2f | Qty: %d%n",
                            memberSku, truncate(member.getName(), 35),
                            member.getRetailPrice(), member.getGlobalQuantity());
                } else {
                    System.out.printf("  %-12s | (item not found in inventory)%n", memberSku);
                }
            }
            System.out.println("  " + "-".repeat(60));
        }
    }

    // =========================================================================
    // Delete
    // =========================================================================

    public static void deleteBundle(Scanner scanner) {
        List<BundleRecord> records = getAllBundleRecords();

        if (records.isEmpty()) {
            System.out.println("No bundles to delete.");
            return;
        }

        viewBundles();
        System.out.print("\nEnter Bundle SKU to delete: ");
        String sku = scanner.nextLine().trim();

        BundleRecord toDelete = records.stream()
                .filter(r -> r.bundleSku.equalsIgnoreCase(sku))
                .findFirst().orElse(null);

        if (toDelete == null) {
            System.out.println("ERROR: Bundle SKU not found.");
            return;
        }

        System.out.print("Confirm deletion of bundle " + sku + "? (y/n): ");
        if (!scanner.nextLine().equalsIgnoreCase("y")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        // Remove from Bundles.json
        records.removeIf(r -> r.bundleSku.equalsIgnoreCase(sku));
        writeBundleRecords(records);

        // Remove from Merch.json
        List<MerchandiseItem> items = MerchController.getAllItems();
        items.removeIf(i -> i.getSku().equalsIgnoreCase(sku));
        MerchController.writeItemsPublic(items);

        System.out.println("SUCCESS: Bundle " + sku + " deleted.");
    }

    // =========================================================================
    // Bundle-aware sale support
    // =========================================================================

    /**
     * Returns the BundleRecord for a given SKU, or null if it's not a bundle.
     * Called by MerchStandController.processSale() to detect bundle SKUs.
     */
    public static BundleRecord findBundleRecord(String sku) {
        return getAllBundleRecords().stream()
                .filter(r -> r.bundleSku.equalsIgnoreCase(sku))
                .findFirst().orElse(null);
    }

    // =========================================================================
    // Persistence helpers
    // =========================================================================

    public static List<BundleRecord> getAllBundleRecords() {
        try {
            Path path = Paths.get(BUNDLES_FILE);
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.writeString(path, "[]");
                return new ArrayList<>();
            }
            String json = Files.readString(path).trim();
            if (json.isEmpty() || json.equals("[]")) return new ArrayList<>();
            List<BundleRecord> list = JsonParser.deserializeList(json, BundleRecord.class);
            list.removeIf(Objects::isNull);
            return list;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void writeBundleRecords(List<BundleRecord> records) {
        try {
            records.removeIf(Objects::isNull);
            Files.writeString(Paths.get(BUNDLES_FILE), JsonParser.serialize(records));
        } catch (Exception e) {
            System.out.println("Persistence Error: Could not write Bundles.json.");
        }
    }

    private static String truncate(String s, int maxLen) {
        if (s == null) return "";
        return s.length() <= maxLen ? s : s.substring(0, maxLen - 1) + "…";
    }
}
