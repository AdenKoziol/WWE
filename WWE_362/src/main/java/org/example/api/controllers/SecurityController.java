package org.example.api.controllers;

import org.example.api.JsonParser;
import org.example.models.Employee;
import org.example.models.Event;
import org.example.models.SecurityAssignment;
import org.example.models.SecurityChainAssignment;
import org.example.models.SecurityIncident;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SecurityController {

    private static final String INCIDENT_FILE = "src/main/java/org/example/database/SecurityIncidents.json";
    private static final String ASSIGNMENT_FILE = "src/main/java/org/example/database/SecurityAssignments.json";
    private static final String CHAIN_FILE = "src/main/java/org/example/database/SecurityChainAssignments.json";

    public static void assignSecurityToEvent() {
        java.util.Scanner scanner = new java.util.Scanner(System.in);

        try {
            System.out.println("Events:");
            EventController.displayAllEvents();

            System.out.println("\nSecurity Staff:");
            EmployeeController.displayAllSecurityEmployees();

            System.out.print("Enter employee ID: ");
            int employeeID = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter event ID: ");
            int eventID = Integer.parseInt(scanner.nextLine());

            Employee employee = EmployeeController.getEmployeeByID(employeeID);

            if (employee == null || !"Security".equalsIgnoreCase(employee.getEmployeeType())) {
                System.out.println("Invalid security employee.");
                return;
            }

            if (isAssignedToEvent(employeeID, eventID)) {
                System.out.println("Employee is already assigned to this event.");
                return;
            }

            if ("Chief".equalsIgnoreCase(employee.getEmployeeRole()) && eventAlreadyHasChief(eventID)) {
                System.out.println("This event already has an Arena Chief assigned.");
                return;
            }

            SecurityAssignment assignment = new SecurityAssignment(employeeID, eventID);
            saveAssignment(assignment);

            System.out.println("Security assigned to event.");

        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }

    public static void assignSecurityChain() {
        java.util.Scanner scanner = new java.util.Scanner(System.in);

        try {
            System.out.println("Events:");
            EventController.displayAllEvents();

            System.out.print("Enter event ID: ");
            int eventID = Integer.parseInt(scanner.nextLine());

            System.out.println("\nSecurity Staff:");
            EmployeeController.displayAllSecurityEmployees();

            System.out.print("Enter employee ID: ");
            int employeeID = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter reports-to employee ID: ");
            int reportsToID = Integer.parseInt(scanner.nextLine());

            Employee employee = EmployeeController.getEmployeeByID(employeeID);
            Employee superior = EmployeeController.getEmployeeByID(reportsToID);

            if (employee == null || superior == null) {
                System.out.println("Invalid employee selection.");
                return;
            }

            if (!isAssignedToEvent(employeeID, eventID) || !isAssignedToEvent(reportsToID, eventID)) {
                System.out.println("Both employees must already be assigned to this event.");
                return;
            }

            if (alreadyHasSuperior(eventID, employeeID)) {
                System.out.println("This employee already has a superior assigned for this event.");
                return;
            }

            String employeeRole = employee.getEmployeeRole() == null ? "" : employee.getEmployeeRole();
            String superiorRole = superior.getEmployeeRole() == null ? "" : superior.getEmployeeRole();

            boolean validChain =
                    (employeeRole.equalsIgnoreCase("Guard") && superiorRole.equalsIgnoreCase("Supervisor")) ||
                    (employeeRole.equalsIgnoreCase("Supervisor") && superiorRole.equalsIgnoreCase("Chief"));

            if (!validChain) {
                System.out.println("Invalid chain. Only Guard -> Supervisor or Supervisor -> Chief are allowed.");
                return;
            }

            SecurityChainAssignment assignment = new SecurityChainAssignment(eventID, employeeID, reportsToID);
            saveChainAssignment(assignment);

            System.out.println("Security chain assignment saved.");

        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }

    public static void logIncident() {
        java.util.Scanner scanner = new java.util.Scanner(System.in);

        try {
            System.out.println("Events:");
            EventController.displayAllEvents();

            System.out.print("Enter event ID: ");
            int eventID = Integer.parseInt(scanner.nextLine());

            System.out.println("\nSecurity Staff:");
            EmployeeController.displayAllSecurityEmployees();

            System.out.print("Enter reporting employee ID: ");
            int employeeID = Integer.parseInt(scanner.nextLine());

            Employee employee = EmployeeController.getEmployeeByID(employeeID);

            if (employee == null || !"Security".equalsIgnoreCase(employee.getEmployeeType())) {
                System.out.println("Invalid security employee.");
                return;
            }

            System.out.print("Enter incident type: ");
            String type = scanner.nextLine();

            System.out.print("Enter location: ");
            String location = scanner.nextLine();

            System.out.print("Enter description: ");
            String description = scanner.nextLine();

            String role = employee.getEmployeeRole() == null ? "" : employee.getEmployeeRole();
            int assignedToID = employeeID;
            String escalationTrail = role;

            if (role.equalsIgnoreCase("Guard")) {
                System.out.print("Would you like to alert your Supervisor? [Y/N]: ");
                String input = scanner.nextLine();

                if (input.equalsIgnoreCase("Y")) {
                    int nextID = getReportsTo(eventID, employeeID);
                    if (nextID != -1) {
                        assignedToID = nextID;
                        escalationTrail = role + " -> Supervisor";
                    }
                }
            } else if (role.equalsIgnoreCase("Supervisor")) {
                System.out.print("Would you like to alert the Arena Chief? [Y/N]: ");
                String input = scanner.nextLine();

                if (input.equalsIgnoreCase("Y")) {
                    int nextID = getReportsTo(eventID, employeeID);
                    if (nextID != -1) {
                        assignedToID = nextID;
                        escalationTrail = role + " -> Arena Chief";
                    }
                }
            }

            SecurityIncident incident = new SecurityIncident(
                    getNextIncidentID(),
                    eventID,
                    employeeID,
                    assignedToID,
                    0,
                    type,
                    location,
                    description,
                    "OPEN",
                    escalationTrail,
                    ""
            );

            if (incident.hasMissingInfo()) {
                System.out.println("Incident could not be created.");
                return;
            }

            saveIncident(incident);
            System.out.println("Security incident logged.");

        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }

    public static void runEscalation() {
        java.util.Scanner scanner = new java.util.Scanner(System.in);

        try {
            System.out.println("Security Staff:");
            EmployeeController.displayAllSecurityEmployees();

            System.out.print("Enter your employee ID: ");
            int employeeID = Integer.parseInt(scanner.nextLine());

            List<SecurityIncident> incidents = getAllIncidents();
            List<SecurityIncident> assigned = new ArrayList<>();

            for (SecurityIncident incident : incidents) {
                if (incident.getAssignedToEmployeeID() == employeeID &&
                        !"RESOLVED".equalsIgnoreCase(incident.getStatus())) {
                    assigned.add(incident);
                    System.out.println(incident);
                }
            }

            if (assigned.isEmpty()) {
                System.out.println("No incidents assigned to you.");
                return;
            }

            System.out.print("Enter incident ID: ");
            int incidentID = Integer.parseInt(scanner.nextLine());

            for (SecurityIncident incident : assigned) {
                if (incident.getID() == incidentID) {
                    int nextID = getReportsTo(incident.getEventID(), employeeID);

                    if (nextID == -1) {
                        Employee current = EmployeeController.getEmployeeByID(employeeID);

                        if (current != null && "Chief".equalsIgnoreCase(current.getEmployeeRole())) {
                            System.out.print("Would you like to notify the police? [Y/N]: ");
                            String input = scanner.nextLine();

                            if (input.equalsIgnoreCase("Y")) {
                                System.out.println("The police have been called to report to the venue.");
                            }

                            return;
                        }

                        System.out.println("No higher authority assigned to this event.");
                        return;
                    }

                    Employee current = EmployeeController.getEmployeeByID(employeeID);
                    Employee next = EmployeeController.getEmployeeByID(nextID);

                    incident.setAssignedToEmployeeID(nextID);
                    incident.setEscalationTrail(
                            incident.getEscalationTrail() + " -> " + next.getEmployeeRole()
                    );

                    writeIncidents(incidents);
                    System.out.println("Incident escalated from " + current.getEmployeeRole() + " to " + next.getEmployeeRole() + ".");
                    return;
                }
            }

            System.out.println("Incident not found.");

        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }

    public static void resolveIncident() {
        java.util.Scanner scanner = new java.util.Scanner(System.in);

        try {
            System.out.println("Security Staff:");
            EmployeeController.displayAllSecurityEmployees();

            System.out.print("Enter your employee ID: ");
            int employeeID = Integer.parseInt(scanner.nextLine());

            List<SecurityIncident> incidents = getAllIncidents();
            List<SecurityIncident> assigned = new ArrayList<>();

            for (SecurityIncident incident : incidents) {
                if (incident.getAssignedToEmployeeID() == employeeID &&
                        !"RESOLVED".equalsIgnoreCase(incident.getStatus())) {
                    assigned.add(incident);
                    System.out.println(incident);
                }
            }

            if (assigned.isEmpty()) {
                System.out.println("No incidents assigned to you.");
                return;
            }

            System.out.print("Enter incident ID: ");
            int incidentID = Integer.parseInt(scanner.nextLine());

            for (SecurityIncident incident : assigned) {
                if (incident.getID() == incidentID) {
                    System.out.print("Enter resolution notes: ");
                    String notes = scanner.nextLine();

                    incident.setStatus("RESOLVED");
                    incident.setResolvedByEmployeeID(employeeID);
                    incident.setResolutionNotes(notes);

                    writeIncidents(incidents);
                    System.out.println("Incident resolved.");
                    return;
                }
            }

            System.out.println("Incident not found.");

        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }

    public static void displayAllIncidents() {
        List<SecurityIncident> incidents = getAllIncidents();

        if (incidents.isEmpty()) {
            System.out.println("No incidents found.");
            return;
        }

        for (SecurityIncident incident : incidents) {
            Event event = EventController.getEventByID(incident.getEventID());
            Employee reportedBy = EmployeeController.getEmployeeByID(incident.getReportedByEmployeeID());
            Employee assignedTo = EmployeeController.getEmployeeByID(incident.getAssignedToEmployeeID());

            String eventName = event != null ? event.getName() : "Unknown";
            String reporter = reportedBy != null ? reportedBy.getName() : "Unknown";
            String assignee = assignedTo != null ? assignedTo.getName() : "Unknown";

            System.out.printf(
                    "Incident ID: %-5d | Event: %-18s | Type: %-18s | Reported By: %-18s | Assigned To: %-18s | Status: %-10s%n",
                    incident.getID(),
                    eventName,
                    incident.getIncidentType(),
                    reporter,
                    assignee,
                    incident.getStatus()
            );
        }
    }

    public static void displayAllAssignments() {
        List<SecurityAssignment> assignments = getAllAssignments();

        if (assignments.isEmpty()) {
            System.out.println("No assignments found.");
            return;
        }

        for (SecurityAssignment assignment : assignments) {
            Employee employee = EmployeeController.getEmployeeByID(assignment.getEmployeeID());
            Event event = EventController.getEventByID(assignment.getEventID());

            String employeeName = employee != null ? employee.getName() : "Unknown";
            String eventName = event != null ? event.getName() : "Unknown";

            System.out.printf(
                    "Employee ID: %-5d | Name: %-18s | Role: %-12s | Event ID: %-5d | Event: %s%n",
                    assignment.getEmployeeID(),
                    employeeName,
                    employee != null ? employee.getEmployeeRole() : "Unknown",
                    assignment.getEventID(),
                    eventName
            );
        }
    }

    public static void displayChainAssignmentsForEvent() {
        java.util.Scanner scanner = new java.util.Scanner(System.in);

        try {
            System.out.println("Events:");
            EventController.displayAllEvents();

            System.out.print("Enter event ID: ");
            int eventID = Integer.parseInt(scanner.nextLine());

            List<SecurityChainAssignment> assignments = getAllChainAssignments();
            boolean found = false;

            for (SecurityChainAssignment assignment : assignments) {
                if (assignment.getEventID() == eventID) {
                    Employee employee = EmployeeController.getEmployeeByID(assignment.getEmployeeID());
                    Employee superior = EmployeeController.getEmployeeByID(assignment.getReportsToEmployeeID());

                    String employeeName = employee != null ? employee.getName() : "Unknown";
                    String employeeRole = employee != null ? employee.getEmployeeRole() : "Unknown";

                    String superiorName = superior != null ? superior.getName() : "Unknown";
                    String superiorRole = superior != null ? superior.getEmployeeRole() : "Unknown";

                    System.out.printf(
                            "Employee ID: %-5d | %-18s (%-12s) -> Reports To: %-5d | %-18s (%-12s)%n",
                            assignment.getEmployeeID(),
                            employeeName,
                            employeeRole,
                            assignment.getReportsToEmployeeID(),
                            superiorName,
                            superiorRole
                    );

                    found = true;
                }
            }

            if (!found) {
                System.out.println("No chain assignments found for this event.");
            }

        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }

    private static boolean eventAlreadyHasChief(int eventID) {
        List<SecurityAssignment> assignments = getAllAssignments();

        for (SecurityAssignment assignment : assignments) {
            if (assignment.getEventID() == eventID) {
                Employee employee = EmployeeController.getEmployeeByID(assignment.getEmployeeID());

                if (employee != null && "Chief".equalsIgnoreCase(employee.getEmployeeRole())) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean alreadyHasSuperior(int eventID, int employeeID) {
        List<SecurityChainAssignment> assignments = getAllChainAssignments();

        for (SecurityChainAssignment assignment : assignments) {
            if (assignment.getEventID() == eventID &&
                assignment.getEmployeeID() == employeeID) {
                return true;
            }
        }

        return false;
    }

    private static boolean isAssignedToEvent(int employeeID, int eventID) {
        List<SecurityAssignment> assignments = getAllAssignments();

        for (SecurityAssignment assignment : assignments) {
            if (assignment.getEmployeeID() == employeeID && assignment.getEventID() == eventID) {
                return true;
            }
        }

        return false;
    }

    private static int getReportsTo(int eventID, int employeeID) {
        List<SecurityChainAssignment> list = getAllChainAssignments();

        for (SecurityChainAssignment assignment : list) {
            if (assignment.getEventID() == eventID && assignment.getEmployeeID() == employeeID) {
                return assignment.getReportsToEmployeeID();
            }
        }

        return -1;
    }

    private static int getNextIncidentID() {
        List<SecurityIncident> incidents = getAllIncidents();
        int max = 0;

        for (SecurityIncident incident : incidents) {
            if (incident.getID() > max) {
                max = incident.getID();
            }
        }

        return max + 1;
    }

    private static void saveIncident(SecurityIncident incident) {
        List<SecurityIncident> incidents = getAllIncidents();
        incidents.add(incident);
        writeIncidents(incidents);
    }

    private static void saveAssignment(SecurityAssignment assignment) {
        List<SecurityAssignment> assignments = getAllAssignments();
        assignments.add(assignment);
        writeAssignments(assignments);
    }

    private static void saveChainAssignment(SecurityChainAssignment assignment) {
        List<SecurityChainAssignment> assignments = getAllChainAssignments();
        assignments.add(assignment);
        writeChainAssignments(assignments);
    }

    private static List<SecurityIncident> getAllIncidents() {
        try {
            Path path = Paths.get(INCIDENT_FILE);

            if (!Files.exists(path)) {
                createEmpty(path);
                return new ArrayList<>();
            }

            String json = Files.readString(path).trim();

            if (json.isEmpty()) {
                return new ArrayList<>();
            }

            List<SecurityIncident> incidents = JsonParser.deserializeList(json, SecurityIncident.class);
            return incidents != null ? incidents : new ArrayList<>();

        } catch (IOException e) {
            System.out.println("Error reading incident file.");
            return new ArrayList<>();
        }
    }

    private static List<SecurityAssignment> getAllAssignments() {
        try {
            Path path = Paths.get(ASSIGNMENT_FILE);

            if (!Files.exists(path)) {
                createEmpty(path);
                return new ArrayList<>();
            }

            String json = Files.readString(path).trim();

            if (json.isEmpty()) {
                return new ArrayList<>();
            }

            List<SecurityAssignment> assignments = JsonParser.deserializeList(json, SecurityAssignment.class);
            return assignments != null ? assignments : new ArrayList<>();

        } catch (IOException e) {
            System.out.println("Error reading assignment file.");
            return new ArrayList<>();
        }
    }

    private static List<SecurityChainAssignment> getAllChainAssignments() {
        try {
            Path path = Paths.get(CHAIN_FILE);

            if (!Files.exists(path)) {
                createEmpty(path);
                return new ArrayList<>();
            }

            String json = Files.readString(path).trim();

            if (json.isEmpty()) {
                return new ArrayList<>();
            }

            List<SecurityChainAssignment> assignments = JsonParser.deserializeList(json, SecurityChainAssignment.class);
            return assignments != null ? assignments : new ArrayList<>();

        } catch (IOException e) {
            System.out.println("Error reading chain assignment file.");
            return new ArrayList<>();
        }
    }

    private static void writeIncidents(List<SecurityIncident> incidents) {
        try {
            Path path = Paths.get(INCIDENT_FILE);

            if (!Files.exists(path)) {
                createEmpty(path);
            }

            Files.writeString(path, JsonParser.serialize(incidents));

        } catch (IOException e) {
            System.out.println("Error writing incident file.");
        }
    }

    private static void writeAssignments(List<SecurityAssignment> assignments) {
        try {
            Path path = Paths.get(ASSIGNMENT_FILE);

            if (!Files.exists(path)) {
                createEmpty(path);
            }

            Files.writeString(path, JsonParser.serialize(assignments));

        } catch (IOException e) {
            System.out.println("Error writing assignment file.");
        }
    }

    private static void writeChainAssignments(List<SecurityChainAssignment> assignments) {
        try {
            Path path = Paths.get(CHAIN_FILE);

            if (!Files.exists(path)) {
                createEmpty(path);
            }

            Files.writeString(path, JsonParser.serialize(assignments));

        } catch (IOException e) {
            System.out.println("Error writing chain assignment file.");
        }
    }

    private static void createEmpty(Path path) throws IOException {
        Path parent = path.getParent();

        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        Files.writeString(path, "[]");
    }
}