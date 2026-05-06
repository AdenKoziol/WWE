package org.example;

public class HealthAndSafetyState implements DepartmentState {
    @Override
    public void showMenu(DepartmentContext context) {
        HealthAndSafety.showMenu(context.getScanner());
    }

    @Override
    public String getDepartmentName() {
        return "Health and Safety";
    }
}