package org.example;

public class BoxOfficeState implements DepartmentState {
    @Override
    public void showMenu(DepartmentContext context) {
        BoxOffice.showMenu(context.getScanner());
    }

    @Override
    public String getDepartmentName() {
        return "Box Office";
    }
}