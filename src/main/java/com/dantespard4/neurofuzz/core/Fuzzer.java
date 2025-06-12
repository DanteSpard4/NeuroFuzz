package com.dantespard4.neurofuzz.core;

import com.dantespard4.neurofuzz.http.HttpExecutor;
import com.dantespard4.neurofuzz.http.HttpResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Fuzzer {

    private final PayloadMutator mutator = new PayloadMutator();
    private final HttpExecutor httpExecutor = new HttpExecutor();

    public void fuzz(String url) {
        String originalPayload = "{\"username\": \"user\", \"password\": \"pass\"}";
        String mutatedPayload = mutator.mutateJson(originalPayload);

        httpExecutor.sendPost(url, mutatedPayload);
    }

    public void fuzzMultiple(String url, File payloadFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(payloadFile))) {
            String line;
            int count = 1;
            while ((line = br.readLine()) != null) {
                String mutated = mutator.mutateJson(line);
                System.out.println("[#] Payload #" + count + ": " + mutated);

                HttpResult result = httpExecutor.sendPost(url, mutated);
                System.out.println(result.toString());
                System.out.println("----");

                count++;
            }
        } catch (IOException e) {
            System.err.println("[!] Error al leer archivo de payloads: " + e.getMessage());
        }
    }
}

