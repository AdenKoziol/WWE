package org.example;

import org.example.api.controllers.ConcessionItemController;
import org.example.api.controllers.ConcessionStandController;
import org.example.api.controllers.EmployeeController;
import org.example.models.ConcessionItem;
import org.example.models.ConcessionStand;
import org.example.models.Employee;

import java.util.List;
import java.util.Scanner;

public class ManageConcessions {

    public static void showMenu(Scanner scanner) {
        while (true) {
            printHeader();

            System.out.println("Manage Concessions");
            System.out.println("1. Manage Concession Stands");
            System.out.println("2. Manage Concession Items");
            System.out.println("3. Manage Employees");
            System.out.println("4. View Schedule");
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
                    viewSchedule(scanner);
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
            System.out.println("0. Back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    ConcessionStandController.createConcessionStand();
                    break;
                case "2":
                    ConcessionStandController.displayAllConcessionStands();
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
            System.out.println("4. Assign Item To Stand");
            System.out.println("5. Remove Item From Stand");
            System.out.println("0. Back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    ConcessionItemController.createConcessionItem();
                    break;
                case "2":
                    ConcessionItemController.displayAllConcessionItems();
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
                    break;
                case "4":
                    assignItemToStand(scanner);
                    break;
                case "5":
                    removeItemFromStand(scanner);
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
            System.out.println("4. Assign Employee To Stand");
            System.out.println("5. Remove Employee From Stand");
            System.out.println("0. Back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    EmployeeController.createEmployee();
                    break;
                case "2":
                    EmployeeController.displayAllEmployees();
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
                    break;
                case "4":
                    assignEmployeeToStandFromEmployeeMenu(scanner);
                    break;
                case "5":
                    removeEmployeeFromStand(scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
                    pause(scanner);
            }
        }
    }

    private static void assignEmployeeToStandFromEmployeeMenu(Scanner scanner) {
        try {
            System.out.print("Enter employee ID: ");
            int employeeID = Integer.parseInt(scanner.nextLine());

            if (isEmployeeAssignedToAnyStand(employeeID)) {
                System.out.println("That employee is already assigned to another stand.");
                return;
            }

            System.out.print("Enter concession stand ID: ");
            int standID = Integer.parseInt(scanner.nextLine());

            boolean added = ConcessionStandController.addEmployeeToStand(standID, employeeID);

            if (added) {
                System.out.println("Employee assigned to stand successfully.");
            } else {
                System.out.println("Could not assign employee to stand.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }
    }

    private static void removeItemFromStand(Scanner scanner) {
        try {
            System.out.print("Enter concession stand ID: ");
            int standID = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter concession item ID: ");
            int itemID = Integer.parseInt(scanner.nextLine());

            boolean removed = ConcessionStandController.removeItemFromStand(standID, itemID);

            if (removed) {
                System.out.println("Item removed from stand successfully.");
            } else {
                System.out.println("Could not remove item from stand.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }
    }

    private static void removeEmployeeFromStand(Scanner scanner) {
        try {
            System.out.print("Enter concession stand ID: ");
            int standID = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter employee ID: ");
            int employeeID = Integer.parseInt(scanner.nextLine());

            boolean removed = ConcessionStandController.removeEmployeeFromStand(standID, employeeID);

            if (removed) {
                System.out.println("Employee removed from stand successfully.");
            } else {
                System.out.println("Could not remove employee from stand.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }
    }

    private static void assignItemToStand(Scanner scanner) {
        try {
            System.out.print("Enter concession item ID: ");
            int itemID = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter concession stand ID: ");
            int standID = Integer.parseInt(scanner.nextLine());

            if (isItemAlreadyAssignedToStand(standID, itemID)) {
                System.out.println("That item is already assigned to this stand.");
                return;
            }

            boolean added = ConcessionStandController.addItemToStand(standID, itemID);

            if (added) {
                System.out.println("Item assigned to stand successfully.");
            } else {
                System.out.println("Could not assign item to stand.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }
    }

    private static boolean isEmployeeAssignedToAnyStand(int employeeID) {
        List<ConcessionStand> stands = ConcessionStandController.getAllConcessionStands();

        if (stands == null || stands.isEmpty()) {
            return false;
        }

        for (ConcessionStand stand : stands) {
            List<Employee> employees = stand.getEmployeesOnShift();

            if (employees == null || employees.isEmpty()) {
                continue;
            }

            for (Employee employee : employees) {
                if (employee.getID() == employeeID) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean isItemAlreadyAssignedToStand(int standID, int itemID) {
        List<ConcessionStand> stands = ConcessionStandController.getAllConcessionStands();

        if (stands == null || stands.isEmpty()) {
            return false;
        }

        for (ConcessionStand stand : stands) {
            if (stand.getID() == standID) {
                List<ConcessionItem> items = stand.getItemsSold();

                if (items == null || items.isEmpty()) {
                    return false;
                }

                for (ConcessionItem item : items) {
                    if (item.getID() == itemID) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static void viewSchedule(Scanner scanner) {
        printHeader();
        System.out.println("View Schedule");
        System.out.println();

        List<ConcessionStand> stands = ConcessionStandController.getAllConcessionStands();

        if (stands == null || stands.isEmpty()) {
            System.out.println("No concession stands found.");
            return;
        }

        for (ConcessionStand stand : stands) {
            System.out.println("Stand ID: " + stand.getID());
            System.out.println("Name: " + stand.getName());
            System.out.println("Location: " + stand.getLocation());
            System.out.println("Employees On Shift:");

            List<Employee> employees = stand.getEmployeesOnShift();
            if (employees == null || employees.isEmpty()) {
                System.out.println("None");
            } else {
                for (Employee employee : employees) {
                    System.out.println("- " + employee.getName() + ", ID=" + employee.getID());
                }
            }

            System.out.println("Items Sold:");

            List<ConcessionItem> items = stand.getItemsSold();
            if (items == null || items.isEmpty()) {
                System.out.println("None");
            } else {
                for (ConcessionItem item : items) {
                    System.out.println("- " + item.getName() + ", ID=" + item.getID());
                }
            }

            System.out.println();
        }
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