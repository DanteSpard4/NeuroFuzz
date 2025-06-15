package com.dantespard4.neurofuzz.core;

import java.io.IOException;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import java.util.*;

public class Mutator {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Random RANDOM = new Random();

    public static String mutate(String input, Set<String> strategies) {
        try {
            JsonNode original = MAPPER.readTree(input);
            ObjectNode mutated = original.deepCopy();

            strategies.forEach(strategy -> applyStrategy(mutated, strategy));

            return MAPPER.writeValueAsString(mutated);

        } catch (IOException e) {
            System.err.println("[!] Error processing JSON: " + e.getMessage());
            return input; // fallback
        }
    }

    private static void applyStrategy(ObjectNode node, String strategy) {
        switch (strategy) {
            case "replace-char" -> mutateTextByReplacingCharacters(node);
            case "delete-key" -> removeRandomKey(node);
            case "insert-junk" -> addJunkField(node);
            case "repeat-key" -> simulateDuplicateKey(node);
            case "empty-value" -> setRandomFieldToEmptyValue(node);
            default -> System.err.println("[!] Unknown strategy: " + strategy);
        }
    }

    private static void mutateTextByReplacingCharacters(ObjectNode node) {
        node.fieldNames().forEachRemaining(field -> {
            JsonNode value = node.get(field);
            if (value.isTextual()) {
                String mutated = value.asText().
                        replace("a", "@").
                        replace("e", "3");
                node.put(field, mutated);
            }
        });
    }

    private static void removeRandomKey(ObjectNode node) {
        List<String> keys = collectFieldNames(node);
        if (!keys.isEmpty()) {
            node.remove(keys.get(RANDOM.nextInt(keys.size())));
        }
    }

    private static void addJunkField(ObjectNode node) {
        node.put("$$junk" + RANDOM.nextInt(1000), "???");
    }

    private static void simulateDuplicateKey(ObjectNode node) {
        // Duplicate keys aren't allowed in JSON structure
        List<String> keys = collectFieldNames(node);
        if (!keys.isEmpty()) {
            String key = getRandomElement(keys);
            node.put(key + "_copy", "duplicated_value");
        }
    }

    private static void setRandomFieldToEmptyValue(ObjectNode node) {
        List<String> fields = collectFieldNames(node);
        if (!fields.isEmpty()) {
            String field = getRandomElement(fields);
            JsonNode value = node.get(field);
            if (value.isTextual()) node.put(field, "");
            else if (value.isNumber()) node.put(field, 0);
            else if (value.isBoolean()) node.put(field, false);
        }
    }

    private static List<String> collectFieldNames(ObjectNode node) {
        List<String> names = new ArrayList<>();
        node.fieldNames().forEachRemaining(names::add);
        return names;
    }

    private static <T> T getRandomElement(List<T> list) {
        return list.get(RANDOM.nextInt(list.size()));
    }

}

