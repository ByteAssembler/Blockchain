package org.reloading.CLI;

public class CLIView {
    public void display(CLIMenu menu){
        try {
            new ProcessBuilder("clear").inheritIO().start().waitFor();
        } catch (Exception e) {
            System.out.println("Error while clearing the terminal: " + e.getMessage());
        }

        menu.display();
    }
}
