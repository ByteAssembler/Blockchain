package org.reloading.CLI;

import org.reloading.exceptions.AccountAlreadyExistsException;
import org.reloading.persons.Account;
import org.reloading.persons.Accounts;

import java.util.Scanner;

public class CLIController {
    private final CLIMenu menu;
    private boolean stopped;

    public CLIController() {
        stopped = false;

        menu = new CLIMenu("Select an option:");

        menu.addMenuItem("Create an account", () -> {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the name: ");
            String name = scanner.next();
            System.out.print("Enter the start amount: ");
            double amount = scanner.nextDouble();
            try {
                new Account(name, amount); // adds account to accounts
            } catch (AccountAlreadyExistsException e) {
                // TODO
                throw new RuntimeException(e);
            }
        });

        menu.addMenuItem("Show Accounts", Accounts::print);

        menu.addMenuItem("Quit", () -> this.stopped = true);
    }

    void select(CLIView view) {
        this.menu.getMenuItems().get(new Scanner(System.in).nextInt()).execute();
    }

    public CLIMenu getMenu() {
        return menu;
    }

    boolean isStopped() {
        return stopped;
    }
}
