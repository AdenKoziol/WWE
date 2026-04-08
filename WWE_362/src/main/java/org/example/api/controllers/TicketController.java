package org.example.api.controllers;

import org.example.api.JsonParser;
import org.example.models.Event;
import org.example.models.TicketSale;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TicketController {

    private static final String TICKET_FILE = "src/main/java/org/example/database/TicketSale.json";
    private static final double VIP_PRICE = 150.00;
    private static final double GA_PRICE = 50.00;

    public static void processSale(Scanner scanner) {
        try {
            System.out.println("Available Events:");
            EventController.displayAllEvents();
            
            System.out.print("\nEnter Event ID for the sale: ");
            int eventID = Integer.parseInt(scanner.nextLine());

            // Validate event exists
            EventController ec = new EventController();
            Event event = ec.getEventByID(eventID);
            if (event == null) {
                System.out.println("Invalid Event ID. Sale cancelled.");
                return;
            }

            System.out.println("Ticket Tiers: 1. VIP ($150) | 2. General Admission ($50)");
            System.out.print("Choose tier (1 or 2): ");
            String tierChoice = scanner.nextLine();
            String tier = tierChoice.equals("1") ? "VIP" : "General Admission";
            double pricePerTicket = tierChoice.equals("1") ? VIP_PRICE : GA_PRICE;

            System.out.print("Enter quantity: ");
            int quantity = Integer.parseInt(scanner.nextLine());

            double total = pricePerTicket * quantity;
            
            TicketSale sale = new TicketSale(getNextID(), eventID, tier, quantity, total);
            saveSale(sale);
            
            System.out.println("\nSale Processed Successfully!");
            System.out.println(sale);

        } catch (NumberFormatException e) {
            System.out.println("Invalid numerical input. Sale cancelled.");
        }
    }

    public static void displayAllSales() {
        List<TicketSale> sales = getAllSales();
        if (sales.isEmpty()) {
            System.out.println("No ticket sales recorded yet.");
            return;
        }
        for (TicketSale sale : sales) {
            System.out.println(sale);
        }
    }

    private static void saveSale(TicketSale sale) {
        List<TicketSale> sales = getAllSales();
        sales.add(sale);
        try {
            Path path = Paths.get(TICKET_FILE);
            if (!Files.exists(path)) createEmptyFile(path);
            String json = JsonParser.serialize(sales);
            Files.writeString(path, json);
        } catch (IOException e) {
            System.out.println("Error saving ticket sale.");
        }
    }

    private static List<TicketSale> getAllSales() {
        try {
            Path path = Paths.get(TICKET_FILE);
            if (!Files.exists(path)) {
                createEmptyFile(path);
                return new ArrayList<>();
            }
            String json = Files.readString(path).trim();
            if (json.isEmpty()) return new ArrayList<>();
            
            List<TicketSale> sales = JsonParser.deserializeList(json, TicketSale.class);
            return sales != null ? sales : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private static int getNextID() {
        List<TicketSale> sales = getAllSales();
        int maxID = 0;
        for (TicketSale sale : sales) {
            if (sale.getID() > maxID) maxID = sale.getID();
        }
        return maxID + 1;
    }

    private static void createEmptyFile(Path path) throws IOException {
        Path parent = path.getParent();
        if (parent != null && !Files.exists(parent)) Files.createDirectories(parent);
        Files.writeString(path, "[]");
    }
}
