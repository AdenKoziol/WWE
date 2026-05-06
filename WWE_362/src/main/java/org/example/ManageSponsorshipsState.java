package org.example;

public class ManageSponsorshipsState implements DepartmentState {
    @Override
    public void showMenu(DepartmentContext context) {
        ManageSponsorships.showMenu(context.getScanner());
    }

    @Override
    public String getDepartmentName() {
        return "Manage Sponsorships";
    }
}