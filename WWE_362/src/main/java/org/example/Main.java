package org.example;

import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final DepartmentContext context = new DepartmentContext(scanner);

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
            System.out.println("6. Manage Concessions");
            System.out.println("7. Manage Sponsorships");
            System.out.println("8. Manage Merchandise");
            System.out.println("9. Manage Broadcasting");
            System.out.println("10. Health and Safety");
            System.out.println("11. Coordinate Travel");
            System.out.println("12. Arena Security");
            System.out.println("13. Manage Production");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    context.setState(new EventPlanningState());
                    context.showCurrentDepartment();
                    break;
                case "2":
                    context.setState(new BoxOfficeState());
                    context.showCurrentDepartment();
                    break;
                case "3":
                    context.setState(new CostumeDesigningState());
                    context.showCurrentDepartment();
                    break;
                case "4":
                    context.setState(new TalentManagementState());
                    context.showCurrentDepartment();
                    break;
                case "5":
                    context.setState(new MatchCardCreatingState());
                    context.showCurrentDepartment();
                    break;
                case "6":
                    context.setState(new ManageConcessionsState());
                    context.showCurrentDepartment();
                    break;
                case "7":
                    context.setState(new ManageSponsorshipsState());
                    context.showCurrentDepartment();
                    break;
                case "8":
                    context.setState(new MerchMenuState());
                    context.showCurrentDepartment();
                    break;
                case "9":
                    context.setState(new ManageBroadcastingState());
                    context.showCurrentDepartment();
                    break;
                case "10":
                    context.setState(new HealthAndSafetyState());
                    context.showCurrentDepartment();
                    break;
                case "11":
                    context.setState(new CoordinateTravelState());
                    context.showCurrentDepartment();
                    break;
                case "12":
                    context.setState(new ArenaSecurityState());
                    context.showCurrentDepartment();
                    break;
                case "13":
                    ManageProduction.showMenu(scanner);
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