package org.example;

import java.util.Scanner;
import org.example.api.controllers.TicketController;

public class BoxOffice {

    public static void showMenu(Scanner scanner) {
        while (true) {
            printHeader();

            System.out.println("Box Office (Ticketing)");
            System.out.println("1. Process Ticket Sale");
            System.out.println("2. View All Sales");
            System.out.println("0. Back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    processSale(scanner);
                    break;
                case "2":
                    viewSales(scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
                    pause(scanner);
            }
        }
    }

    private static void processSale(Scanner scanner) {
        printHeader();
        System.out.println("Process Ticket Sale");
        TicketController.processSale(scanner);
        pause(scanner);
    }

    private static void viewSales(Scanner scanner) {
        printHeader();
        System.out.println("View All Sales");
        TicketController.displayAllSales();
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
}