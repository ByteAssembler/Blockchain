package org.reloading.exceptions;

public class NotEnoughMoneyException extends Exception {
    public NotEnoughMoneyException(String message) {
        super("Not enough money: " + message);
    }
}
