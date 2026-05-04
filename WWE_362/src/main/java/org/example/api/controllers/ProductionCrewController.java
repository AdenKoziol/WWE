package org.example.api.controllers;

import org.example.models.ProductionCrew;
import java.util.List;
import java.util.Scanner;

public class ProductionCrewController extends AbstractController<ProductionCrew> {

    private static final String CREW_FILE = "src/main/java/org/example/database/ProductionCrew.json";

    @Override
    protected String getFilePath() {
        return CREW_FILE;
    }

    @Override
    protected Class<ProductionCrew> getType() {
        return ProductionCrew.class;
    }

    @Override
    protected int getID(ProductionCrew crew) {
        return crew.getID();
    }

    public static ProductionCrew createCrew() {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter crew name: ");
            String crewName = scanner.nextLine();

            System.out.print("Enter supervisor name: ");
            String supervisorName = scanner.nextLine();

            ProductionCrew crew = new ProductionCrew(getNextID(), crewName, supervisorName);

            if (crew.hasMissingInfo()) {
                System.out.println("Crew could not be created. Missing required information.");
                return null;
            }

            saveCrew(crew);
            System.out.println("Production crew created successfully.");
            System.out.println(crew);
            return crew;

        } catch (Exception e) {
            System.out.println("Invalid input. Crew could not be created.");
            return null;
        }
    }

    public static void saveCrew(ProductionCrew crew) {
        ProductionCrewController controller = new ProductionCrewController();
        List<ProductionCrew> crews = controller.getAll();
        crews.add(crew);
        controller.writeAll(crews);
    }

    public static int getNextID() {
        ProductionCrewController controller = new ProductionCrewController();
        return controller.getNextID(controller.getAll());
    }

    public static List<ProductionCrew> getAllCrews() {
        ProductionCrewController controller = new ProductionCrewController();
        return controller.getAll();
    }

    public ProductionCrew getCrewByID(int id) {
        List<ProductionCrew> crews = getAllCrews();

        for (ProductionCrew crew : crews) {
            if (crew.getID() == id) {
                return crew;
            }
        }

        return null;
    }

    public static void displayAllCrews() {
        List<ProductionCrew> crews = getAllCrews();

        if (crews.isEmpty()) {
            System.out.println("No production crews found.");
            return;
        }

        for (ProductionCrew crew : crews) {
            System.out.println(crew);
        }
    }
}