package org.reloading.CLI;

public class CLIView {
    public void display(CLIMenu menu){
        try {
            new ProcessBuilder("clear").inheritIO().start().waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }

        menu.display();
    }
}
