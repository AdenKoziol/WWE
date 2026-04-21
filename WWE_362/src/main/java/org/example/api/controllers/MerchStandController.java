package org.example.api.controllers;

import org.example.api.JsonParser;
import org.example.models.MerchStand;
import org.example.models.ConcessionStand;
import org.example.models.Employee;
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
        System.out.println("\n--- MERCH STAND OPERATIONAL REPORT ---");
        for (MerchStand s : stands) {
            System.out.println("Stand: " + s.getStandID() + " (" + s.getLocation() + ")");

            // Display Staff
            String staffNames = s.getStaffOnShift().isEmpty() ? "No Staff Assigned"
                    : s.getStaffOnShift().stream().map(Employee::getName).reduce((a, b) -> a + ", " + b).get();
            System.out.println("  Staff: " + staffNames);
            System.out.println("  Revenue: $" + s.getProfit());
            System.out.println("  Inventory: " + s.getLocalInventory().size() + " items stocked.");
            System.out.println("------------------------------------------");
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

        // Lookup the employee from the global Employee list
        Employee emp = EmployeeController.getEmployeeByID(empID);

        if (emp != null) {
            // Prevent duplicate assignments
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
            // Use removeIf to purge any employee matching the ID
            boolean removed = stand.getStaffOnShift().removeIf(e -> e.getID() == employeeID);
            if (removed) {
                changesMade = true;
            }
        }

        if (changesMade) {
            writeStands(allStands);
            System.out.println("Global Cleanup: Employee removed from all active stand shifts.");
        }
    }
}