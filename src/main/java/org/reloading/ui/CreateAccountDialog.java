package org.reloading.ui;

import org.reloading.persons.Account;

import javax.swing.*;
import java.util.Optional;

public class CreateAccountDialog {
    public static Account createAccount() {
        String name = getNameFromUser();
        double initialAmount = getDollarAmountFromUser();

        Account account = new Account(name, initialAmount);

        JOptionPane.showMessageDialog(null, "Account created with UUID: " + account.getPersonUUID(),
                "Account Created", JOptionPane.INFORMATION_MESSAGE);

        return account;
    }

    private static String getNameFromUser() {
        String name = JOptionPane.showInputDialog(null, "Enter account name:", "Create Account", JOptionPane.PLAIN_MESSAGE);

        try {
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Name cannot be empty. Please enter a valid name.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return getNameFromUser();
            }

            return name;
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(null, "Name cannot be empty. Please enter a valid name.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return getNameFromUser();
        }
    }

    private static double getDollarAmountFromUser() {
        String amountStr = JOptionPane.showInputDialog(null, "Enter initial dollar amount:", "Create Account", JOptionPane.PLAIN_MESSAGE);
        amountStr = amountStr.replace("$", "").replace(",", "");

        try {
            return Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input for dollar amount. Please enter a valid number.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return getDollarAmountFromUser();
        }
    }

    public static Optional<Double> convertDollarAmount(String amountStr) {
        amountStr = amountStr.replace("$", "").replace(",", "");

        try {
            return Optional.of(Double.parseDouble(amountStr));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}

