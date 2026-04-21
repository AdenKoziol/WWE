package org.example.api.controllers;

import org.example.api.JsonParser;
import org.example.models.Event;
import org.example.models.Sponsor;
import org.example.models.Sponsorship;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SponsorshipController {

    private static final String SPONSORSHIP_FILE = "src/main/java/org/example/database/Sponsorship.json";

    public static void saveSponsorship(Sponsorship sponsorship) {
        List<Sponsorship> sponsorships = getAllSponsorships();
        sponsorships.add(sponsorship);
        writeSponsorships(sponsorships);
    }

    public static int getNextID() {
        List<Sponsorship> sponsorships = getAllSponsorships();

        int maxID = 0;

        for (Sponsorship sponsorship : sponsorships) {
            if (sponsorship.getID() > maxID) {
                maxID = sponsorship.getID();
            }
        }

        return maxID + 1;
    }

    public static List<Sponsorship> getAllSponsorships() {
        try {
            Path path = Paths.get(SPONSORSHIP_FILE);

            if (!Files.exists(path)) {
                createEmptySponsorshipFile(path);
                return new ArrayList<>();
            }

            String json = Files.readString(path).trim();

            if (json.isEmpty()) {
                return new ArrayList<>();
            }

            List<Sponsorship> sponsorships = JsonParser.deserializeList(json, Sponsorship.class);
            return sponsorships != null ? sponsorships : new ArrayList<>();

        } catch (IOException e) {
            System.out.println("Error reading sponsorship file.");
            return new ArrayList<>();
        }
    }

    public Sponsorship getSponsorshipByID(int id) {
        List<Sponsorship> sponsorships = getAllSponsorships();

        for (Sponsorship sponsorship : sponsorships) {
            if (sponsorship.getID() == id) {
                return sponsorship;
            }
        }

        return null;
    }

    public static boolean deleteSponsorshipByID(int id) {
        List<Sponsorship> sponsorships = getAllSponsorships();

        for (int i = 0; i < sponsorships.size(); i++) {
            Sponsorship sponsorship = sponsorships.get(i);

            if (sponsorship.getID() == id) {
                sponsorships.remove(i);
                writeSponsorships(sponsorships);
                return true;
            }
        }

        return false;
    }

    public static boolean updateSponsorship(int id, double newAmount, String newStatus) {
        List<Sponsorship> sponsorships = getAllSponsorships();

        for (Sponsorship sponsorship : sponsorships) {
            if (sponsorship.getID() == id) {
                if (newAmount <= 0) {
                    return false;
                }

                if (newStatus == null || newStatus.trim().isEmpty()) {
                    return false;
                }

                sponsorship.setAmount(newAmount);
                sponsorship.setStatus(newStatus);
                writeSponsorships(sponsorships);
                return true;
            }
        }

        return false;
    }

    public static void displaySponsorshipsForSponsor(int sponsorID) {
        List<Sponsorship> sponsorships = getAllSponsorships();
        boolean found = false;

        for (Sponsorship sponsorship : sponsorships) {
            if (sponsorship.getSponsorID() == sponsorID) {
                System.out.println(sponsorship);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No sponsorships found for this sponsor.");
        }
    }

    public static boolean sponsorAlreadyHasSponsorshipForEvent(int sponsorID, int eventID) {
        List<Sponsorship> sponsorships = getAllSponsorships();

        for (Sponsorship sponsorship : sponsorships) {
            if (sponsorship.getSponsorID() == sponsorID && sponsorship.getEventID() == eventID) {
                return true;
            }
        }

        return false;
    }

    public static Sponsorship createSponsorship(int sponsorID, int eventID, double amount, String status) {
        SponsorController sponsorController = new SponsorController();
        Sponsor sponsor = sponsorController.getSponsorByID(sponsorID);

        EventController eventController = new EventController();
        Event event = eventController.getEventByID(eventID);

        if (sponsor == null || event == null) {
            return null;
        }

        Sponsorship sponsorship = new Sponsorship(
                getNextID(),
                sponsor.getID(),
                sponsor.getName(),
                event.getID(),
                event.getName(),
                amount,
                status
        );

        if (sponsorship.hasMissingInfo()) {
            return null;
        }

        if (amount <= 0) {
            return null;
        }

        return sponsorship;
    }

    private static void writeSponsorships(List<Sponsorship> sponsorships) {
        try {
            Path path = Paths.get(SPONSORSHIP_FILE);

            if (!Files.exists(path)) {
                createEmptySponsorshipFile(path);
            }

            String json = JsonParser.serialize(sponsorships);
            Files.writeString(path, json);

        } catch (IOException e) {
            System.out.println("Error writing sponsorship file.");
        }
    }

    private static void createEmptySponsorshipFile(Path path) throws IOException {
        Path parent = path.getParent();

        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        Files.writeString(path, "[]");
    }
}