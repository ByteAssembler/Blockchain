package org.reloading.utils;

public interface Printable {
    default void print() {
        System.out.println(this);
    }
}
