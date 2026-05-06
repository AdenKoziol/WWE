package org.example;

public class TalentManagementState implements DepartmentState {
    @Override
    public void showMenu(DepartmentContext context) {
        TalentManagement.showMenu(context.getScanner());
    }

    @Override
    public String getDepartmentName() {
        return "Talent Management";
    }
}