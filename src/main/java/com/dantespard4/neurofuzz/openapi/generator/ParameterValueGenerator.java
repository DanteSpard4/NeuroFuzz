package com.dantespard4.neurofuzz.openapi.generator;

import com.dantespard4.neurofuzz.openapi.model.ApiParameter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Generates example values for API parameters based on their type and location.
 */
public class ParameterValueGenerator {

    /**
     * Generates example values for path parameters.
     *
     * @param parameters List of API parameters
     * @return Map with parameter names and their example values
     */
    public Map<String, Object> generatePathParamExamples(List<ApiParameter> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return new HashMap<>();
        }

        return generateParamExamples(parameters, "path");
    }

    /**
     * Generates example values for query parameters.
     *
     * @param parameters List of API parameters
     * @return Map with parameter names and their example values
     */
    public Map<String, Object> generateQueryParamExamples(List<ApiParameter> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return new HashMap<>();
        }

        return generateParamExamples(parameters, "query");
    }

    /**
     * Generates example values for parameters of a specific type.
     */
    private Map<String, Object> generateParamExamples(List<ApiParameter> parameters, String location) {
        Map<String, Object> paramValues = new HashMap<>();

        for (ApiParameter param : parameters) {
            if (location.equals(param.location())) {
                String name = param.name();
                Object value = generateExampleValue(param.name(), param.schemaType());
                paramValues.put(name, value);
            }
        }

        return paramValues;
    }

    /**
     * Generates an example value based on the schema type and parameter name.
     */
    private Object generateExampleValue(String paramName, String schemaType) {
        // First check if the name suggests a specific data type
        if (paramName.toLowerCase().contains("id")) {
            return schemaType.equals("string") ? UUID.randomUUID().toString() : 12345;
        } else if (paramName.toLowerCase().contains("date")) {
            return LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        } else if (paramName.toLowerCase().contains("email")) {
            return "ejemplo@dominio.com";
        }

        // If there's no hint in the name, use the schema type
        return switch (schemaType) {
            case "string" -> "ejemplo";
            case "integer" -> 123;
            case "number" -> 123.45;
            case "boolean" -> true;
            default -> "valor";
        };
    }

    /**
     * Generates values for all parameter types.
     */
    public Map<String, Object> generateAllParamExamples(List<ApiParameter> parameters) {
        Map<String, Object> allParams = new HashMap<>();
        allParams.putAll(generatePathParamExamples(parameters));
        allParams.putAll(generateQueryParamExamples(parameters));
        // Add other parameter types as needed
        return allParams;
    }
}