package org.example;

public class MatchCardCreatingState implements DepartmentState {
    @Override
    public void showMenu(DepartmentContext context) {
        MatchCardCreating.showMenu(context.getScanner());
    }

    @Override
    public String getDepartmentName() {
        return "Arena Security";
    }
}