package org.example.api.controllers;

import org.example.api.JsonParser;
import org.example.models.MedicalStaffAssignment;
import org.example.models.Employee;
import org.example.models.Event;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MedicalStaffAssignmentController {

    private static final String FILE = "src/main/java/org/example/database/MedicalStaffAssignment.json";

    public static void assignEmployeeToEvent() {
        java.util.Scanner scanner = new java.util.Scanner(System.in);

        try {
            System.out.print("Enter employee ID: ");
            int employeeID = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter event ID: ");
            int eventID = Integer.parseInt(scanner.nextLine());

            MedicalStaffAssignment assignment = new MedicalStaffAssignment(employeeID, eventID);

            saveAssignment(assignment);
            System.out.println("Employee assigned to event.");

        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }

    public static boolean removeEmployeeFromEvent(int employeeID, int eventID) {
        List<MedicalStaffAssignment> list = getAllAssignments();

        for (int i = 0; i < list.size(); i++) {
            MedicalStaffAssignment a = list.get(i);

            if (a.getEmployeeID() == employeeID && a.getEventID() == eventID) {
                list.remove(i);
                write(list);
                return true;
            }
        }

        return false;
    }

    public static List<MedicalStaffAssignment> getAllAssignments() {
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

            List<MedicalStaffAssignment> list = JsonParser.deserializeList(json, MedicalStaffAssignment.class);
            return list != null ? list : new ArrayList<>();

        } catch (IOException e) {
            System.out.println("Error reading file.");
            return new ArrayList<>();
        }
    }

    public static void displayAllAssignments() {
    List<MedicalStaffAssignment> list = getAllAssignments();

    if (list.isEmpty()) {
        System.out.println("No assignments found.");
        return;
    }

    for (MedicalStaffAssignment a : list) {
        Employee employee = EmployeeController.getEmployeeByID(a.getEmployeeID());
        Event event = new EventController().getEventByID(a.getEventID());

        String employeeName = (employee != null) ? employee.getName() : "Unknown";
        String eventName = (event != null) ? event.getName() : "Unknown";
        String eventDate = (event != null) ? event.getDate() : "Unknown";
        String eventLocation = (event != null) ? event.getVenueName() : "Unknown";

        System.out.printf(
            "Employee ID: %-5d | Name: %-15s | Event ID: %-5d | Event: %-15s | Date: %-12s | Location: %s%n \n",
            a.getEmployeeID(),
            employeeName,
            a.getEventID(),
            eventName,
            eventDate,
            eventLocation
        );
    }
}

    private static void saveAssignment(MedicalStaffAssignment a) {
        List<MedicalStaffAssignment> list = getAllAssignments();
        list.add(a);
        write(list);
    }

    private static void write(List<MedicalStaffAssignment> list) {
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