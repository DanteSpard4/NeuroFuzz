package com.dantespard4.neurofuzz.util;

public class AnsiColors {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String GRAY = "\u001B[90m";

    public static String colorForStatus(int statusCode) {
        if (statusCode >= 200 && statusCode < 300) {
            return GREEN;
        } else if (statusCode >= 400 && statusCode < 500) {
            return YELLOW;
        } else if (statusCode >= 500 || statusCode == 0) {
            return RED;
        } else {
            return BLUE;
        }
    }
}
