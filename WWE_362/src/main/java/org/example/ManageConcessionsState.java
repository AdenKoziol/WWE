package org.example;

public class ManageConcessionsState implements DepartmentState {
    @Override
    public void showMenu(DepartmentContext context) {
        ManageConcessions.showMenu(context.getScanner());
    }

    @Override
    public String getDepartmentName() {
        return "Manage Concessions";
    }
}