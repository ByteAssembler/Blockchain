package org.reloading.CLI;

public class CircularColors {
    public static final String RESET = "\u001B[0m";
    public static final String[] COLORS = {
            "\u001B[31m",
            "\u001B[32m",
            "\u001B[33m",
            "\u001B[34m",
            "\u001B[35m",
            "\u001B[36m",
            "\u001B[37m"
    };

    private static int currentColorIndex = 0;

    public static void switchToNextColor() {
        String color = COLORS[currentColorIndex];
        currentColorIndex = (currentColorIndex + 1) % COLORS.length;
        System.out.print(color);
    }

    public static void reset() {
        currentColorIndex = 0;
    }

    public static void setBackToNormal() {
        System.out.print(RESET);
    }
}