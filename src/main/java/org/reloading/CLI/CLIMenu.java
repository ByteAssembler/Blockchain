package org.reloading.CLI;

import java.util.ArrayList;

public class CLIMenu implements CLIItem {
    private final String prompt;
    private final ArrayList<CLIMenuItem> menuItems;

    public CLIMenu(String prompt) {
        this.prompt = prompt;
        this.menuItems = new ArrayList<>();
    }

    public void addMenuItem(CLIMenuItem item) {
        this.menuItems.add(item);
    }

    public void addMenuItem(String option, Runnable exe) {
        addMenuItem(new CLIMenuItem(option, exe));
    }

    @Override
    public void display() {
        System.out.println();
        System.out.println();

        System.out.println(prompt); // display menu title itself
        CircularColors.reset(); // sets color index back to 0

        menuItems.forEach((CLIMenuItem item) -> {
            CircularColors.switchToNextColor();
            item.display();
        }); // display items

        CircularColors.setBackToNormal(); // switches back to normal terminal color
    }

    public ArrayList<CLIMenuItem> getMenuItems() {
        return menuItems;
    }
}
