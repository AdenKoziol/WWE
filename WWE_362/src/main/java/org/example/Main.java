package org.example;

import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        showMainMenu();
    }

    private static void showMainMenu() {
        while (true) {
            printHeader();

            System.out.println("Main Menu");
            System.out.println("1. Event Planning");
            System.out.println("2. Box Office (Ticketing)");
            System.out.println("3. Design Costume");
            System.out.println("4. Talent Management");
            System.out.println("5. Match Cards");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    EventPlanning.showMenu(scanner);
                    break;
                case "2":
                    BoxOffice.showMenu(scanner);
                    break;
                case "3":
                    CostumeDesigning.showmenu(scanner);
                    break;
                case "4":
                    TalentManagement.showMenu(scanner);
                    break;
                case "5":
                    MatchCardCreating.showMenu(scanner);
                    break;
                case "0":
                    System.out.println("Exiting... Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
                    pause();
            }
        }
    }

    private static void printHeader() {
        System.out.println("\n==============================");
        System.out.println("            WWE");
        System.out.println("==============================\n");
    }

    private static void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}
