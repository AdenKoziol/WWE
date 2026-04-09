package org.example;

import java.util.Scanner;
import org.example.api.controllers.MatchCardController;

public class MatchCardCreating {

    public static void showMenu(Scanner scanner) {
        while (true) {
            printHeader();

            System.out.println("Match Card Creating:");
            System.out.println("1. Create Match Card");
            System.out.println("2. Delete Match Card");
            System.out.println("3. View Match Cards");
            System.out.println("0. Back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    createMatchCard(scanner);
                    break;
                case "2":
                    deleteMatchCard(scanner);
                    break;
                case "3":
                    viewMatchCards(scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
                    pause(scanner);
            }
        }
    }

    private static void createMatchCard(Scanner scanner) {
        printHeader();
        System.out.println("Create Match Card");
        MatchCardController.createMatchCard();
    }

    private static void deleteMatchCard(Scanner scanner) {
        printHeader();
        System.out.println("Delete Match Card");
        MatchCardController.displayAllMatchCards();
        System.out.print("Enter match card ID: ");
        String matchCardID = scanner.nextLine();
        MatchCardController.deleteMatchCardByID(Integer.parseInt(matchCardID));
    }

    private static void viewMatchCards(Scanner scanner) {
        printHeader();
        System.out.println("View Match Cards");
        MatchCardController.displayAllMatchCards();
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