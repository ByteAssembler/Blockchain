package org.reloading.CLI;

import org.reloading.exceptions.AccountAlreadyExistsException;
import org.reloading.persons.Account;
import org.reloading.persons.Accounts;

import java.io.IOException;
import java.util.Scanner;

public class CLIController {
    private final CLIMenu menu;
    private int selected = 0;
    private boolean stopped;

    public CLIController() {
        stopped = false;

        menu = new CLIMenu("Select an option:");
        menu.addMenuItem(new CLIMenuItem("Create an account", () -> {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the Name: ");
            String name = scanner.next();
            System.out.print("Enter the Startamount: ");
            double amount = scanner.nextDouble();
            try {
                new Account(name, amount); // adds account to accounts
            } catch (AccountAlreadyExistsException e) {
                // TODO
                throw new RuntimeException(e);
            }
        }));

        menu.addMenuItem(new CLIMenuItem("Show Accounts", Accounts::print));

        menu.addMenuItem(new CLIMenuItem("Quit", () -> this.stopped = true));

        menu.getMenuItems().get(0).setSelected(true);
    }

    public CLIMenu getMenu() {
        return menu;
    }

    void select(CLIView view) throws IOException {

        while (true) {
            menu.getMenuItems().get(selected).setSelected(false);

            // Get arrows or enter key pressed
            int input = System.in.read();

            if (input == 10 || input == 13) {
                System.out.println("Selected option " + selected);
                break;
            }

            if (input == 27) { // ANSI escape character
                int next1 = System.in.read();
                int next2 = System.in.read();
                if (next1 == 91) { // '['
                    switch (next2) {
                        case 65: // Up arrow key
                            selected--;
                            break;
                        case 66: // Down arrow key
                            selected++;
                            break;
                    }
                }
            }

            // finished getting keys

            selected %= menu.getMenuItems().size();
            System.out.println("Selected: " + selected);

            menu.getMenuItems().get(selected).setSelected(true);
            view.display(menu);
        }

        menu.getMenuItems().get(selected).execute();
    }

    boolean isStopped() {
        return stopped;
    }
}
