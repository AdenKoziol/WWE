package org.example;

import org.example.api.controllers.EventController;
import org.example.api.controllers.ProductionAssignmentController;
import org.example.api.controllers.ProductionCrewController;
import org.example.models.Event;
import org.example.models.ProductionAssignment;
import org.example.models.ProductionCrew;

import java.util.Scanner;

public class ManageProduction {

    public static void showMenu(Scanner scanner) {
        manageProduction(scanner);
    }

    private static void manageProduction(Scanner scanner) {
        while (true) {
            printHeader();
            System.out.println("Existing Production Crews:");
            ProductionCrewController.displayAllCrews();
            System.out.println();
            System.out.println("1. Select Existing Production Crew");
            System.out.println("2. Create New Production Crew");
            System.out.println("0. Back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    selectExistingCrew(scanner);
                    break;
                case "2":
                    ProductionCrew crew = ProductionCrewController.createCrew();
                    if (crew != null) {
                        continue;
                    }
                    pause(scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
                    pause(scanner);
            }
        }
    }

    private static void selectExistingCrew(Scanner scanner) {
        System.out.print("Enter production crew ID: ");

        try {
            int crewID = Integer.parseInt(scanner.nextLine());

            ProductionCrewController crewController = new ProductionCrewController();
            ProductionCrew crew = crewController.getCrewByID(crewID);

            if (crew == null) {
                System.out.println("Production crew not found.");
                pause(scanner);
                return;
            }

            manageCrewAssignments(scanner, crewID);

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
            pause(scanner);
        }
    }

    private static void manageCrewAssignments(Scanner scanner, int crewID) {
        while (true) {
            printHeader();
            System.out.println("Production Crew Assignments");
            ProductionAssignmentController.displayAssignmentsForCrew(crewID);
            System.out.println();
            System.out.println("1. Create Production Assignment");
            System.out.println("2. Edit Production Assignment");
            System.out.println("3. Delete Production Assignment");
            System.out.println("4. Review Event Production");
            System.out.println("0. Back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    createProductionAssignment(scanner, crewID);
                    break;
                case "2":
                    editAssignment(scanner);
                    break;
                case "3":
                    deleteAssignment(scanner);
                    break;
                case "4":
                    reviewEventProduction(scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
                    pause(scanner);
            }
        }
    }

    private static void createProductionAssignment(Scanner scanner, int crewID) {
        printHeader();
        System.out.println("Create Production Assignment");
        System.out.println("Events:");
        EventController.displayAllEvents();
        System.out.print("Enter event ID: ");

        int eventID;

        try {
            eventID = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Event ID must be a whole number.");
            pause(scanner);
            return;
        }

        EventController eventController = new EventController();
        Event event = eventController.getEventByID(eventID);

        if (event == null) {
            System.out.println("Event ID does not exist.");
            pause(scanner);
            return;
        }

        System.out.print("Enter production role: ");
        String role = scanner.nextLine();

        if (ProductionAssignmentController.roleAlreadyAssigned(eventID, role)) {
            System.out.println("This role is already assigned for the event.");
            pause(scanner);
            return;
        }

        if (ProductionAssignmentController.crewHasActiveAssignment(crewID, eventID)) {
            System.out.println("Crew already has an active assignment for this event.");
            pause(scanner);
            return;
        }

        System.out.print("Enter assignment status: ");
        String status = scanner.nextLine();

        ProductionAssignment assignment = ProductionAssignmentController.createAssignment(crewID, eventID, role, status);

        if (assignment == null) {
            System.out.println("Production assignment details are invalid.");
            pause(scanner);
            return;
        }

        ProductionAssignmentController.saveAssignment(assignment);
        System.out.println("Production assignment created successfully.");
        System.out.println(assignment);
        ProductionAssignmentController.displayMissingRoles(eventID);
        pause(scanner);
    }

    private static void printHeader() {
        System.out.println("\n==============================");
        System.out.println("            WWE");
        System.out.println("==============================\n");
    }

    private static void pause(Scanner scanner) {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void editAssignment(Scanner scanner) {
        System.out.print("Enter assignment ID: ");

        try {
            int id = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter new role: ");
            String role = scanner.nextLine();

            System.out.print("Enter new status: ");
            String status = scanner.nextLine();

            boolean success = ProductionAssignmentController.updateAssignment(id, role, status);

            if (!success) {
                System.out.println("Update failed. Invalid input or conflict.");
            } else {
                System.out.println("Assignment updated successfully.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }

        pause(scanner);
    }

    private static void deleteAssignment(Scanner scanner) {
        System.out.print("Enter assignment ID: ");

        try {
            int id = Integer.parseInt(scanner.nextLine());

            boolean success = ProductionAssignmentController.deleteAssignmentByID(id);

            if (!success) {
                System.out.println("Assignment not found.");
            } else {
                System.out.println("Assignment deleted successfully.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }

        pause(scanner);
    }

    private static void reviewEventProduction(Scanner scanner) {
        System.out.println("Events:");
        EventController.displayAllEvents();

        System.out.print("Enter event ID: ");

        try {
            int eventID = Integer.parseInt(scanner.nextLine());

            EventController eventController = new EventController();
            Event event = eventController.getEventByID(eventID);

            if (event == null) {
                System.out.println("Event not found.");
                pause(scanner);
                return;
            }

            ProductionAssignmentController.displayAssignmentsForEvent(eventID);

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }

        pause(scanner);
    }
}