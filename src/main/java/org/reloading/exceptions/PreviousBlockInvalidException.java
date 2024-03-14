package org.reloading.exceptions;

public class PreviousBlockInvalidException extends Exception {
    public PreviousBlockInvalidException(String message) {
        super("Previous block is invalid: " + message);
    }
}
