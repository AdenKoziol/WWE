package org.example.api.controllers;

import org.example.models.Event;
import org.example.models.Sponsor;
import org.example.models.Sponsorship;
import java.util.List;

public class SponsorshipController extends AbstractController<Sponsorship> {

    private static final String SPONSORSHIP_FILE = "src/main/java/org/example/database/Sponsorship.json";

    @Override
    protected String getFilePath() {
        return SPONSORSHIP_FILE;
    }

    @Override
    protected Class<Sponsorship> getType() {
        return Sponsorship.class;
    }

    @Override
    protected int getID(Sponsorship sponsorship) {
        return sponsorship.getID();
    }

    public static void saveSponsorship(Sponsorship sponsorship) {
        SponsorshipController controller = new SponsorshipController();
        List<Sponsorship> sponsorships = controller.getAll();
        sponsorships.add(sponsorship);
        controller.writeAll(sponsorships);
    }

    public static int getNextID() {
        SponsorshipController controller = new SponsorshipController();
        return controller.getNextID(controller.getAll());
    }

    public static List<Sponsorship> getAllSponsorships() {
        SponsorshipController controller = new SponsorshipController();
        return controller.getAll();
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
        SponsorshipController controller = new SponsorshipController();
        List<Sponsorship> sponsorships = controller.getAll();

        for (int i = 0; i < sponsorships.size(); i++) {
            Sponsorship sponsorship = sponsorships.get(i);

            if (sponsorship.getID() == id) {
                sponsorships.remove(i);
                controller.writeAll(sponsorships);
                return true;
            }
        }

        return false;
    }

    public static boolean updateSponsorship(int id, double newAmount, String newStatus) {
        SponsorshipController controller = new SponsorshipController();
        List<Sponsorship> sponsorships = controller.getAll();

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
                controller.writeAll(sponsorships);
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
}