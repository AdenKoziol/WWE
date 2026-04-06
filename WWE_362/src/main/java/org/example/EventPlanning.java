package org.example;

import java.util.Scanner;
import org.example.api.controllers.EventController;
import org.example.api.controllers.VenueController;

public class EventPlanning {

    public static void showMenu(Scanner scanner) {
        while (true) {
            printHeader();

            System.out.println("Event Planning");
            System.out.println("1. Create Event");
            System.out.println("2. Cancel Event");
            System.out.println("3. View Events");
            System.out.println("0. Back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    createEvent(scanner);
                    break;
                case "2":
                    cancelEvent(scanner);
                    break;
                case "3":
                    viewEvents(scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
                    pause(scanner);
            }
        }
    }

    private static void createEvent(Scanner scanner) {
        printHeader();
        System.out.println("Create Event");
        EventController.scheduleEvent();
    }

    private static void cancelEvent(Scanner scanner) {
        printHeader();
        System.out.println("Cancel Event");
        EventController.displayAllEvents();
        System.out.print("Enter event ID: ");
        String eventID = scanner.nextLine();
        EventController.deleteEventByID(Integer.parseInt(eventID));
    }

    private static void viewEvents(Scanner scanner) {
        printHeader();
        System.out.println("View Events");
        EventController.displayAllEvents();
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
}