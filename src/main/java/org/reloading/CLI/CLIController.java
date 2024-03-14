package org.reloading.CLI;

import org.reloading.exceptions.AccountAlreadyExistsException;
import org.reloading.persons.Account;
import org.reloading.persons.Accounts;

import java.util.Scanner;

public class CLIController {
    private CLIMenu menu;

    private boolean stopped;
    public CLIController(){
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

        menu.addMenuItem(new CLIMenuItem("Quit", ()->{
            this.stopped = true;
        }));
    }

    public CLIMenu getMenu() {
        return menu;
    }

    void select(CLIView view){

        this.menu.getMenuItems().get(new Scanner(System.in).nextInt()).execute();
    }

    boolean isStopped(){
        return stopped;
    }
}
