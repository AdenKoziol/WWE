package org.example.api.controllers;

import org.example.api.JsonParser;
import org.example.models.Wrestler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class WrestlerController {

    private static final String WRESTLER_FILE = "src/main/java/org/example/database/Wrestlers.json";

    public static void createWrestler() {
        java.util.Scanner scanner = new java.util.Scanner(System.in);

        try {
            System.out.print("Enter stage name: ");
            String stageName = scanner.nextLine();

            System.out.print("Enter real name: ");
            String realName = scanner.nextLine();

            System.out.print("Enter hometown: ");
            String hometown = scanner.nextLine();

            System.out.print("Enter wrestling style: ");
            String style = scanner.nextLine();

            if (stageNameExists(stageName)) {
                System.out.println("Wrestler could not be created. Stage name already taken.");
                return;
            }

            Wrestler wrestler = new Wrestler(getNextID(), stageName, realName, hometown, style);

            if (wrestler.hasMissingInfo()) {
                System.out.println("Wrestler could not be created. Missing required information.");
                return;
            }

            saveWrestler(wrestler);
            System.out.println("Wrestler created successfully.");
            System.out.println(wrestler);

        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }

    public static void saveWrestler(Wrestler wrestler) {
        List<Wrestler> wrestlers = getAllWrestlers();
        wrestlers.add(wrestler);
        writeWrestlers(wrestlers);
    }

    public static int getNextID() {
        List<Wrestler> wrestlers = getAllWrestlers();

        int maxID = 0;

        for (Wrestler wrestler : wrestlers) {
            if (wrestler.getID() > maxID) {
                maxID = wrestler.getID();
            }
        }

        return maxID + 1;
    }

    public static List<Wrestler> getAllWrestlers() {
        try {
            Path path = Paths.get(WRESTLER_FILE);

            if (!Files.exists(path)) {
                createEmptyFile(path);
                return new ArrayList<>();
            }

            String json = Files.readString(path).trim();

            if (json.isEmpty()) {
                return new ArrayList<>();
            }

            List<Wrestler> wrestlers = JsonParser.deserializeList(json, Wrestler.class);
            return wrestlers != null ? wrestlers : new ArrayList<>();

        } catch (IOException e) {
            System.out.println("Error reading wrestler file.");
            return new ArrayList<>();
        }
    }

    public static boolean deleteWrestlerByID(int id) {
        List<Wrestler> wrestlers = getAllWrestlers();

        for (int i = 0; i < wrestlers.size(); i++) {
            Wrestler wrestler = wrestlers.get(i);

            if (wrestler.getID() == id) {
                wrestlers.remove(i);
                writeWrestlers(wrestlers);
                return true;
            }
        }

        return false;
    }

    public static void displayAllWrestlers() {
        List<Wrestler> wrestlers = getAllWrestlers();

        if (wrestlers.isEmpty()) {
            System.out.println("No wrestlers found.");
            return;
        }

        for (Wrestler wrestler : wrestlers) {
            System.out.println(wrestler);
        }
    }

    private static void writeWrestlers(List<Wrestler> wrestlers) {
        try {
            Path path = Paths.get(WRESTLER_FILE);

            if (!Files.exists(path)) {
                createEmptyFile(path);
            }

            String json = JsonParser.serialize(wrestlers);
            Files.writeString(path, json);

        } catch (IOException e) {
            System.out.println("Error writing wrestler file.");
        }
    }

    private static boolean stageNameExists(String stageName) {
    List<Wrestler> wrestlers = getAllWrestlers();

    for (Wrestler wrestler : wrestlers) {
        if (wrestler.getStageName() != null &&
            wrestler.getStageName().equalsIgnoreCase(stageName)) {
            return true;
        }
    }

    return false;
}

    private static void createEmptyFile(Path path) throws IOException {
        Path parent = path.getParent();

        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        Files.writeString(path, "[]");
    }
}