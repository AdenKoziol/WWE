package org.example.api.controllers;

import org.example.api.JsonParser;
import org.example.models.ConcessionItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConcessionItemController {

    private static final String CONCESSION_ITEM_FILE = "src/main/java/org/example/database/ConcessionItem.json";

    public static void createConcessionItem() {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter item name: ");
            String name = scanner.nextLine();

            System.out.print("Enter item price: ");
            double price = Double.parseDouble(scanner.nextLine());

            ConcessionItem item = new ConcessionItem(getNextID(), name, price);

            saveConcessionItem(item);
            System.out.println("Concession item created successfully.");
            System.out.println(item);

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Price must be a number.");
        }
    }

    public static void saveConcessionItem(ConcessionItem item) {
        List<ConcessionItem> items = getAllConcessionItems();
        items.add(item);
        writeConcessionItems(items);
    }

    public static int getNextID() {
        List<ConcessionItem> items = getAllConcessionItems();

        int maxID = 0;

        for (ConcessionItem item : items) {
            if (item.getID() > maxID) {
                maxID = item.getID();
            }
        }

        return maxID + 1;
    }

    public static List<ConcessionItem> getAllConcessionItems() {
        try {
            Path path = Paths.get(CONCESSION_ITEM_FILE);

            if (!Files.exists(path)) {
                createEmptyConcessionItemFile(path);
                return new ArrayList<>();
            }

            String json = Files.readString(path).trim();

            if (json.isEmpty()) {
                return new ArrayList<>();
            }

            List<ConcessionItem> items = JsonParser.deserializeList(json, ConcessionItem.class);
            return items != null ? items : new ArrayList<>();

        } catch (IOException e) {
            System.out.println("Error reading concession item file.");
            return new ArrayList<>();
        }
    }

    public ConcessionItem getConcessionItemByID(int id) {
        List<ConcessionItem> items = getAllConcessionItems();

        for (ConcessionItem item : items) {
            if (item.getID() == id) {
                return item;
            }
        }

        return null;
    }

    public static boolean deleteConcessionItemByID(int id) {
        List<ConcessionItem> items = getAllConcessionItems();

        for (int i = 0; i < items.size(); i++) {
            ConcessionItem item = items.get(i);

            if (item.getID() == id) {
                items.remove(i);
                writeConcessionItems(items);
                return true;
            }
        }

        return false;
    }

    public static void displayAllConcessionItems() {
        List<ConcessionItem> items = getAllConcessionItems();

        if (items.isEmpty()) {
            System.out.println("No concession items found.");
            return;
        }

        for (ConcessionItem item : items) {
            System.out.println(item);
        }
    }

    private static void writeConcessionItems(List<ConcessionItem> items) {
        try {
            Path path = Paths.get(CONCESSION_ITEM_FILE);

            if (!Files.exists(path)) {
                createEmptyConcessionItemFile(path);
            }

            String json = JsonParser.serialize(items);
            Files.writeString(path, json);

        } catch (IOException e) {
            System.out.println("Error writing concession item file.");
        }
    }

    private static void createEmptyConcessionItemFile(Path path) throws IOException {
        Path parent = path.getParent();

        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        Files.writeString(path, "[]");
    }
}