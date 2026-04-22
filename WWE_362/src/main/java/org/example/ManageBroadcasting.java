package org.example;

import org.example.api.controllers.BroadcastController;
import java.util.Scanner;

public class ManageBroadcasting {

    public static void showMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n==============================");
            System.out.println("            WWE");
            System.out.println("==============================\n");
            
            System.out.println("Manage Broadcasting");
            System.out.println("1. Register Broadcast Deal");
            System.out.println("2. View All Broadcast Deals");
            System.out.println("3. Delete Broadcast Deal");
            System.out.println("0. Back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    BroadcastController.registerDeal(scanner);
                    break;
                case "2":
                    BroadcastController.viewAllDeals();
                    break;
                case "3":
                    BroadcastController.deleteDeal(scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }
}
