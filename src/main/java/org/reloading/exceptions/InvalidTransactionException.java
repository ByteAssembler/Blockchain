package org.reloading.exceptions;

public class InvalidTransactionException extends IllegalArgumentException {
    public InvalidTransactionException(String message) {
        super("Invalid Transaction: " + message);
    }
}
