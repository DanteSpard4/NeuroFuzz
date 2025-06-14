package com.dantespard4.neurofuzz.core;

import com.dantespard4.neurofuzz.http.HttpExecutor;
import com.dantespard4.neurofuzz.http.HttpResult;
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

    private final HttpExecutor httpExecutor = new HttpExecutor();
    private final ObjectWriter jsonWriter = new ObjectMapper().writer();
    private Set<String> mutationStrategies = new HashSet<>();
    private boolean onlyErrors;

    public void setMutationStrategies(Set<String> mutationStrategies) {
        this.mutationStrategies = mutationStrategies;
    }

    public void setOnlyErrors(boolean onlyErrors) {
        this.onlyErrors = onlyErrors;
    }

    public void fuzzMultiple(String url, File payloadFile, boolean verbose, File saveFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(payloadFile, StandardCharsets.UTF_8));
             BufferedWriter bw = (saveFile != null) ? new BufferedWriter(new FileWriter(saveFile)) : null
        ) {
            String line;
            int count = 1;

            while ((line = br.readLine()) != null) {



                String mutated = Mutator.mutate(line, mutationStrategies);
                if (verbose) {
                    System.out.println("[#] Payload #" + count + ": " + mutated);
                } else {
                    System.out.println("[#] Enviando payload #" + count);
                }

                HttpResult result = httpExecutor.sendPost(url, mutated);

                if (onlyErrors && (result.statusCode() < 400)){
                    count ++;
                    continue;
                }

                String color = colorForStatus(result.statusCode());
                if(bw != null) {
                    Map<String, String> logEntry = new LinkedHashMap<>();
                    logEntry.put("payload", mutated);
                    logEntry.put("statusCode", String.valueOf(result.statusCode()));
                    logEntry.put("responseTimeMs", String.valueOf(result.responseTimeMs()));
                    logEntry.put("responseBody", result.responseBody());

                    bw.write(jsonWriter.writeValueAsString(logEntry));
                    bw.newLine();
                    bw.flush();
                }

                if (verbose) {
                    System.out.println(color + "[✔] Response: " + result.statusCode() + " (" + result.responseTimeMs() + "ms)" + RESET
                            + " - Body: " + truncate(result.responseBody(), 100));
                } else {
                    System.out.println(color + "[✔] " + result.statusCode() + " - " + result.responseTimeMs() + "ms"
                            + " - " + result.responseBody().length() + " bytes" + RESET);
                }
                System.out.println("----");
                count++;
            }
        } catch (IOException e) {
            System.err.println(RED + "[!] Error al leer archivo de payloads: " + e.getMessage() + RESET);
        }
    }

    private String truncate(String s, int maxLen) {
        return s.length() <= maxLen ? s : s.substring(0, maxLen) + "...";
    }
}

