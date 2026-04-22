package org.example;

import java.util.Scanner;
import org.example.api.controllers.EmployeeController;
import org.example.api.controllers.EventController;
import org.example.api.controllers.MedicalStaffAssignmentController;
import org.example.api.controllers.InjuryReportController;
import org.example.api.controllers.WellnessCheckController;

public class HealthAndSafety{

    public static void showMenu(Scanner scanner) {
        while (true) {
            printHeader();

            System.out.println("\n[Manage Medical Staff]");
            System.out.println("1. Create Employee");
            System.out.println("2. Delete Employee");
            System.out.println("3. View All Medical Staff");
            System.out.println("4. Assign Employee to Event");
            System.out.println("5. Remove Employee from Event");
            System.out.println("6. View Event Assignments");
            System.out.println("0. Back");

            System.out.println("\n[Manage Wrestlers and Injury Reports]");
            System.out.println("7. Log Injury Report");
            System.out.println("8. Delete Injury Report");
            System.out.println("9. Update Injury Status");
            System.out.println("10. View Injury Reports");
            System.out.println("11. View All Wrestlers Status");
            System.out.println("12. Log Wellness Check");
            System.out.println("13. Schedule Wellness Check");
            System.out.println("14. Cancel Wellness Check");
            System.out.println("15. View Wellness Checks");
            System.out.println("0. Back");
            System.out.print("\nChoose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    createEmployee(scanner);
                    break;
                case "2":
                    deleteEmployee(scanner);
                    break;
                case "3":
                    viewAllEmployees(scanner);
                    break;
                case "4":
                    assignEmployeeToEvent(scanner);
                    break;
                case "5":
                    removeEmployeeFromEvent(scanner);
                    break;
                case "6":
                    viewEventAssignments(scanner);
                    break;
                case "7":
                    logInjuryReport(scanner);
                    break;
                case "8":
                    deleteInjuryReport(scanner);
                    break;
                case "9":
                    updateInjuryStatus(scanner);
                    break;
                case "10":
                    viewInjuryReports(scanner);
                    break;
                case "11":
                    viewActiveInjuries(scanner);
                    break;
                case "12":
                    logWellnessCheck(scanner);
                    break;
                case "13":
                    scheduleWellnessCheck(scanner);
                    break;
                case "14":
                    cancelWellnessCheck(scanner);
                    break;
                case "15":
                    viewWellnessChecks(scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
                    pause(scanner);
            }
        }
    }

    private static void createEmployee(Scanner scanner) {
        printHeader();
        System.out.println("Create Employee");
        EmployeeController.createEmployee();
    }

    private static void deleteEmployee(Scanner scanner) {
        printHeader();
        System.out.println("Delete Employee");
        EmployeeController.displayAllMedicalEmployees();
        System.out.print("Enter employee ID: ");
        String id = scanner.nextLine();
        EmployeeController.deleteEmployeeByID(Integer.parseInt(id));
    }

    private static void viewAllEmployees(Scanner scanner) {
        printHeader();
        System.out.println("View All Medical Staff");
        EmployeeController.displayAllMedicalEmployees();
    }

    private static void assignEmployeeToEvent(Scanner scanner) {
        printHeader();
        System.out.println("Assign Employee to Event");
        EmployeeController.displayAllMedicalEmployees();
        EventController.displayAllEvents();
        MedicalStaffAssignmentController.assignEmployeeToEvent();
    }

    private static void removeEmployeeFromEvent(Scanner scanner) {
        printHeader();
        System.out.println("Remove Employee from Event");
        MedicalStaffAssignmentController.displayAllAssignments();
        System.out.print("Enter employee ID: ");
        int employeeID = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter event ID: ");
        int eventID = Integer.parseInt(scanner.nextLine());
        MedicalStaffAssignmentController.removeEmployeeFromEvent(employeeID, eventID);
    }

    private static void viewEventAssignments(Scanner scanner) {
        printHeader();
        System.out.println("View Event Assignments");
        MedicalStaffAssignmentController.displayAllAssignments();
    }

    private static void logInjuryReport(Scanner scanner) {
        printHeader();
        System.out.println("Log Injury Report");
        InjuryReportController.logInjury();
    }

    private static void deleteInjuryReport(Scanner scanner) {
        printHeader();
        System.out.println("Delete Injury Report");

        InjuryReportController.displayAll();

        System.out.print("Enter report ID: ");
        int id = Integer.parseInt(scanner.nextLine());

        boolean deleted = InjuryReportController.deleteInjuryReportByID(id);

        if (deleted) {
            System.out.println("Injury report deleted.");
        } else {
            System.out.println("Injury report not found.");
        }
    }

    private static void updateInjuryStatus(Scanner scanner) {
        printHeader();
        System.out.println("Update Wrestler Status");

        org.example.api.controllers.WrestlerController.displayAllWrestlers();

        System.out.print("Enter wrestler ID: ");
        int id = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter status (active/inactive): ");
        String input = scanner.nextLine();

        if (!input.equalsIgnoreCase("active") && !input.equalsIgnoreCase("inactive")) {
            System.out.println("Invalid status. Must be 'active' or 'inactive'.");
            return;
        }

        boolean active = input.equalsIgnoreCase("active");

        org.example.api.controllers.WrestlerController.updateWrestlerStatus(id, active);

        System.out.println("Wrestler status updated.");
    }

    private static void viewInjuryReports(Scanner scanner) {
        printHeader();
        System.out.println("View Injury Reports");
        InjuryReportController.displayAll();
    }

    private static void viewActiveInjuries(Scanner scanner) {
        printHeader();
        System.out.println("View All Wrestlers' Status");
        org.example.api.controllers.WrestlerController.displayAllWrestlers();
    }

    private static void logWellnessCheck(Scanner scanner) {
        printHeader();
        System.out.println("Log Wellness Check");
        WellnessCheckController.logCheck();
    }

    private static void scheduleWellnessCheck(Scanner scanner) {
        printHeader();
        System.out.println("Schedule Wellness Check");
        WellnessCheckController.scheduleCheck();
    }

    private static void cancelWellnessCheck(Scanner scanner) {
        printHeader();
        System.out.println("Cancel Wellness Check");
        WellnessCheckController.displayAll();
        System.out.print("Enter check ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        WellnessCheckController.cancelCheck(id);
    }

    private static void viewWellnessChecks(Scanner scanner) {
        printHeader();
        System.out.println("View Wellness Checks");
        WellnessCheckController.displayAll();
    }

    private static void printHeader() {
        System.out.println("\n==============================");
        System.out.println("     WWE Health and Safety");
        System.out.println("==============================\n");
    }

    private static void pause(Scanner scanner) {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}