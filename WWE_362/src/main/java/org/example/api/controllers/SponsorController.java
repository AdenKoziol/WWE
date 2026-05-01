package org.example.api.controllers;

import org.example.models.Sponsor;
import java.util.List;
import java.util.Scanner;

public class SponsorController extends AbstractController<Sponsor> {

    private static final String SPONSOR_FILE = "src/main/java/org/example/database/Sponsor.json";

    @Override
    protected String getFilePath() {
        return SPONSOR_FILE;
    }

    @Override
    protected Class<Sponsor> getType() {
        return Sponsor.class;
    }

    @Override
    protected int getID(Sponsor sponsor) {
        return sponsor.getID();
    }

    public static Sponsor createSponsor() {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter sponsor name: ");
            String name = scanner.nextLine();

            System.out.print("Enter contact name: ");
            String contactName = scanner.nextLine();

            System.out.print("Enter contact email: ");
            String contactEmail = scanner.nextLine();

            Sponsor sponsor = new Sponsor(getNextID(), name, contactName, contactEmail);

            if (sponsor.hasMissingInfo()) {
                System.out.println("Sponsor could not be created. Missing required information.");
                return null;
            }

            saveSponsor(sponsor);
            System.out.println("Sponsor created successfully.");
            System.out.println(sponsor);
            return sponsor;

        } catch (Exception e) {
            System.out.println("Invalid input. Sponsor could not be created.");
            return null;
        }
    }

    public static void saveSponsor(Sponsor sponsor) {
        SponsorController controller = new SponsorController();
        List<Sponsor> sponsors = controller.getAll();
        sponsors.add(sponsor);
        controller.writeAll(sponsors);
    }

    public static int getNextID() {
        SponsorController controller = new SponsorController();
        return controller.getNextID(controller.getAll());
    }

    public static List<Sponsor> getAllSponsors() {
        SponsorController controller = new SponsorController();
        return controller.getAll();
    }

    public Sponsor getSponsorByID(int id) {
        List<Sponsor> sponsors = getAllSponsors();

        for (Sponsor sponsor : sponsors) {
            if (sponsor.getID() == id) {
                return sponsor;
            }
        }

        return null;
    }

    public static void displayAllSponsors() {
        List<Sponsor> sponsors = getAllSponsors();

        if (sponsors.isEmpty()) {
            System.out.println("No sponsors found.");
            return;
        }

        for (Sponsor sponsor : sponsors) {
            System.out.println(sponsor);
        }
    }
}