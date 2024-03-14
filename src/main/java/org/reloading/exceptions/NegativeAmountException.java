package org.reloading.exceptions;

public class NegativeAmountException extends Exception {
    public NegativeAmountException(String message) {
        super("Negative Amount: " + message);
    }
}
