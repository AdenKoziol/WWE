package org.example.api.controllers;

import org.example.models.Event;
import org.example.models.MatchCard;
import org.example.models.Wrestler;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MatchCardController extends AbstractController<MatchCard> {

    private static final String MATCH_CARD_FILE = "src/main/java/org/example/database/MatchCard.json";

    @Override
    protected String getFilePath() {
        return MATCH_CARD_FILE;
    }

    @Override
    protected Class<MatchCard> getType() {
        return MatchCard.class;
    }

    @Override
    protected int getID(MatchCard matchCard) {
        return matchCard.getID();
    }

    public static void createMatchCard() {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("Events: ");
            EventController.displayAllEvents();
            System.out.print("Enter event ID: ");
            int eventID = Integer.parseInt(scanner.nextLine());

            EventController eventController = new EventController();
            Event event = eventController.getEventByID(eventID);

            if (event == null) {
                System.out.println("Match card could not be created. Invalid event ID.");
                return;
            }

            System.out.println("Wrestlers: ");
            WrestlerController.displayAllWrestlers();

            System.out.print("Enter first wrestler stage name: ");
            String wrestler1 = scanner.nextLine();

            System.out.print("Enter second wrestler stage name: ");
            String wrestler2 = scanner.nextLine();

            System.out.print("Enter match type: ");
            String matchType = scanner.nextLine();

            MatchCard matchCard = new MatchCard(getNextID(), eventID, event.getName(), wrestler1, wrestler2, matchType);

            if (matchCard.hasMissingInfo()) {
                System.out.println("Match card could not be created. Missing required information.");
                return;
            }

            if (wrestler1.equalsIgnoreCase(wrestler2)) {
                System.out.println("Match card could not be created. Wrestlers must be different.");
                return;
            }

            if (!wrestlerExists(wrestler1) || !wrestlerExists(wrestler2)) {
                System.out.println("Match card could not be created. Wrestler does not exist.");
                return;
            }

            if (!wrestlerIsAvailable(wrestler1) || !wrestlerIsAvailable(wrestler2)) {
                System.out.println("Match card could not be created. Wrestler is not available.");
                return;
            }

            saveMatchCard(matchCard);
            System.out.println("Match card created successfully.");
            System.out.println(matchCard);

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Event ID must be a whole number.");
        }
    }

    private static boolean wrestlerExists(String stageName) {
        List<Wrestler> wrestlers = WrestlerController.getAllWrestlers();

        for (Wrestler wrestler : wrestlers) {
            if (wrestler.getStageName() != null && wrestler.getStageName().equalsIgnoreCase(stageName)) {
                return true;
            }
        }

        return false;
    }

    private static boolean wrestlerIsAvailable(String stageName) {
        List<Wrestler> wrestlers = WrestlerController.getAllWrestlers();

        for (Wrestler wrestler : wrestlers) {
            if (wrestler.getStageName() != null && wrestler.getStageName().equalsIgnoreCase(stageName)) {
                return wrestler.isActive();
            }
        }

        return false;
    }

    public static void saveMatchCard(MatchCard matchCard) {
        MatchCardController controller = new MatchCardController();
        List<MatchCard> matchCards = controller.getAll();
        matchCards.add(matchCard);
        controller.writeAll(matchCards);
    }

    public static int getNextID() {
        MatchCardController controller = new MatchCardController();
        return controller.getNextID(controller.getAll());
    }

    public static List<MatchCard> getAllMatchCards() {
        MatchCardController controller = new MatchCardController();
        return controller.getAll();
    }

    public static MatchCard getMatchCardByID(int id) {
        List<MatchCard> matchCards = getAllMatchCards();

        for (MatchCard matchCard : matchCards) {
            if (matchCard.getID() == id) {
                return matchCard;
            }
        }

        return null;
    }

    public static boolean deleteMatchCardByID(int id) {
        MatchCardController controller = new MatchCardController();
        List<MatchCard> matchCards = controller.getAll();

        for (int i = 0; i < matchCards.size(); i++) {
            MatchCard matchCard = matchCards.get(i);

            if (matchCard.getID() == id) {
                matchCards.remove(i);
                controller.writeAll(matchCards);
                return true;
            }
        }

        return false;
    }

    public static void displayAllMatchCards() {
        List<MatchCard> matchCards = getAllMatchCards();

        if (matchCards.isEmpty()) {
            System.out.println("No match cards found.");
            return;
        }

        for (MatchCard matchCard : matchCards) {
            System.out.println(matchCard);
        }
    }

    public static List<String> getWrestlersByEventID(int eventID) {
        List<MatchCard> matchCards = getAllMatchCards();
        List<String> wrestlers = new ArrayList<>();

        for (MatchCard matchCard : matchCards) {
            if (matchCard.getEventID() == eventID) {
                wrestlers.add(matchCard.getWrestler1());
                wrestlers.add(matchCard.getWrestler2());
            }
        }

        return wrestlers;
    }
}