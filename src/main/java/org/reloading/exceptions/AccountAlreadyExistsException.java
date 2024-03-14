package org.reloading.exceptions;

public class AccountAlreadyExistsException extends Exception {
    public AccountAlreadyExistsException(String message) {
        super("Account already exists: " + message);
    }
}
