package org.example;

import java.util.Scanner;
import org.example.api.controllers.EmployeeController;
import org.example.api.controllers.SecurityController;

public class ArenaSecurity {

    public static void showMenu(Scanner scanner) {
        while (true) {
            printHeader();

            System.out.println("Arena Security");
            System.out.println("1. Create Security Employee");
            System.out.println("2. Assign Security Staff to Event");
            System.out.println("3. Assign Security Chain");
            System.out.println("4. View Security Assignments");
            System.out.println("5. View Security Chain Assignments");
            System.out.println("6. Log Security Incident");
            System.out.println("7. View Incident Reports");
            System.out.println("8. Run Incident Escalation");
            System.out.println("9. Resolve Incident");
            System.out.println("0. Back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    createSecurityEmployee(scanner);
                    break;
                case "2":
                    assignSecurityToEvent(scanner);
                    break;
                case "3":
                    assignSecurityChain(scanner);
                    break;
                case "4":
                    viewSecurityAssignments(scanner);
                    break;
                case "5":
                    viewSecurityChainAssignments(scanner);
                    break;
                case "6":
                    logSecurityIncident(scanner);
                    break;
                case "7":
                    viewIncidentReports(scanner);
                    break;
                case "8":
                    runIncidentEscalation(scanner);
                    break;
                case "9":
                    resolveIncident(scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
                    pause(scanner);
            }
        }
    }

    private static void createSecurityEmployee(Scanner scanner) {
        printHeader();
        System.out.println("Create Security Employee");
        EmployeeController.createSecurityEmployee();
    }

    private static void assignSecurityToEvent(Scanner scanner) {
        printHeader();
        System.out.println("Assign Security Staff to Event");
        SecurityController.assignSecurityToEvent();
    }

    private static void assignSecurityChain(Scanner scanner) {
        printHeader();
        System.out.println("Assign Security Chain");
        SecurityController.assignSecurityChain();
    }

    private static void viewSecurityAssignments(Scanner scanner) {
        printHeader();
        System.out.println("View Security Assignments");
        SecurityController.displayAllAssignments();
    }

    private static void viewSecurityChainAssignments(Scanner scanner) {
        printHeader();
        System.out.println("View Security Chain Assignments");
        SecurityController.displayChainAssignmentsForEvent();
    }

    private static void logSecurityIncident(Scanner scanner) {
        printHeader();
        System.out.println("Log Security Incident");
        SecurityController.logIncident();
    }

    private static void viewIncidentReports(Scanner scanner) {
        printHeader();
        System.out.println("View Incident Reports");
        SecurityController.displayAllIncidents();
    }

    private static void runIncidentEscalation(Scanner scanner) {
        printHeader();
        System.out.println("Run Incident Escalation");
        SecurityController.runEscalation();
    }

    private static void resolveIncident(Scanner scanner) {
        printHeader();
        System.out.println("Resolve Incident");
        SecurityController.resolveIncident();
    }

    private static void printHeader() {
        System.out.println("\n==============================");
        System.out.println("      WWE Arena Security");
        System.out.println("==============================\n");
    }

    private static void pause(Scanner scanner) {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}