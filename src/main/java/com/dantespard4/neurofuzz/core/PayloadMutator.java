package com.dantespard4.neurofuzz.core;

import java.util.Random;

public class PayloadMutator {

    private final Random random = new Random();

    public String mutateJson(String inputJson) {
        // Mutación simple: altera un valor
        return inputJson.replace("user", "u$er_" + random.nextInt(1000));
    }
}
