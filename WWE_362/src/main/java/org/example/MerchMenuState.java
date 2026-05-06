package org.example;

public class MerchMenuState implements DepartmentState {
    @Override
    public void showMenu(DepartmentContext context) {
        MerchMenu.showMenu(context.getScanner());
    }

    @Override
    public String getDepartmentName() {
        return "Merchandising";
    }
}