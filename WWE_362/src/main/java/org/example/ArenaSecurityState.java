package org.example;

public class ArenaSecurityState implements DepartmentState {
    @Override
    public void showMenu(DepartmentContext context) {
        ArenaSecurity.showMenu(context.getScanner());
    }

    @Override
    public String getDepartmentName() {
        return "Arena Security";
    }
}