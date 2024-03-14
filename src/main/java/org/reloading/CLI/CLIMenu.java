package org.reloading.CLI;

import java.util.ArrayList;
import java.util.LinkedList;

public class CLIMenu implements CLIItem {
    private String prompt;
    private ArrayList<CLIMenuItem> menuItems;

    public CLIMenu(String prompt){
        this.prompt = prompt;
        this.menuItems = new ArrayList<>();
    }

    public void addMenuItem(CLIMenuItem item){
        this.menuItems.add(item);
    }
    @Override
    public void display() {
        System.out.println(prompt); // display menu title itself
        CircularColors.reset(); // sets color index back to 0
        menuItems.forEach((CLIMenuItem item)->{
            CircularColors.switchToNextColor();
            item.display();
        }); // display items
        CircularColors.setBackToNormal(); // switches back to normal terminal color
    }

    public ArrayList<CLIMenuItem> getMenuItems() {
        return menuItems;
    }
}
