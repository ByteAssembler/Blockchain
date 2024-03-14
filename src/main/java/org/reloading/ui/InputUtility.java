package org.reloading.ui;

import org.reloading.blockchain.Transaction;
import org.reloading.exceptions.InvalidTransactionException;
import org.reloading.exceptions.NegativeAmountException;
import org.reloading.persons.Account;
import org.reloading.persons.Accounts;
import org.reloading.persons.Person;

import javax.swing.*;
import java.util.Optional;

public class InputUtility {
    public static Optional<Transaction> createTransactionDialog() {
        if (Accounts.getAccount().size() < 2) {
            JOptionPane.showMessageDialog(null,
                    "At least two accounts are required to create a transaction. " +
                            "Please create an account first.", "Error", JOptionPane.ERROR_MESSAGE);
            return Optional.empty();
        }

        Account sender = InputUtility.getPersonDialog(Person.Type.SENDER);
        Account receiver = InputUtility.getPersonDialog(Person.Type.RECEIVER);

        // check if sender and receiver are the same
        if (sender.getPersonName().equalsIgnoreCase(receiver.getPersonName())) {
            JOptionPane.showMessageDialog(null,
                    "Sender and receiver cannot be the same. Please enter different names.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return createTransactionDialog();
        }

        double amount = getDollarAmountFromUser();

        if (amount <= 0) {
            JOptionPane.showMessageDialog(null,
                    "Amount must be greater than 0. Please enter a valid amount.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return createTransactionDialog();
        }

        try {
            return Optional.of(new Transaction(sender, receiver, amount));
        } catch (NegativeAmountException | InvalidTransactionException e) {
            return Optional.empty();
        }
    }

    public static Account getPersonDialog(Person.Type personType) {
        if (Accounts.getAccount().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "No accounts exist. Please create an account first.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        String name = JOptionPane.showInputDialog(null,
                "Enter " + personType + " name:", "Create " + personType, JOptionPane.PLAIN_MESSAGE);

        try {
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Name cannot be empty. Please enter a valid name.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return getPersonDialog(personType);
            }

            Optional<Account> accountOption = Accounts.getAccountBySimilarPersonName(name);
            if (accountOption.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Account with name " + name + " does not exist. Please enter a valid name.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return getPersonDialog(personType);
            }

            return accountOption.get();
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(null,
                    "Name cannot be empty. Please enter a valid name.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return getPersonDialog(personType);
        }
    }

    public static double getDollarAmountFromUser() {
        String amountStr = JOptionPane.showInputDialog(null,
                "Enter initial dollar amount:", "Create Account", JOptionPane.PLAIN_MESSAGE);
        amountStr = amountStr.replace("$", "").replace(",", "");

        try {
            double val = Double.parseDouble(amountStr);
            if (val <= 0) {
                JOptionPane.showMessageDialog(null,
                        "Amount must be greater than 0. Please enter a valid amount.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return getDollarAmountFromUser();
            }
            return val;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null,
                    "Invalid input for dollar amount. Please enter a valid number.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return getDollarAmountFromUser();
        }
    }
}
