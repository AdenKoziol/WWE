package org.example;

public class CoordinateTravelState implements DepartmentState {
    @Override
    public void showMenu(DepartmentContext context) {
        CoordinateTravel.showMenu(context.getScanner());
    }

    @Override
    public String getDepartmentName() {
        return "Coordinate Travel";
    }
}