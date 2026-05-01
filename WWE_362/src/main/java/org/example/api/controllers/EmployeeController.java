package org.example.api.controllers;

import org.example.api.JsonParser;
import org.example.models.Employee;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmployeeController {

    private static final String EMPLOYEE_FILE = "src/main/java/org/example/database/Employee.json";

    public static void createEmployee() {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter employee name: ");
            String name = scanner.nextLine();

            System.out.print("Enter employee type: ");
            String employeeType = scanner.nextLine();

            Employee employee = new Employee(getNextID(), name, employeeType);

            saveEmployee(employee);
            System.out.println("Employee created successfully.");
            System.out.println(employee);

        } catch (Exception e) {
            System.out.println("Invalid input. Employee could not be created.");
        }
    }

    public static void createSecurityEmployee() {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter employee name: ");
            String name = scanner.nextLine();

            System.out.print("Enter security role (Guard/Supervisor/Chief): ");
            String role = scanner.nextLine();

            Employee employee = new Employee(getNextID(), name, "Security", role);

            saveEmployee(employee);
            System.out.println("Security employee created successfully.");
            System.out.println(employee);

        } catch (Exception e) {
            System.out.println("Invalid input. Employee could not be created.");
        }
    }

    public static void saveEmployee(Employee employee) {
        List<Employee> employees = getAllEmployees();
        employees.add(employee);
        writeEmployees(employees);
    }

    public static int getNextID() {
        List<Employee> employees = getAllEmployees();

        int maxID = 0;

        for (Employee employee : employees) {
            if (employee.getID() > maxID) {
                maxID = employee.getID();
            }
        }

        return maxID + 1;
    }

    public static List<Employee> getAllEmployees() {
        try {
            Path path = Paths.get(EMPLOYEE_FILE);

            if (!Files.exists(path)) {
                createEmptyEmployeeFile(path);
                return new ArrayList<>();
            }

            String json = Files.readString(path).trim();

            if (json.isEmpty()) {
                return new ArrayList<>();
            }

            List<Employee> employees = JsonParser.deserializeList(json, Employee.class);
            return employees != null ? employees : new ArrayList<>();

        } catch (IOException e) {
            System.out.println("Error reading employee file.");
            return new ArrayList<>();
        }
    }

    public static Employee getEmployeeByID(int id) {
        List<Employee> employees = getAllEmployees();

        for (Employee employee : employees) {
            if (employee.getID() == id) {
                return employee;
            }
        }

        return null;
    }

    public static boolean deleteEmployeeByID(int id) {
        List<Employee> employees = getAllEmployees();

        for (int i = 0; i < employees.size(); i++) {
            Employee employee = employees.get(i);

            if (employee.getID() == id) {
                employees.remove(i);
                writeEmployees(employees);
                return true;
            }
        }

        return false;
    }

    public static void displayAllEmployees() {
        List<Employee> employees = getAllEmployees();

        if (employees.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }

        for (Employee employee : employees) {
            System.out.println(employee);
        }
    }

    public static void displayAllMedicalEmployees() {
        List<Employee> employees = getAllEmployees();

        if (employees.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }

        boolean found = false;

        for (Employee employee : employees) {
            if (employee.getEmployeeType() != null &&
                employee.getEmployeeType().equalsIgnoreCase("Medical")) {
                System.out.println(employee);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No medical employees found.");
        }
    }

    public static void displayAllSecurityEmployees() {
        List<Employee> employees = getAllEmployees();

        if (employees.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }

        boolean found = false;

        for (Employee employee : employees) {
            if (employee.getEmployeeType() != null &&
                employee.getEmployeeType().equalsIgnoreCase("Security")) {
                System.out.println(employee);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No security employees found.");
        }
    }

    private static void writeEmployees(List<Employee> employees) {
        try {
            Path path = Paths.get(EMPLOYEE_FILE);

            if (!Files.exists(path)) {
                createEmptyEmployeeFile(path);
            }

            String json = JsonParser.serialize(employees);
            Files.writeString(path, json);

        } catch (IOException e) {
            System.out.println("Error writing employee file.");
        }
    }

    private static void createEmptyEmployeeFile(Path path) throws IOException {
        Path parent = path.getParent();

        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        Files.writeString(path, "[]");
    }
}