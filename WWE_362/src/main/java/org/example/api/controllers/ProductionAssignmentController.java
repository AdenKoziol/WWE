package org.example.api.controllers;

import org.example.models.Event;
import org.example.models.ProductionAssignment;
import org.example.models.ProductionCrew;
import java.util.List;

public class ProductionAssignmentController extends AbstractController<ProductionAssignment> {

    private static final String ASSIGNMENT_FILE = "src/main/java/org/example/database/ProductionAssignment.json";

    private static final String[] VALID_ROLES = {
            "Lighting", "Audio", "Camera", "Stage", "Pyro"
    };

    private static final String[] VALID_STATUSES = {
            "Scheduled", "In Progress", "Completed", "Cancelled"
    };

    private static final String[] REQUIRED_ROLES = {
            "Lighting", "Audio", "Camera", "Stage"
    };

    @Override
    protected String getFilePath() {
        return ASSIGNMENT_FILE;
    }

    @Override
    protected Class<ProductionAssignment> getType() {
        return ProductionAssignment.class;
    }

    @Override
    protected int getID(ProductionAssignment assignment) {
        return assignment.getID();
    }

    public static void saveAssignment(ProductionAssignment assignment) {
        ProductionAssignmentController controller = new ProductionAssignmentController();
        List<ProductionAssignment> assignments = controller.getAll();
        assignments.add(assignment);
        controller.writeAll(assignments);
    }

    public static int getNextID() {
        ProductionAssignmentController controller = new ProductionAssignmentController();
        return controller.getNextID(controller.getAll());
    }

    public static List<ProductionAssignment> getAllAssignments() {
        ProductionAssignmentController controller = new ProductionAssignmentController();
        return controller.getAll();
    }

    public static ProductionAssignment createAssignment(int crewID, int eventID, String role, String status) {

        ProductionCrewController crewController = new ProductionCrewController();
        ProductionCrew crew = crewController.getCrewByID(crewID);

        EventController eventController = new EventController();
        Event event = eventController.getEventByID(eventID);

        if (crew == null || event == null) {
            return null;
        }

        if (!isValidRole(role)) {
            return null;
        }

        if (!isValidStatus(status)) {
            return null;
        }

        ProductionAssignment assignment = new ProductionAssignment(
                getNextID(),
                crew.getID(),
                crew.getCrewName(),
                event.getID(),
                event.getName(),
                role,
                status
        );

        if (assignment.hasMissingInfo()) {
            return null;
        }

        return assignment;
    }

    public static void displayAssignmentsForCrew(int crewID) {
        List<ProductionAssignment> assignments = getAllAssignments();
        boolean found = false;

        for (ProductionAssignment assignment : assignments) {
            if (assignment.getCrewID() == crewID) {
                System.out.println(assignment);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No assignments found for this crew.");
        }
    }

    private static boolean isValidRole(String role) {
        for (String r : VALID_ROLES) {
            if (r.equalsIgnoreCase(role)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isValidStatus(String status) {
        for (String s : VALID_STATUSES) {
            if (s.equalsIgnoreCase(status)) {
                return true;
            }
        }
        return false;
    }

    public static boolean roleAlreadyAssigned(int eventID, String role) {
        List<ProductionAssignment> assignments = getAllAssignments();

        for (ProductionAssignment assignment : assignments) {
            if (assignment.getEventID() == eventID &&
                    assignment.getRole().equalsIgnoreCase(role)) {
                return true;
            }
        }
        return false;
    }

    public static boolean crewHasActiveAssignment(int crewID, int eventID) {
        List<ProductionAssignment> assignments = getAllAssignments();

        for (ProductionAssignment assignment : assignments) {
            if (assignment.getCrewID() == crewID &&
                    assignment.getEventID() == eventID &&
                    assignment.getStatus().equalsIgnoreCase("Scheduled")) {
                return true;
            }
        }
        return false;
    }

    public static void displayMissingRoles(int eventID) {
        List<ProductionAssignment> assignments = getAllAssignments();

        boolean[] found = new boolean[REQUIRED_ROLES.length];

        for (ProductionAssignment assignment : assignments) {
            if (assignment.getEventID() == eventID) {
                for (int i = 0; i < REQUIRED_ROLES.length; i++) {
                    if (REQUIRED_ROLES[i].equalsIgnoreCase(assignment.getRole())) {
                        found[i] = true;
                    }
                }
            }
        }

        System.out.println("Missing required roles:");

        boolean anyMissing = false;

        for (int i = 0; i < REQUIRED_ROLES.length; i++) {
            if (!found[i]) {
                System.out.println("- " + REQUIRED_ROLES[i]);
                anyMissing = true;
            }
        }

        if (!anyMissing) {
            System.out.println("None. All required roles are assigned.");
        }
    }

    public static boolean deleteAssignmentByID(int id) {
        ProductionAssignmentController controller = new ProductionAssignmentController();
        List<ProductionAssignment> assignments = controller.getAll();

        for (int i = 0; i < assignments.size(); i++) {
            ProductionAssignment assignment = assignments.get(i);

            if (assignment.getID() == id) {
                assignments.remove(i);
                controller.writeAll(assignments);
                return true;
            }
        }

        return false;
    }

    public static boolean updateAssignment(int id, String newRole, String newStatus) {
        ProductionAssignmentController controller = new ProductionAssignmentController();
        List<ProductionAssignment> assignments = controller.getAll();

        for (ProductionAssignment assignment : assignments) {
            if (assignment.getID() == id) {

                if (!isValidRole(newRole) || !isValidStatus(newStatus)) {
                    return false;
                }

                if (roleAlreadyAssigned(assignment.getEventID(), newRole) &&
                        !assignment.getRole().equalsIgnoreCase(newRole)) {
                    return false;
                }

                assignment.setRole(newRole);
                assignment.setStatus(newStatus);
                controller.writeAll(assignments);
                return true;
            }
        }

        return false;
    }

    public static void displayAssignmentsForEvent(int eventID) {
        List<ProductionAssignment> assignments = getAllAssignments();
        boolean found = false;

        for (ProductionAssignment assignment : assignments) {
            if (assignment.getEventID() == eventID) {
                System.out.println(assignment);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No assignments found for this event.");
        }

        displayMissingRoles(eventID);
    }
}