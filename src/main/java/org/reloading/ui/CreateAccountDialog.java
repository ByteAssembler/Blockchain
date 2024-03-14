package org.reloading.ui;

import org.reloading.exceptions.AccountAlreadyExistsException;
import org.reloading.persons.Account;
import org.reloading.persons.Accounts;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.Optional;

public class CreateAccountDialog {
    public static Account createAccount() {
        String name = getNameFromUser();
        double initialAmount = InputUtility.getDollarAmountFromUser();

        if (Accounts.checkIfAccountWithPersonNameExists(name)) {
            JOptionPane.showMessageDialog(null,
                    "Account with name (similar) " + name + " already exists.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return createAccount();
        }

        Account account;
        try {
            account = new Account(name, initialAmount);
        } catch (AccountAlreadyExistsException e) {
            // This should never happen!
            JOptionPane.showMessageDialog(null, e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(e);
        }

        JOptionPane.showMessageDialog(null,
                "Account created with UUID: " + account.getPersonUUID(),
                "Account Created", JOptionPane.INFORMATION_MESSAGE);

        return account;
    }

    private static String getNameFromUser() {
        String name = JOptionPane.showInputDialog(null,
                "Enter account name:", "Create Account", JOptionPane.PLAIN_MESSAGE);

        try {
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Name cannot be empty. Please enter a valid name.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return getNameFromUser();
            }

            return name;
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(null,
                    "Name cannot be empty. Please enter a valid name.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return getNameFromUser();
        }
    }

    public static Optional<BigDecimal> convertDollarAmount(String amountStr) {
        amountStr = amountStr.replace("$", "").replace(",", "");

        try {
            var dub = Double.parseDouble(amountStr);
            var big = BigDecimal.valueOf(dub);
            return Optional.of(big);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}

