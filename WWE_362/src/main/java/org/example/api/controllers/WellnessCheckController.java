package org.example.api.controllers;

import org.example.api.JsonParser;
import org.example.models.WellnessCheck;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class WellnessCheckController {

    private static final String FILE = "src/main/java/org/example/database/WellnessCheck.json";

    public static void scheduleCheck() {
        java.util.Scanner scanner = new java.util.Scanner(System.in);

        try {
            System.out.println("\nWrestlers:");
            WrestlerController.displayAllWrestlers();

            System.out.print("Enter wrestler ID: ");
            int wrestlerID = Integer.parseInt(scanner.nextLine());

            System.out.println("\nMedical Staff:");
            EmployeeController.displayAllMedicalEmployees();

            System.out.print("Enter medical staff ID: ");
            int staffID = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter date: ");
            String date = scanner.nextLine();

            WellnessCheck wc = new WellnessCheck(
                    getNextID(),
                    wrestlerID,
                    staffID,
                    date,
                    "SCHEDULED",
                    ""
            );

            save(wc);
            System.out.println("Wellness check scheduled.");

        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }

    public static void logCheck() {
        java.util.Scanner scanner = new java.util.Scanner(System.in);

        try {
            List<WellnessCheck> scheduled = getScheduledChecks();

            if (scheduled.isEmpty()) {
                System.out.println("No scheduled wellness checks available.");
                return;
            }

            System.out.println("\nScheduled Wellness Checks:");

            for (WellnessCheck wc : scheduled) {
                org.example.models.Wrestler w =
                    WrestlerController.getWrestlerByID(wc.getWrestlerID());

                org.example.models.Employee e =
                    EmployeeController.getEmployeeByID(wc.getStaffID());

                String wrestlerName = (w != null) ? w.getStageName() : "Unknown";
                String staffName = (e != null) ? e.getName() : "Unknown";

                System.out.printf(
                    "Check ID: %-5d | Wrestler: %-15s | Staff: %-15s | Date: %s%n",
                    wc.getID(),
                    wrestlerName,
                    staffName,
                    wc.getDate()
                );
            }

            System.out.print("Enter check ID: ");
            int id = Integer.parseInt(scanner.nextLine());

            WellnessCheck selected = null;

            for (WellnessCheck wc : scheduled) {
                if (wc.getID() == id) {
                    selected = wc;
                    break;
                }
            }

            if (selected == null) {
                System.out.println("Invalid ID. Must select a scheduled check.");
                return;
            }

            System.out.print("Enter notes: ");
            String notes = scanner.nextLine();

            List<WellnessCheck> all = getAll();

            for (WellnessCheck wc : all) {
                if (wc.getID() == id) {
                    wc.setStatus("COMPLETED");
                    wc.setNotes(notes);
                    write(all);

                    System.out.print("Is the wrestler cleared to participate in their next event? [Y/N]: ");
                    String choice = scanner.nextLine();

                    if (choice.equalsIgnoreCase("Y")) {
                        WrestlerController.updateWrestlerStatus(wc.getWrestlerID(), true);
                        System.out.println("Wrestler marked ACTIVE.");
                    } else if (choice.equalsIgnoreCase("N")) {
                        WrestlerController.updateWrestlerStatus(wc.getWrestlerID(), false);
                        System.out.println("Wrestler marked INACTIVE.");
                    }

                    return;
                }
            }

        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }

    public static List<WellnessCheck> getScheduledChecks() {
        List<WellnessCheck> all = getAll();
        List<WellnessCheck> scheduled = new ArrayList<>();

        for (WellnessCheck wc : all) {
            if ("SCHEDULED".equalsIgnoreCase(wc.getStatus())) {
                scheduled.add(wc);
            }
        }

        return scheduled;
    }

    public static void cancelCheck(int id) {
        List<WellnessCheck> list = getAll();

        for (WellnessCheck wc : list) {
            if (wc.getID() == id) {
                wc.setStatus("CANCELLED");
                write(list);
                return;
            }
        }
    }

    public static List<WellnessCheck> getAll() {
        try {
            Path path = Paths.get(FILE);

            if (!Files.exists(path)) {
                createEmpty(path);
                return new ArrayList<>();
            }

            String json = Files.readString(path).trim();

            if (json.isEmpty()) {
                return new ArrayList<>();
            }

            List<WellnessCheck> list = JsonParser.deserializeList(json, WellnessCheck.class);
            return list != null ? list : new ArrayList<>();

        } catch (IOException e) {
            System.out.println("Error reading file.");
            return new ArrayList<>();
        }
    }

    public static void displayAll() {
        List<WellnessCheck> list = getAll();

        if (list.isEmpty()) {
            System.out.println("No checks found.");
            return;
        }

        for (WellnessCheck wc : list) {
            org.example.models.Wrestler w =
                WrestlerController.getWrestlerByID(wc.getWrestlerID());

            org.example.models.Employee e =
                EmployeeController.getEmployeeByID(wc.getStaffID());

            String wrestlerName = (w != null) ? w.getStageName() : "Unknown";
            String staffName = (e != null) ? e.getName() : "Unknown";

            System.out.printf(
                "Check ID: %-5d | Wrestler: %-15s | Staff: %-15s | Status: %-12s | Date: %s%n",
                wc.getID(),
                wrestlerName,
                staffName,
                wc.getStatus(),
                wc.getDate()
            );
        }
    }

    private static int getNextID() {
        List<WellnessCheck> list = getAll();

        int max = 0;

        for (WellnessCheck wc : list) {
            if (wc.getID() > max) {
                max = wc.getID();
            }
        }

        return max + 1;
    }

    private static void save(WellnessCheck wc) {
        List<WellnessCheck> list = getAll();
        list.add(wc);
        write(list);
    }

    private static void write(List<WellnessCheck> list) {
        try {
            Path path = Paths.get(FILE);

            if (!Files.exists(path)) {
                createEmpty(path);
            }

            String json = JsonParser.serialize(list);
            Files.writeString(path, json);

        } catch (IOException e) {
            System.out.println("Error writing file.");
        }
    }

    private static void createEmpty(Path path) throws IOException {
        Path parent = path.getParent();

        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        Files.writeString(path, "[]");
    }
}