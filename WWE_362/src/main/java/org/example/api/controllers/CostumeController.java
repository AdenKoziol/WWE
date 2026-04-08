package org.example.api.controllers;

import org.example.api.JsonParser;
import org.example.models.Costume; 
import org.example.models.Wrestler; 

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CostumeController {

    private static final String COSTUME_FILE = "WWE_362/src/main/java/org/example/database/Costumes.json";

    public static void newDesign() {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter Talent ID for this costume: ");
            int talentID = Integer.parseInt(scanner.nextLine());

            // if (WrestlerController.getWrestlerByID(talentID) == null) {
            //     System.out.println("Error: No wrestler found with ID " + talentID);
            //     return;
            // }
            
            System.out.print("Enter Costume Theme/Name: ");
            String theme = scanner.nextLine();

            System.out.print("Enter Primary Color: ");
            String color = scanner.nextLine();

            System.out.print("Enter Estimated Cost: ");
            double cost = Double.parseDouble(scanner.nextLine());

            Costume costume = new Costume(getNextID(), talentID, theme, color, cost);

            // Using the validation method you wrote in the Costume model
            if (costume.hasMissingInfo()) {
                System.out.println("Costume could not be created. Missing required information.");
                return;
            }

            saveCostume(costume);
            System.out.println("Costume design registered successfully.");
            System.out.println(costume);

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. IDs must be integers and Cost must be a number.");
        }
    }

    public static void changeDesign(int id) {
        List<Costume> costumes = getAllCostumes();
        for (Costume c : costumes) {
            if (c.getID() == id) {
                Scanner scanner = new Scanner(System.in);
                System.out.print("Enter new Theme (Current: " + c.getTheme() + "): ");
                c.setTheme(scanner.nextLine());
                
                writeCostumes(costumes);
                System.out.println("Design updated.");
                return;
            }
        }
        System.out.println("Costume ID not found.");
    }

    public static void deleteDesign(int id) {
        List<Costume> costumes = getAllCostumes();
        boolean removed = costumes.removeIf(c -> c.getID() == id);

        if (removed) {
            writeCostumes(costumes);
            System.out.println("Costume design removed from registry.");
        } else {
            System.out.println("No costume found with ID: " + id);
        }
    }

    public static void findDesign(int id) {
        List<Costume> costumes = getAllCostumes();
        for (Costume c : costumes) {
            if (c.getID() == id) {
                System.out.println("Design Found: " + c);
                return;
            }
        }
        System.out.println("No design found with that ID.");
    }


    public static void saveCostume(Costume costume) {
        List<Costume> costumes = getAllCostumes();
        costumes.add(costume);
        writeCostumes(costumes);
    }

    public static List<Costume> getAllCostumes() {
        try {
            Path path = Paths.get(COSTUME_FILE);
            if (!Files.exists(path)) {
                createEmptyFile(path);
                return new ArrayList<>();
            }
            String json = Files.readString(path).trim();
            if (json.isEmpty() || json.equals("[]")) return new ArrayList<>();

            return JsonParser.deserializeList(json, Costume.class);
        } catch (IOException e) {
            System.out.println("Error reading costume database.");
            return new ArrayList<>();
        }
    }

    public static int getNextID() {
        List<Costume> costumes = getAllCostumes();
        int maxID = 0;
        for (Costume c : costumes) {
            if (c.getID() > maxID) maxID = c.getID();
        }
        return maxID + 1;
    }

    private static void writeCostumes(List<Costume> costumes) {
        try {
            String json = JsonParser.serialize(costumes);
            Files.writeString(Paths.get(COSTUME_FILE), json);
        } catch (IOException e) {
            System.out.println("Error saving to file.");
        }
    }

    private static void createEmptyFile(Path path) throws IOException {
        if (path.getParent() != null) Files.createDirectories(path.getParent());
        Files.writeString(path, "[]");
    }
}