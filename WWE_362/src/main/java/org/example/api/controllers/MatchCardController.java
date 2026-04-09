package org.example.api.controllers;

import org.example.api.JsonParser;
import org.example.models.Event;
import org.example.models.MatchCard;
import org.example.models.Wrestler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MatchCardController {

    private static final String MATCH_CARD_FILE = "src/main/java/org/example/database/MatchCard.json";

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
        List<MatchCard> matchCards = getAllMatchCards();
        matchCards.add(matchCard);
        writeMatchCards(matchCards);
    }

    public static int getNextID() {
        List<MatchCard> matchCards = getAllMatchCards();

        int maxID = 0;

        for (MatchCard matchCard : matchCards) {
            if (matchCard.getID() > maxID) {
                maxID = matchCard.getID();
            }
        }

        return maxID + 1;
    }

    public static List<MatchCard> getAllMatchCards() {
        try {
            Path path = Paths.get(MATCH_CARD_FILE);

            if (!Files.exists(path)) {
                createEmptyMatchCardFile(path);
                return new ArrayList<>();
            }

            String json = Files.readString(path).trim();

            if (json.isEmpty()) {
                return new ArrayList<>();
            }

            List<MatchCard> matchCards = JsonParser.deserializeList(json, MatchCard.class);
            return matchCards != null ? matchCards : new ArrayList<>();

        } catch (IOException e) {
            System.out.println("Error reading match card file.");
            return new ArrayList<>();
        }
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
        List<MatchCard> matchCards = getAllMatchCards();

        for (int i = 0; i < matchCards.size(); i++) {
            MatchCard matchCard = matchCards.get(i);

            if (matchCard.getID() == id) {
                matchCards.remove(i);
                writeMatchCards(matchCards);
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

    private static void writeMatchCards(List<MatchCard> matchCards) {
        try {
            Path path = Paths.get(MATCH_CARD_FILE);

            if (!Files.exists(path)) {
                createEmptyMatchCardFile(path);
            }

            String json = JsonParser.serialize(matchCards);
            Files.writeString(path, json);

        } catch (IOException e) {
            System.out.println("Error writing match card file.");
        }
    }

    private static void createEmptyMatchCardFile(Path path) throws IOException {
        Path parent = path.getParent();

        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        Files.writeString(path, "[]");
    }
}