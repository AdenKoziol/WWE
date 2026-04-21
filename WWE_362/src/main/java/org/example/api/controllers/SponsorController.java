package org.example.api.controllers;

import org.example.api.JsonParser;
import org.example.models.Sponsor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SponsorController {

    private static final String SPONSOR_FILE = "src/main/java/org/example/database/Sponsor.json";

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
        List<Sponsor> sponsors = getAllSponsors();
        sponsors.add(sponsor);
        writeSponsors(sponsors);
    }

    public static int getNextID() {
        List<Sponsor> sponsors = getAllSponsors();

        int maxID = 0;

        for (Sponsor sponsor : sponsors) {
            if (sponsor.getID() > maxID) {
                maxID = sponsor.getID();
            }
        }

        return maxID + 1;
    }

    public static List<Sponsor> getAllSponsors() {
        try {
            Path path = Paths.get(SPONSOR_FILE);

            if (!Files.exists(path)) {
                createEmptySponsorFile(path);
                return new ArrayList<>();
            }

            String json = Files.readString(path).trim();

            if (json.isEmpty()) {
                return new ArrayList<>();
            }

            List<Sponsor> sponsors = JsonParser.deserializeList(json, Sponsor.class);
            return sponsors != null ? sponsors : new ArrayList<>();

        } catch (IOException e) {
            System.out.println("Error reading sponsor file.");
            return new ArrayList<>();
        }
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

    private static void writeSponsors(List<Sponsor> sponsors) {
        try {
            Path path = Paths.get(SPONSOR_FILE);

            if (!Files.exists(path)) {
                createEmptySponsorFile(path);
            }

            String json = JsonParser.serialize(sponsors);
            Files.writeString(path, json);

        } catch (IOException e) {
            System.out.println("Error writing sponsor file.");
        }
    }

    private static void createEmptySponsorFile(Path path) throws IOException {
        Path parent = path.getParent();

        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        Files.writeString(path, "[]");
    }
}