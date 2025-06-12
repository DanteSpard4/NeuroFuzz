package com.dantespard4.neurofuzz;


import com.dantespard4.neurofuzz.cli.FuzzCommand;
import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new FuzzCommand()).execute(args);
        System.exit(exitCode);
    }
}