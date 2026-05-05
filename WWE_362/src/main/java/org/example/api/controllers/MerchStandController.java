package org.example.api.controllers;

import org.example.api.JsonParser;
import org.example.models.MerchStand;
import org.example.models.Employee;
import org.example.models.InventoryEntry;
import org.example.models.MerchandiseItem;

import java.nio.file.*;
import java.util.*;

public class MerchStandController {
    private static final String STANDS_FILE = "src/main/java/org/example/database/MerchStands.json";

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
        if (entry == null || entry.getQuantity() < qty) {
            System.out.println("DENIED: Insufficient stock at this location.");
            return;
        }

        // Check if this SKU is a bundle — if so, also decrement each member SKU
        org.example.api.controllers.BundleController.BundleRecord bundleRecord =
                org.example.api.controllers.BundleController.findBundleRecord(sku);

        if (bundleRecord != null) {
            // Validate that every member SKU has enough stand stock before committing
            for (String memberSku : bundleRecord.memberSkus) {
                InventoryEntry memberEntry = stand.findEntry(memberSku);
                if (memberEntry == null || memberEntry.getQuantity() < qty) {
                    System.out.printf(
                            "DENIED: Bundle sale blocked — insufficient stand stock for member SKU %s (need %d, have %d).%n",
                            memberSku,
                            qty,
                            memberEntry == null ? 0 : memberEntry.getQuantity()
                    );
                    return;
                }
            }
            // All members have enough stock — decrement each one
            for (String memberSku : bundleRecord.memberSkus) {
                InventoryEntry memberEntry = stand.findEntry(memberSku);
                memberEntry.setQuantity(memberEntry.getQuantity() - qty);
            }
            System.out.println("BUNDLE SALE: Member SKUs decremented — " +
                    String.join(", ", bundleRecord.memberSkus));
        }

        // Decrement the bundle (or regular) SKU entry itself
        entry.setQuantity(entry.getQuantity() - qty);

        MerchandiseItem item = MerchController.findItemBySku(sku);
        if (item != null) {
            double saleAmount = item.getRetailPrice() * qty;
            stand.makeSale(saleAmount);
            System.out.printf("SALE RECORDED: Total $%.2f%n", saleAmount);
        }

        writeStands(stands);
    }

    public static void viewAllStandStocks() {
        List<MerchStand> stands = getAllStands();

        System.out.println("\n========================================");
        System.out.println("     MERCH STAND OPERATIONAL REPORT");
        System.out.println("========================================");

        if (stands.isEmpty()) {
            System.out.println("No stands registered.");
            return;
        }

        for (MerchStand stand : stands) {
            System.out.println("\nStand: " + stand.getStandID() + " (" + stand.getLocation() + ")");

            // Staff display
            String staffNames = stand.getStaffOnShift() == null || stand.getStaffOnShift().isEmpty()
                    ? "No Staff Assigned"
                    : stand.getStaffOnShift().stream()
                            .map(Employee::getName)
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("No Staff Assigned");
            System.out.println("  Staff    : " + staffNames);
            System.out.printf ("  Revenue  : $%.2f%n", stand.getProfit());

            // Inventory display
            List<InventoryEntry> inventory = stand.getLocalInventory();
            if (inventory == null || inventory.isEmpty()) {
                System.out.println("  Inventory: No items stocked.");
            } else {
                System.out.println("  Inventory:");
                System.out.printf("    %-12s | %-30s | %-8s | %s%n",
                        "SKU", "Name", "Price", "Qty");
                System.out.println("    " + "-".repeat(62));

                for (InventoryEntry entry : inventory) {
                    // Cross-reference the SKU against the global item registry
                    // to get the full item name and retail price (including decorator labels/prices)
                    MerchandiseItem globalItem = MerchController.findItemBySku(entry.getSku());

                    String displayName  = (globalItem != null) ? globalItem.getName()  : "(Unknown Item)";
                    String displayPrice = (globalItem != null)
                            ? String.format("$%.2f", globalItem.getRetailPrice())
                            : "N/A";

                    System.out.printf("    %-12s | %-30s | %-8s | %d%n",
                            entry.getSku(), displayName, displayPrice, entry.getQuantity());
                }
            }

            System.out.println("  " + "-".repeat(64));
        }
    }

    public static List<MerchStand> getAllStands() {
        try {
            Path path = Paths.get(STANDS_FILE);
            if (!Files.exists(path))
                return new ArrayList<>();
            String json = Files.readString(path).trim();
            if (json.isEmpty() || json.equals("[]"))
                return new ArrayList<>();

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

    public static void assignEmployeeToStand(Scanner scanner) {
        List<MerchStand> stands = getAllStands();

        System.out.print("Enter Stand ID (e.g., MAIN-ENT-01): ");
        String sid = scanner.nextLine();

        MerchStand stand = stands.stream()
                .filter(s -> s.getStandID().equalsIgnoreCase(sid))
                .findFirst().orElse(null);

        if (stand == null) {
            System.out.println("Stand not found.");
            return;
        }

        System.out.print("Enter Employee ID to assign: ");
        int empID = Integer.parseInt(scanner.nextLine());

        Employee emp = EmployeeController.getEmployeeByID(empID);

        if (emp != null) {
            if (stand.getStaffOnShift().stream().anyMatch(e -> e.getID() == empID)) {
                System.out.println("Employee is already assigned to this stand.");
                return;
            }
            stand.getStaffOnShift().add(emp);
            writeStands(stands);
            System.out.println("SUCCESS: " + emp.getName() + " assigned to " + sid);
        } else {
            System.out.println("ERROR: Employee ID not found in global records.");
        }
    }

    public static boolean removeEmployeeFromStand(String standID, int employeeID) {
        List<MerchStand> stands = getAllStands();
        boolean foundAndRemoved = false;
        for (MerchStand stand : stands) {
            if (stand.getStandID().equalsIgnoreCase(standID)) {
                if (stand.getStaffOnShift() != null) {
                    boolean removed = stand.getStaffOnShift().removeIf(e -> e.getID() == employeeID);
                    if (removed) {
                        foundAndRemoved = true;
                        writeStands(stands);
                        break;
                    }
                }
            }
        }
        return foundAndRemoved;
    }

    public static void removeEmployeeFromAllStands(int employeeID) {
        List<MerchStand> allStands = getAllStands();
        boolean changesMade = false;

        for (MerchStand stand : allStands) {
            boolean removed = stand.getStaffOnShift().removeIf(e -> e.getID() == employeeID);
            if (removed) changesMade = true;
        }

        if (changesMade) {
            writeStands(allStands);
            System.out.println("Global Cleanup: Employee removed from all active stand shifts.");
        }
    }
}