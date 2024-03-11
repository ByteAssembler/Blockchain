package org.reloading.exceptions;

public class NegativeAmountException extends IllegalArgumentException {
    public NegativeAmountException(String message) {
        super("Negative Amount: " + message);
    }
}
