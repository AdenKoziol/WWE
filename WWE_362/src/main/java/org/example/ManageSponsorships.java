package org.example;

import org.example.api.controllers.EventController;
import org.example.api.controllers.SponsorController;
import org.example.api.controllers.SponsorshipController;
import org.example.models.Event;
import org.example.models.Sponsor;
import org.example.models.Sponsorship;

import java.util.Scanner;

public class ManageSponsorships {

    public static void showMenu(Scanner scanner) {
        manageEventSponsorships(scanner);
    }

    private static void manageEventSponsorships(Scanner scanner) {
        while (true) {
            printHeader();
            System.out.println("Existing Sponsors:");
            SponsorController.displayAllSponsors();
            System.out.println();
            System.out.println("1. Select Existing Sponsor");
            System.out.println("2. Create New Sponsor");
            System.out.println("0. Back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    selectExistingSponsor(scanner);
                    break;
                case "2":
                    Sponsor sponsor = SponsorController.createSponsor();
                    if (sponsor != null) {
                        continue;
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

    private static void selectExistingSponsor(Scanner scanner) {
        System.out.print("Enter sponsor ID: ");

        try {
            int sponsorID = Integer.parseInt(scanner.nextLine());

            SponsorController sponsorController = new SponsorController();
            Sponsor sponsor = sponsorController.getSponsorByID(sponsorID);

            if (sponsor == null) {
                System.out.println("Sponsor not found.");
                pause(scanner);
                return;
            }

            manageSponsorSponsorships(scanner, sponsorID);

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
            pause(scanner);
        }
    }

    private static void manageSponsorSponsorships(Scanner scanner, int sponsorID) {
        while (true) {
            printHeader();
            System.out.println("Sponsor Event Sponsorships");
            SponsorshipController.displaySponsorshipsForSponsor(sponsorID);
            System.out.println();
            System.out.println("1. Create Event Sponsorship");
            System.out.println("2. Edit Event Sponsorship");
            System.out.println("3. Delete Event Sponsorship");
            System.out.println("0. Back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    createEventSponsorship(scanner, sponsorID);
                    break;
                case "2":
                    editEventSponsorship(scanner);
                    break;
                case "3":
                    deleteEventSponsorship(scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
                    pause(scanner);
            }
        }
    }

    private static void createEventSponsorship(Scanner scanner, int sponsorID) {
        while (true) {
            printHeader();
            System.out.println("Create Event Sponsorship");
            System.out.println("Events:");
            EventController.displayAllEvents();
            System.out.print("Enter event ID: ");

            int eventID;

            try {
                eventID = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Event ID must be a whole number.");
                pause(scanner);
                return;
            }

            EventController eventController = new EventController();
            Event event = eventController.getEventByID(eventID);

            if (event == null) {
                System.out.println("Event ID does not exist.");
                pause(scanner);
                return;
            }

            if (SponsorshipController.sponsorAlreadyHasSponsorshipForEvent(sponsorID, eventID)) {
                System.out.println("Sponsor already has a sponsorship for that event.");
                pause(scanner);
                return;
            }

            while (true) {
                try {
                    System.out.print("Enter sponsorship amount: ");
                    double amount = Double.parseDouble(scanner.nextLine());

                    System.out.print("Enter sponsorship status: ");
                    String status = scanner.nextLine();

                    Sponsorship sponsorship = SponsorshipController.createSponsorship(sponsorID, eventID, amount, status);

                    if (sponsorship == null) {
                        System.out.println("Sponsorship details are invalid. Reenter the information.");
                        continue;
                    }

                    SponsorshipController.saveSponsorship(sponsorship);
                    System.out.println("Event sponsorship created successfully.");
                    System.out.println(sponsorship);
                    return;

                } catch (NumberFormatException e) {
                    System.out.println("Sponsorship details are invalid. Reenter the information.");
                }
            }
        }
    }

    private static void editEventSponsorship(Scanner scanner) {
        try {
            System.out.print("Enter event sponsorship ID: ");
            int id = Integer.parseInt(scanner.nextLine());

            SponsorshipController sponsorshipController = new SponsorshipController();
            Sponsorship sponsorship = sponsorshipController.getSponsorshipByID(id);

            if (sponsorship == null) {
                System.out.println("Event sponsorship not found.");
                pause(scanner);
                return;
            }

            System.out.println(sponsorship);

            System.out.print("Enter updated sponsorship amount: ");
            double amount = Double.parseDouble(scanner.nextLine());

            System.out.print("Enter updated sponsorship status: ");
            String status = scanner.nextLine();

            boolean updated = SponsorshipController.updateSponsorship(id, amount, status);

            if (updated) {
                System.out.println("Event sponsorship updated successfully.");
            } else {
                System.out.println("Updated sponsorship information is invalid.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }

        pause(scanner);
    }

    private static void deleteEventSponsorship(Scanner scanner) {
        try {
            System.out.print("Enter event sponsorship ID: ");
            int id = Integer.parseInt(scanner.nextLine());

            SponsorshipController sponsorshipController = new SponsorshipController();
            Sponsorship sponsorship = sponsorshipController.getSponsorshipByID(id);

            if (sponsorship == null) {
                System.out.println("Event sponsorship not found.");
                pause(scanner);
                return;
            }

            System.out.println(sponsorship);
            System.out.print("Confirm deletion (y/n): ");
            String confirm = scanner.nextLine();

            if (!confirm.equalsIgnoreCase("y")) {
                System.out.println("Deletion cancelled.");
                pause(scanner);
                return;
            }

            boolean deleted = SponsorshipController.deleteSponsorshipByID(id);

            if (deleted) {
                System.out.println("Event sponsorship deleted successfully.");
            } else {
                System.out.println("Could not delete event sponsorship.");
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