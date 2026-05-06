package org.example;

public class CostumeDesigningState implements DepartmentState {
    @Override
    public void showMenu(DepartmentContext context) {
        CostumeDesigning.showmenu(context.getScanner());
    }

    @Override
    public String getDepartmentName() {
        return "CostumeDesigning";
    }
}