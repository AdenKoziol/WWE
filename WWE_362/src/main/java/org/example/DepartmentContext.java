package org.example;

import java.util.Scanner;

public class DepartmentContext {
    private DepartmentState currentState;
    private final Scanner scanner;

    public DepartmentContext(Scanner scanner) {
        this.scanner = scanner;
    }

    public void setState(DepartmentState state) {
        this.currentState = state;
    }

    public void showCurrentDepartment() {
        if (currentState != null) {
            currentState.showMenu(this);
        }
    }

    public Scanner getScanner() {
        return scanner;
    }

    public String getCurrentDepartmentName() {
        return currentState != null ? currentState.getDepartmentName() : "None";
    }
}