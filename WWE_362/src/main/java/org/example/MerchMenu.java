package org.example;

import org.example.api.controllers.MerchController;
import org.example.api.controllers.MerchStandController;
import java.util.Scanner;

public class MerchMenu {
    public static void showMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n========================================");
            System.out.println("      WWE MERCHANDISE & LOGISTICS");
            System.out.println("========================================");
            System.out.println("[ADMIN & SETUP]");
            System.out.println("1. Register New Stock Item (Warehouse)");
            System.out.println("2. Configure New Merch Stand (Location)");
            
            System.out.println("\n[LOGISTICS & SALES]");
            System.out.println("3. Assign Stock to Stand (Warehouse -> Stand)");
            System.out.println("4. Process Live Sale (Point of Sale)");
            System.out.println("5. View Stand Inventory Manifests");
            
            System.out.println("\n[INVENTORY & REPORTING]");
            System.out.println("6. View Global Inventory (All Products)");
            System.out.println("7. Search Product by SKU");
            System.out.println("8. Financial Stock Valuation Report");
            System.out.println("9. View Low Margin Alerts");
            System.out.println("0. Back");
            System.out.print("\nSelection: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1": MerchController.registerNewItem(scanner); break;
                case "2": MerchStandController.registerStand(scanner); break;
                case "3": MerchController.assignToBooth(scanner); break; 
                case "4": MerchStandController.processSale(scanner); break;
                case "5": MerchStandController.viewAllStandStocks(); break;
                case "6": MerchController.viewAllInventory(); break;
                case "7": MerchController.searchBySku(scanner); break;
                case "8": MerchController.viewStockValuation(); break;
                case "9": MerchController.viewLowMarginAlerts(); break;
                case "0": return;
                default: System.out.println("Invalid Option. Please try again.");
            }
        }
    }
}