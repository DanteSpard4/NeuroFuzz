package com.dantespard4.neurofuzz.util;

public class AnsiColors {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String GRAY = "\u001B[90m";

    public static String colorForStatus(int statusCode) {
        return switch (statusCode / 100) {
            case 2 -> GREEN;
            case 3 -> BLUE;
            case 4 -> YELLOW;
            case 5, 0 -> RED;
            default -> GRAY;
        };
    }
}
