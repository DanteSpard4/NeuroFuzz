package com.dantespard4.neurofuzz.core;

import java.util.Random;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import java.util.*;

public class Mutator {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Random random = new Random();

    public static String mutate(String input, Set<String> strategies) {
        try {
            JsonNode original = mapper.readTree(input);
            ObjectNode mutated = original.deepCopy();

            if (strategies.contains("replace-char")) {
                replaceChars(mutated);
            }
            if (strategies.contains("delete-key")) {
                deleteRandomKey(mutated);
            }
            if (strategies.contains("insert-junk")) {
                insertJunk(mutated);
            }
            if (strategies.contains("repeat-key")) {
                // JSON doesn't support repeated keys; we simulate by merging keys
                insertDuplicateKey(mutated);
            }
            if (strategies.contains("empty-value")) {
                emptyValues(mutated);
            }

            return mapper.writeValueAsString(mutated);

        } catch (Exception e) {
            return input; // fallback
        }
    }

    private static void replaceChars(ObjectNode node) {
        node.fieldNames().forEachRemaining(field -> {
            JsonNode value = node.get(field);
            if (value.isTextual()) {
                String mutated = value.asText().replace("a", "@").replace("e", "3");
                node.put(field, mutated);
            }
        });
    }

    private static void deleteRandomKey(ObjectNode node) {
        List<String> keys = new ArrayList<>();
        node.fieldNames().forEachRemaining(keys::add);
        if (!keys.isEmpty()) {
            String key = keys.get(random.nextInt(keys.size()));
            node.remove(key);
        }
    }

    private static void insertJunk(ObjectNode node) {
        node.put("$$junk" + random.nextInt(1000), "???");
    }

    private static void insertDuplicateKey(ObjectNode node) {
        // Duplicate keys aren't allowed in JSON structure
        // Instead, we re-add an existing key with a new value
        List<String> keys = new ArrayList<>();
        node.fieldNames().forEachRemaining(keys::add);
        if (!keys.isEmpty()) {
            String key = keys.get(random.nextInt(keys.size()));
            node.put(key + "_copy", "duplicated_value");
        }
    }

    private static void emptyValues(ObjectNode node) {
        List<String> fields = new ArrayList<>();
        node.fieldNames().forEachRemaining(fields::add);
        if (!fields.isEmpty()) {
            String field = fields.get(random.nextInt(fields.size()));
            JsonNode value = node.get(field);
            if (value.isTextual()) node.put(field, "");
            else if (value.isNumber()) node.put(field, 0);
            else if (value.isBoolean()) node.put(field, false);
        }
    }

}

