package com.dantespard4.neurofuzz.cli;


import com.dantespard4.neurofuzz.core.Fuzzer;
import picocli.CommandLine.*;
import picocli.CommandLine.Model.CommandSpec;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

@Command(name = "neurofuzz", mixinStandardHelpOptions = true, version = "NeuroFuzz 0.1.0",
        description = "Basic fuzzer for HTTP APIs")
public class FuzzCommand implements Callable<Integer> {

    @Option(names = {"-u", "--url"}, required = true, description = "Endpoint URL to test")
    private String url;

    @Option(names = {"-p", "--payloads"}, required = true, description = "Payloads .jsonl file")
    private File payloadsFile;

    @Option(names = {"-v", "--verbose"}, description = "Verbose mode, shows more information during execution")
    private boolean verbose;

    @Option(
            names = {"-s", "--save"},
            description = "Optional path to .jsonl file to save results. If omitted, a default filename will be generated.",
            arity = "0..1",
            paramLabel = "FILE"
    )
    private File saveFile;

    @Option(names = {"-m","--mutations"}, description = "Mutation types (comma separated: replace-char,delete-key,insert-junk,repeat-key,empty-value)")
    private String mutationStrategies;

    @Option(names = {"-e", "--only-errors"}, description = "Show only error responses (4xx and 5xx)")
    private boolean onlyErrors;

    @Option(names = {"-t", "--timeout"}, description = "Maximum wait time for each request (in seconds)", defaultValue = "10")
    private int timeoutSeconds;


    @Spec
    private CommandSpec spec;

    @Override
    public Integer call() {

        Fuzzer fuzzer = configureFuzzer();
        File outputFile = resolveOutputFile();
        displayStartupInfo(outputFile);
        fuzzer.fuzzMultiple(url, payloadsFile, verbose,outputFile);
        return 0;
    }

    private Fuzzer configureFuzzer() {
        Fuzzer fuzzer = new Fuzzer(timeoutSeconds);
        fuzzer.setOnlyErrors(onlyErrors);
        fuzzer.setMutationStrategies(parseMutationStrategies());
        return fuzzer;
    }

    private File resolveOutputFile() {
        if (saveFile != null) {
            Object value = spec.optionsMap().get("--save").getValue();
            if (value != null && value.toString().isEmpty()) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
                return new File("fuzz-results-" + timestamp + ".jsonl");
            }
        }
        return saveFile;
    }

    private void displayStartupInfo(File outputFile) {
        System.out.println("[*] Running fuzzer against: " + url);

        if (outputFile != null) {
            System.out.println("[i] Results will be saved in: " + outputFile.getName());
        }

        Set<String> strategies = parseMutationStrategies();
        if (!strategies.isEmpty()) {
            System.out.println("[*] Mutation strategies: " + strategies);
        } else {
            System.out.println("[*] No mutation strategies specified, using defaults.");
        }
    }

    private Set<String> parseMutationStrategies() {
        if (mutationStrategies != null && !mutationStrategies.isEmpty()) {
            return new HashSet<>(Arrays.stream(mutationStrategies.split(","))
                    .map(String::trim)
                    .toList());
        }
        return Set.of("replace-char"); // default strategy
    }
}
