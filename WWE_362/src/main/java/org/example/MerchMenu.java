package org.example;

import org.example.api.controllers.ConcessionStandController;
import org.example.api.controllers.EmployeeController;
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
            System.out.println("2. Configure New Merch Stand");
            System.out.println("3. Delete Merchandise Item");
            System.out.println("4. Delete Merch Stand");
            
            System.out.println("\n[LOGISTICS & SALES]");
            System.out.println("5. Assign Stock to Stand (Warehouse -> Stand)");
            System.out.println("6. Process Live Sale");
            System.out.println("7. View Stand Inventory Manifests");
            
            System.out.println("\n[INVENTORY & REPORTING]");
            System.out.println("8. View Global Inventory (All Products)");
            System.out.println("9. Search Product by SKU");
            System.out.println("10. Financial Stock Valuation Report");
            System.out.println("11. View Low Margin Alerts");

            System.out.println("\n[STAFFING]");
            System.out.println("12. Create Employee");
            System.out.println("13. View All Employees");
            System.out.println("14. Delete Employee");
            System.out.println("15. Assign Employee To Stand");
            System.out.println("16. Remove Employee From Stand");
            System.out.println("17. View Stand Staffing Assignments");

            System.out.println("0. Back");
            System.out.print("\nSelection: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1": MerchController.registerNewItem(scanner); break;
                case "2": MerchStandController.registerStand(scanner); break;
                case "3": MerchController.deleteMerchandise(scanner); break;
                case "4": MerchStandController.deleteStand(scanner); break;
                case "5": MerchController.assignToBooth(scanner); break; 
                case "6": MerchStandController.processSale(scanner); break;
                case "7": MerchStandController.viewAllStandStocks(); break;
                case "8": MerchController.viewAllInventory(); break;
                case "9": MerchController.searchBySku(scanner); break;
                case "10": MerchController.viewStockValuation(); break;
                case "11": MerchController.viewLowMarginAlerts(); break;


                case "12":
                    EmployeeController.createEmployee();
                    break;
                case "13":
                    EmployeeController.displayAllEmployees();
                    break;
                case "14":
                    System.out.print("Enter employee ID to delete: ");
                    try {
                        int id = Integer.parseInt(scanner.nextLine());
                        boolean deleted = EmployeeController.deleteEmployeeByID(id);
                        MerchStandController.removeEmployeeFromAllStands(id);
                        if (deleted) {
                            System.out.println("Employee deleted successfully.");
                        } else {
                            System.out.println("Employee not found.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid ID.");
                    }
                    break;
                case "15":
                    MerchStandController.assignEmployeeToStand(scanner);
                    break;
                case "16":
                    removeEmployeeFromStand(scanner);
                    break;


                case "17": MerchStandController.viewAllStandStocks(); break;


                case "0": return;
                default: System.out.println("Invalid Option. Please try again.");
            }

            
        }
    }

    private static void removeEmployeeFromStand(Scanner scanner) {
        try {
            System.out.print("Enter merch stand ID: ");
            String standID = scanner.nextLine();

            System.out.print("Enter employee ID: ");
            int employeeID = Integer.parseInt(scanner.nextLine());

            boolean removed = MerchStandController.removeEmployeeFromStand(standID, employeeID);

            if (removed) {
                System.out.println("Employee removed from stand successfully.");
            } else {
                System.out.println("Could not remove employee from stand.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }
    }
}