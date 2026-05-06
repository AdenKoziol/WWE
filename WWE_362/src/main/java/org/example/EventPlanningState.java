package org.example;

public class EventPlanningState implements DepartmentState {
    @Override
    public void showMenu(DepartmentContext context) {
        EventPlanning.showMenu(context.getScanner());
    }

    @Override
    public String getDepartmentName() {
        return "Event Planning";
    }
}