package org.reloading.CLI;

public class CLIView {
    public void display(CLIMenu menu) {
        try {
            // clear the console, do not use a command
            System.out.print("\033[H\033[2J");
        } catch (Exception e) {
            throw new RuntimeException("Error clearing the console", e);
        }

        menu.display();
    }
}
