package org.example;

public class ManageBroadcastingState implements DepartmentState {
    @Override
    public void showMenu(DepartmentContext context) {
        ManageBroadcasting.showMenu(context.getScanner());
    }

    @Override
    public String getDepartmentName() {
        return "Manage Broadcasting";
    }
}