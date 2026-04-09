package org.example;

import java.util.Scanner;
import org.example.api.controllers.WrestlerController;

public class TalentManagement {

    public static void showMenu(Scanner scanner) {
        while (true) {
            printHeader();

            System.out.println("Talent Management");
            System.out.println("1. Create Wrestler");
            System.out.println("2. Delete Wrestler");
            System.out.println("3. View Wrestlers");
            System.out.println("0. Back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    createWrestler(scanner);
                    break;
                case "2":
                    deleteWrestler(scanner);
                    break;
                case "3":
                    viewWrestlers(scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
                    pause(scanner);
            }
        }
    }

    private static void createWrestler(Scanner scanner) {
        printHeader();
        System.out.println("Create Wrestler");
        WrestlerController.createWrestler();
    }

    private static void deleteWrestler(Scanner scanner) {
        printHeader();
        System.out.println("Delete Wrestler");
        WrestlerController.displayAllWrestlers();
        System.out.print("Enter wrestler ID: ");
        String id = scanner.nextLine();
        WrestlerController.deleteWrestlerByID(Integer.parseInt(id));
    }

    private static void viewWrestlers(Scanner scanner) {
        printHeader();
        System.out.println("View Wrestlers");
        WrestlerController.displayAllWrestlers();
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