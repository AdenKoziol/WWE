package org.example;

import java.util.Scanner;

import org.example.api.controllers.CostumeController;

public class CostumeDesigning {
    public static void showmenu(Scanner scanner) {
        while (true) {
            printHeader();

            System.out.println("Costume Designing");
            System.out.println("1. Register New Design");
            System.out.println("2. Update Existing Costume");
            System.out.println("3. Remove Costume ");
            System.out.println("4. Search Design Registry");
            System.out.println("0. Exit");

            
     String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    createDesign(scanner);
                    break;
                case "2":
                    updateDesign(scanner);
                    break;
                case "3":
                    removeDesign(scanner);
                    break;
                case "4":
                    searchDesign(scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
                    pause(scanner);
            }
        }
    }

    private static void createDesign(Scanner scanner) {
        printHeader();
        System.out.println("Create a Design");
        CostumeController.newDesign();
    }

    private static void updateDesign(Scanner scanner) {
        printHeader();
        System.out.println("Update a Design");
        System.out.print("Enter a Costume ID: ");
        String designID = scanner.nextLine();
        CostumeController.changeDesign(Integer.parseInt(designID));
    }

    private static void removeDesign(Scanner scanner) {
        printHeader();
        System.out.println("Delete a Design");
        System.out.print("Enter a Costume ID: ");
        String designID = scanner.nextLine();
        CostumeController.deleteDesign(Integer.parseInt(designID));
    }

    private static void searchDesign(Scanner scanner) {
        printHeader();
        System.out.println("Enter a Costume ID to Search For:");
        String designID = scanner.nextLine();
        CostumeController.findDesign(Integer.parseInt(designID));
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