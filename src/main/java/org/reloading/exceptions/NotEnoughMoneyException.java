package org.reloading.exceptions;

public class NotEnoughMoneyException extends RuntimeException {
    public NotEnoughMoneyException(String message) {
        super("Not enough money: " + message);
    }
}
