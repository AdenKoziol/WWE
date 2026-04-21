package org.example;

import org.example.api.controllers.MerchController;
import java.util.Scanner;

public class MerchMenu {
    public static void showMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n--- WWE MERCHANDISE & LOGISTICS ---");
            System.out.println("1. Register New Stock Item (Inventory)");
            System.out.println("2. Assign Stock to Event Booth (Logistics)");
            System.out.println("3. View Global Inventory (All Products)");
            System.out.println("4. Search Product by SKU");
            System.out.println("5. Financial Stock Valuation Report");
            System.out.println("6. View Low Margin Alerts");
            System.out.println("0. Back");
            System.out.print("Selection: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1": MerchController.registerNewItem(scanner); break;
                case "2": MerchController.assignToBooth(scanner); break;
                case "3": MerchController.viewAllInventory(); break;
                case "4": MerchController.searchBySku(scanner); break;
                case "5": MerchController.viewStockValuation(); break;
                case "6": MerchController.viewLowMarginAlerts(); break;
                case "0": return;
                default: System.out.println("Invalid Option.");
            }
        }
    }
}