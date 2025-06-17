package com.dantespard4.neurofuzz.openapi.generator;

import com.dantespard4.neurofuzz.openapi.model.ApiParameter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Genera valores de ejemplo para parámetros de API basados en su tipo y ubicación.
 */
public class ParameterValueGenerator {

    /**
     * Genera valores de ejemplo para parámetros de tipo path.
     *
     * @param parameters Lista de parámetros de API
     * @return Mapa con nombres de parámetros y sus valores de ejemplo
     */
    public Map<String, Object> generatePathParamExamples(List<ApiParameter> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return new HashMap<>();
        }

        return generateParamExamples(parameters, "path");
    }

    /**
     * Genera valores de ejemplo para parámetros de tipo query.
     *
     * @param parameters Lista de parámetros de API
     * @return Mapa con nombres de parámetros y sus valores de ejemplo
     */
    public Map<String, Object> generateQueryParamExamples(List<ApiParameter> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return new HashMap<>();
        }

        return generateParamExamples(parameters, "query");
    }

    /**
     * Genera valores de ejemplo para parámetros de un tipo específico.
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
     * Genera un valor de ejemplo basado en el tipo de esquema y nombre del parámetro.
     */
    private Object generateExampleValue(String paramName, String schemaType) {
        // Primero verifica si el nombre sugiere un tipo específico de dato
        if (paramName.toLowerCase().contains("id")) {
            return schemaType.equals("string") ? UUID.randomUUID().toString() : 12345;
        } else if (paramName.toLowerCase().contains("date")) {
            return LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        } else if (paramName.toLowerCase().contains("email")) {
            return "ejemplo@dominio.com";
        }

        // Si no hay pista en el nombre, usa el tipo de esquema
        return switch (schemaType) {
            case "string" -> "ejemplo";
            case "integer" -> 123;
            case "number" -> 123.45;
            case "boolean" -> true;
            default -> "valor";
        };
    }

    /**
     * Genera valores para todos los tipos de parámetros.
     */
    public Map<String, Object> generateAllParamExamples(List<ApiParameter> parameters) {
        Map<String, Object> allParams = new HashMap<>();
        allParams.putAll(generatePathParamExamples(parameters));
        allParams.putAll(generateQueryParamExamples(parameters));
        // Añadir otros tipos de parámetros según sea necesario
        return allParams;
    }
}