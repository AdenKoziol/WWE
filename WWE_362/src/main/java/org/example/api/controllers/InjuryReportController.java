package org.example.api.controllers;

import org.example.api.JsonParser;
import org.example.models.InjuryReport;
import org.example.models.Employee;
import org.example.models.Wrestler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class InjuryReportController {

    private static final String FILE = "src/main/java/org/example/database/InjuryReport.json";

    public static void logInjury() {
        java.util.Scanner scanner = new java.util.Scanner(System.in);

        try {
            System.out.println("\nWrestlers:");
            WrestlerController.displayAllWrestlers();

            System.out.print("Enter wrestler ID: ");
            int wrestlerID = Integer.parseInt(scanner.nextLine());

            System.out.println("\nMedical Staff:");
            EmployeeController.displayAllMedicalEmployees();

            System.out.print("Enter reporting staff employee ID: ");
            int staffID = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter injury type: ");
            String type = scanner.nextLine();

            System.out.print("Enter description: ");
            String desc = scanner.nextLine();

            System.out.print("Enter date: ");
            String date = scanner.nextLine();

            InjuryReport report = new InjuryReport(
                    getNextID(),
                    wrestlerID,
                    staffID,
                    type,
                    desc,
                    date
            );

            if (report.hasMissingInfo()) {
                System.out.println("Injury report could not be created.");
                return;
            }

            save(report);

            System.out.print("Update wrestler status to inactive? [Y/N]: ");
            String choice = scanner.nextLine();

            if (choice.equalsIgnoreCase("Y")) {
                WrestlerController.updateWrestlerStatus(wrestlerID, false);
                System.out.println("Wrestler marked as INACTIVE.");
            }

            System.out.println("Injury logged.");

        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }

    public static List<InjuryReport> getAll() {
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

            List<InjuryReport> list = JsonParser.deserializeList(json, InjuryReport.class);
            return list != null ? list : new ArrayList<>();

        } catch (IOException e) {
            System.out.println("Error reading file.");
            return new ArrayList<>();
        }
    }

    public static void displayAll() {
        List<InjuryReport> list = getAll();

        if (list.isEmpty()) {
            System.out.println("No reports found.");
            return;
        }

        for (InjuryReport r : list) {
            Wrestler w = WrestlerController.getWrestlerByID(r.getWrestlerID());
            Employee e = EmployeeController.getEmployeeByID(r.getReportingStaffID());

            String wrestlerName = (w != null) ? w.getStageName() : "Unknown";
            String staffName = (e != null) ? e.getName() : "Unknown";
            String status = (w != null && w.isActive()) ? "ACTIVE" : "INACTIVE";

            System.out.printf(
                "Report ID: %-5d | Wrestler: %-15s | Status: %-10s | Staff: %-15s | Type: %-12s | Date: %s%n",
                r.getID(),
                wrestlerName,
                status,
                staffName,
                r.getInjuryType(),
                r.getDate()
            );
        }
    }

    

    private static int getNextID() {
        List<InjuryReport> list = getAll();

        int max = 0;

        for (InjuryReport r : list) {
            if (r.getID() > max) {
                max = r.getID();
            }
        }

        return max + 1;
    }

    private static void save(InjuryReport r) {
        List<InjuryReport> list = getAll();
        list.add(r);
        write(list);
    }

    public static boolean deleteInjuryReportByID(int id) {
        List<InjuryReport> list = getAll();

        for (int i = 0; i < list.size(); i++) {
            InjuryReport r = list.get(i);

            if (r.getID() == id) {
                list.remove(i);
                write(list);
                return true;
            }
        }

        return false;
    }

    private static void write(List<InjuryReport> list) {
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