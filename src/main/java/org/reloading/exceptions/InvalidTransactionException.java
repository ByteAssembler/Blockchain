package org.reloading.exceptions;

public class InvalidTransactionException extends Exception {
    public InvalidTransactionException(String message) {
        super("Invalid Transaction: " + message);
    }
}
