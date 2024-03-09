package org.reloading;

public interface Print {
    default void print() {
        System.out.println(this);
    }
}
