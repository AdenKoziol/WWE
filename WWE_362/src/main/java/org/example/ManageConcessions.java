package org.example;

import org.example.api.controllers.ConcessionItemController;
import org.example.api.controllers.ConcessionStandController;
import org.example.api.controllers.EmployeeController;

import java.util.Scanner;

public class ManageConcessions {

    public static void showMenu(Scanner scanner) {
        while (true) {
            printHeader();

            System.out.println("Manage Concessions");
            System.out.println("1. Manage Concession Stands");
            System.out.println("2. Manage Concession Items");
            System.out.println("3. Manage Employees");
            System.out.println("4. Manage Schedule");
            System.out.println("0. Back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    manageConcessionStands(scanner);
                    break;
                case "2":
                    manageConcessionItems(scanner);
                    break;
                case "3":
                    manageEmployees(scanner);
                    break;
                case "4":
                    viewEmployeeSchedule(scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
                    pause(scanner);
            }
        }
    }

    private static void manageConcessionStands(Scanner scanner) {
        while (true) {
            printHeader();
            System.out.println("Manage Concession Stands");
            System.out.println("1. Create Concession Stand");
            System.out.println("2. View All Concession Stands");
            System.out.println("3. Delete Concession Stand");
            System.out.println("4. View All Items Sold At Stand");
            System.out.println("5. View All Employees On Shift At Stand");
            System.out.println("0. Back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    ConcessionStandController.createConcessionStand();
                    pause(scanner);
                    break;
                case "2":
                    ConcessionStandController.displayAllConcessionStands();
                    pause(scanner);
                    break;
                case "3":
                    System.out.print("Enter concession stand ID to delete: ");
                    try {
                        int id = Integer.parseInt(scanner.nextLine());
                        boolean deleted = ConcessionStandController.deleteConcessionStandByID(id);

                        if (deleted) {
                            System.out.println("Concession stand deleted successfully.");
                        } else {
                            System.out.println("Concession stand not found.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid ID.");
                    }
                    pause(scanner);
                    break;
                case "4":
                    System.out.print("Enter concession stand ID: ");
                    try {
                        int id = Integer.parseInt(scanner.nextLine());
                        ConcessionStandController.displayAllItemsSoldAtStand(id);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid ID.");
                    }
                    pause(scanner);
                    break;
                case "5":
                    System.out.print("Enter concession stand ID: ");
                    try {
                        int id = Integer.parseInt(scanner.nextLine());
                        ConcessionStandController.displayAllEmployeesOnShiftAtStand(id);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid ID.");
                    }
                    pause(scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
                    pause(scanner);
            }
        }
    }

    private static void manageConcessionItems(Scanner scanner) {
        while (true) {
            printHeader();
            System.out.println("Manage Concession Items");
            System.out.println("1. Create Concession Item");
            System.out.println("2. View All Concession Items");
            System.out.println("3. Delete Concession Item");
            System.out.println("0. Back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    ConcessionItemController.createConcessionItem();
                    pause(scanner);
                    break;
                case "2":
                    ConcessionItemController.displayAllConcessionItems();
                    pause(scanner);
                    break;
                case "3":
                    System.out.print("Enter concession item ID to delete: ");
                    try {
                        int id = Integer.parseInt(scanner.nextLine());
                        boolean deleted = ConcessionItemController.deleteConcessionItemByID(id);

                        if (deleted) {
                            System.out.println("Concession item deleted successfully.");
                        } else {
                            System.out.println("Concession item not found.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid ID.");
                    }
                    pause(scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
                    pause(scanner);
            }
        }
    }

    private static void manageEmployees(Scanner scanner) {
        while (true) {
            printHeader();
            System.out.println("Manage Employees");
            System.out.println("1. Create Employee");
            System.out.println("2. View All Employees");
            System.out.println("3. Delete Employee");
            System.out.println("0. Back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    EmployeeController.createEmployee();
                    pause(scanner);
                    break;
                case "2":
                    EmployeeController.displayAllEmployees();
                    pause(scanner);
                    break;
                case "3":
                    System.out.print("Enter employee ID to delete: ");
                    try {
                        int id = Integer.parseInt(scanner.nextLine());
                        boolean deleted = EmployeeController.deleteEmployeeByID(id);

                        if (deleted) {
                            System.out.println("Employee deleted successfully.");
                        } else {
                            System.out.println("Employee not found.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid ID.");
                    }
                    pause(scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
                    pause(scanner);
            }
        }
    }

    private static void viewEmployeeSchedule(Scanner scanner) {

    }

    private static void addEmployeeToStand(Scanner scanner) {
        try {
            System.out.print("Enter concession stand ID: ");
            int standID = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter employee ID: ");
            int employeeID = Integer.parseInt(scanner.nextLine());

            boolean added = ConcessionStandController.addEmployeeToStand(standID, employeeID);

            if (added) {
                System.out.println("Employee added to concession stand successfully.");
            } else {
                System.out.println("Could not add employee to concession stand.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }

        pause(scanner);
    }

    private static void removeEmployeeFromStand(Scanner scanner) {
        try {
            System.out.print("Enter concession stand ID: ");
            int standID = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter employee ID: ");
            int employeeID = Integer.parseInt(scanner.nextLine());

            boolean removed = ConcessionStandController.removeEmployeeFromStand(standID, employeeID);

            if (removed) {
                System.out.println("Employee removed from concession stand successfully.");
            } else {
                System.out.println("Could not remove employee from concession stand.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }

        pause(scanner);
    }

    private static void addItemToStand(Scanner scanner) {
        try {
            System.out.print("Enter concession stand ID: ");
            int standID = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter concession item ID: ");
            int itemID = Integer.parseInt(scanner.nextLine());

            boolean added = ConcessionStandController.addItemToStand(standID, itemID);

            if (added) {
                System.out.println("Item added to concession stand successfully.");
            } else {
                System.out.println("Could not add item to concession stand.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }

        pause(scanner);
    }

    private static void removeItemFromStand(Scanner scanner) {
        try {
            System.out.print("Enter concession stand ID: ");
            int standID = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter concession item ID: ");
            int itemID = Integer.parseInt(scanner.nextLine());

            boolean removed = ConcessionStandController.removeItemFromStand(standID, itemID);

            if (removed) {
                System.out.println("Item removed from concession stand successfully.");
            } else {
                System.out.println("Could not remove item from concession stand.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }

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