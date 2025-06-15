package com.dantespard4.neurofuzz.core;

import com.dantespard4.neurofuzz.http.HttpExecutor;
import com.dantespard4.neurofuzz.http.HttpResult;
import com.dantespard4.neurofuzz.util.FuzzingStats;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static com.dantespard4.neurofuzz.util.AnsiColors.*;

public class Fuzzer {


    private static final ObjectWriter JSON_WRITER = new ObjectMapper().writer();
    private Set<String> mutationStrategies = new HashSet<>();

    private boolean onlyErrors;
    private final HttpExecutor httpExecutor;

    private final FuzzingStats stats = new FuzzingStats();

    public Fuzzer(int timeoutSeconds) {
        this.httpExecutor = new HttpExecutor(timeoutSeconds);
    }

    public void setMutationStrategies(Set<String> mutationStrategies) {
        this.mutationStrategies = mutationStrategies;
    }

    public void setOnlyErrors(boolean onlyErrors) {
        this.onlyErrors = onlyErrors;
    }

    public void fuzzMultiple(String url, File payloadFile, boolean verbose, File saveFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(payloadFile, StandardCharsets.UTF_8));
             BufferedWriter bw = (saveFile != null) ? new BufferedWriter(new FileWriter(saveFile)) : null) {

            if (saveFile != null) stats.setOutputFileName(saveFile.getName());
            processPayloadsFile(url, br, bw, verbose);

        } catch (IOException e) {
            System.err.println(RED + "[!] Error reading payload file: " + e.getMessage() + RESET);
        }

        printStatistics();
    }

    private void processPayloadsFile(String url, BufferedReader br, BufferedWriter bw, boolean verbose) throws IOException {
        String line;
        int count = 1;

        while ((line = br.readLine()) != null) {
            String mutated = Mutator.mutate(line, mutationStrategies);
            printPayloadInfo(mutated, count, verbose);

            HttpResult result = httpExecutor.sendPost(url, mutated);
            stats.recordStatus(result.statusCode());

            if (shouldSkipResult(result.statusCode())) {
                count++;
                continue;
            }

            saveResult(bw, mutated, result);
            printResult(result, verbose);

            System.out.println("----");
            count++;
        }
    }

    private boolean shouldSkipResult(int statusCode) {
        return onlyErrors && statusCode < 400;
    }

    private void printPayloadInfo(String payload, int count, boolean verbose) {
        if (verbose) {
            System.out.println("[#] Payload #" + count + ": " + payload);
        } else {
            System.out.println("[#] Sending payload #" + count);
        }
    }

    private void saveResult(BufferedWriter bw, String payload, HttpResult result) throws IOException {
        if (bw == null) return;

        Map<String, String> logEntry = new LinkedHashMap<>();
        logEntry.put("payload", payload);
        logEntry.put("statusCode", String.valueOf(result.statusCode()));
        logEntry.put("responseTimeMs", String.valueOf(result.responseTimeMs()));
        logEntry.put("responseBody", result.responseBody());

        bw.write(JSON_WRITER.writeValueAsString(logEntry));
        bw.newLine();
        bw.flush();
    }

    private void printResult(HttpResult result, boolean verbose) {
        String color = colorForStatus(result.statusCode());

        if (verbose) {
            System.out.println(color + "[✔] Response: " + result.statusCode() +
                    " (" + result.responseTimeMs() + "ms)" + RESET +
                    " - Body: " + truncate(result.responseBody(), 100));
        } else {
            System.out.println(color + "[✔] " + result.statusCode() + " - " +
                    result.responseTimeMs() + "ms" + " - " +
                    result.responseBody().length() + " bytes" + RESET);
        }
    }

    private void printStatistics() {
        System.out.println("\n--- NeuroFuzz Report ---");
        System.out.printf("Total payloads processed: %d\n", stats.getTotalCount());
        System.out.printf("2xx: %d | 3xx: %d | 4xx: %d | 5xx: %d | Timeouts: %d\n",
                stats.getCount2xx(), stats.getCount3xx(), stats.getCount4xx(), stats.getCount5xx(), stats.getCountTimeouts());
        if (stats.getOutputFileName() != null) {
            System.out.println("Results saved in: " + stats.getOutputFileName());
        }
    }

    private String truncate(String s, int maxLen) {
        return s.length() <= maxLen ? s : s.substring(0, maxLen) + "...";
    }
}
