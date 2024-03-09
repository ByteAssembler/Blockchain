package org.reloading.exceptions;

public class BlockInvalidException extends Exception {
    public BlockInvalidException(String message) {
        super("Block invalid: " + message);
    }
}
