package org.example.api.controllers;

import org.example.api.JsonParser;
import org.example.models.ConcessionItem;
import org.example.models.ConcessionStand;
import org.example.models.Employee;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConcessionStandController {

    private static final String CONCESSION_STAND_FILE = "src/main/java/org/example/database/ConcessionStand.json";

    public static void createConcessionStand() {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter concession stand name: ");
            String name = scanner.nextLine();

            System.out.print("Enter concession stand location: ");
            String location = scanner.nextLine();

            ConcessionStand stand = new ConcessionStand(
                    getNextID(),
                    name,
                    location,
                    new ArrayList<>(),
                    new ArrayList<>()
            );

            saveConcessionStand(stand);
            System.out.println("Concession stand created successfully.");
            System.out.println(stand);

        } catch (Exception e) {
            System.out.println("Invalid input. Concession stand could not be created.");
        }
    }

    public static void saveConcessionStand(ConcessionStand stand) {
        List<ConcessionStand> stands = getAllConcessionStands();
        stands.add(stand);
        writeConcessionStands(stands);
    }

    public static int getNextID() {
        List<ConcessionStand> stands = getAllConcessionStands();

        int maxID = 0;

        for (ConcessionStand stand : stands) {
            if (stand.getID() > maxID) {
                maxID = stand.getID();
            }
        }

        return maxID + 1;
    }

    public static List<ConcessionStand> getAllConcessionStands() {
        try {
            Path path = Paths.get(CONCESSION_STAND_FILE);

            if (!Files.exists(path)) {
                createEmptyConcessionStandFile(path);
                return new ArrayList<>();
            }

            String json = Files.readString(path).trim();

            if (json.isEmpty()) {
                return new ArrayList<>();
            }

            List<ConcessionStand> stands = JsonParser.deserializeList(json, ConcessionStand.class);
            return stands != null ? stands : new ArrayList<>();

        } catch (IOException e) {
            System.out.println("Error reading concession stand file.");
            return new ArrayList<>();
        }
    }

    public ConcessionStand getConcessionStandByID(int id) {
        List<ConcessionStand> stands = getAllConcessionStands();

        for (ConcessionStand stand : stands) {
            if (stand.getID() == id) {
                return stand;
            }
        }

        return null;
    }

    public static boolean deleteConcessionStandByID(int id) {
        List<ConcessionStand> stands = getAllConcessionStands();

        for (int i = 0; i < stands.size(); i++) {
            ConcessionStand stand = stands.get(i);

            if (stand.getID() == id) {
                stands.remove(i);
                writeConcessionStands(stands);
                return true;
            }
        }

        return false;
    }

    public static void displayAllConcessionStands() {
        List<ConcessionStand> stands = getAllConcessionStands();

        if (stands.isEmpty()) {
            System.out.println("No concession stands found.");
            return;
        }

        for (ConcessionStand stand : stands) {
            System.out.println(stand);
        }
    }

    public static boolean addEmployeeToStand(int standID, int employeeID) {
        List<ConcessionStand> stands = getAllConcessionStands();
        EmployeeController employeeController = new EmployeeController();
        Employee employee = employeeController.getEmployeeByID(employeeID);

        if (employee == null) {
            return false;
        }

        for (ConcessionStand stand : stands) {
            if (stand.getID() == standID) {
                if (stand.getEmployeesOnShift() == null) {
                    stand.setEmployeesOnShift(new ArrayList<>());
                }

                for (Employee existingEmployee : stand.getEmployeesOnShift()) {
                    if (existingEmployee.getID() == employeeID) {
                        return false;
                    }
                }

                stand.getEmployeesOnShift().add(employee);
                writeConcessionStands(stands);
                return true;
            }
        }

        return false;
    }

    public static boolean removeEmployeeFromStand(int standID, int employeeID) {
        List<ConcessionStand> stands = getAllConcessionStands();

        for (ConcessionStand stand : stands) {
            if (stand.getID() == standID) {
                if (stand.getEmployeesOnShift() == null || stand.getEmployeesOnShift().isEmpty()) {
                    return false;
                }

                for (int i = 0; i < stand.getEmployeesOnShift().size(); i++) {
                    Employee employee = stand.getEmployeesOnShift().get(i);

                    if (employee.getID() == employeeID) {
                        stand.getEmployeesOnShift().remove(i);
                        writeConcessionStands(stands);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean addItemToStand(int standID, int itemID) {
        List<ConcessionStand> stands = getAllConcessionStands();
        ConcessionItemController itemController = new ConcessionItemController();
        ConcessionItem item = itemController.getConcessionItemByID(itemID);

        if (item == null) {
            return false;
        }

        for (ConcessionStand stand : stands) {
            if (stand.getID() == standID) {
                if (stand.getItemsSold() == null) {
                    stand.setItemsSold(new ArrayList<>());
                }

                for (ConcessionItem existingItem : stand.getItemsSold()) {
                    if (existingItem.getID() == itemID) {
                        return false;
                    }
                }

                stand.getItemsSold().add(item);
                writeConcessionStands(stands);
                return true;
            }
        }

        return false;
    }

    public static boolean removeItemFromStand(int standID, int itemID) {
        List<ConcessionStand> stands = getAllConcessionStands();

        for (ConcessionStand stand : stands) {
            if (stand.getID() == standID) {
                if (stand.getItemsSold() == null || stand.getItemsSold().isEmpty()) {
                    return false;
                }

                for (int i = 0; i < stand.getItemsSold().size(); i++) {
                    ConcessionItem item = stand.getItemsSold().get(i);

                    if (item.getID() == itemID) {
                        stand.getItemsSold().remove(i);
                        writeConcessionStands(stands);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static void displayAllItemsSoldAtStand(int standID) {
        List<ConcessionStand> stands = getAllConcessionStands();

        for (ConcessionStand stand : stands) {
            if (stand.getID() == standID) {
                List<ConcessionItem> items = stand.getItemsSold();

                if (items == null || items.isEmpty()) {
                    System.out.println("No concession items found for this stand.");
                    return;
                }

                for (ConcessionItem item : items) {
                    System.out.println(item);
                }
                return;
            }
        }

        System.out.println("Concession stand not found.");
    }

    public static void displayAllEmployeesOnShiftAtStand(int standID) {
        List<ConcessionStand> stands = getAllConcessionStands();

        for (ConcessionStand stand : stands) {
            if (stand.getID() == standID) {
                List<Employee> employees = stand.getEmployeesOnShift();

                if (employees == null || employees.isEmpty()) {
                    System.out.println("No employees on shift for this stand.");
                    return;
                }

                for (Employee employee : employees) {
                    System.out.println(employee);
                }
                return;
            }
        }

        System.out.println("Concession stand not found.");
    }

    private static void writeConcessionStands(List<ConcessionStand> stands) {
        try {
            Path path = Paths.get(CONCESSION_STAND_FILE);

            if (!Files.exists(path)) {
                createEmptyConcessionStandFile(path);
            }

            String json = JsonParser.serialize(stands);
            Files.writeString(path, json);

        } catch (IOException e) {
            System.out.println("Error writing concession stand file.");
        }
    }

    private static void createEmptyConcessionStandFile(Path path) throws IOException {
        Path parent = path.getParent();

        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        Files.writeString(path, "[]");
    }
}