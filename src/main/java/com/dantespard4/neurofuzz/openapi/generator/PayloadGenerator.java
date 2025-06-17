package com.dantespard4.neurofuzz.openapi.generator;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.parser.OpenAPIV3Parser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Generador de payloads de ejemplo basado en esquemas OpenAPI.
 */
public class PayloadGenerator {

    private final OpenAPI openAPI;

    /**
     * Crea un generador de payloads a partir de una ruta de especificación OpenAPI.
     *
     * @param specPath Ruta al archivo de especificación OpenAPI
     */
    public PayloadGenerator(String specPath) {
        this.openAPI = new OpenAPIV3Parser().read(specPath);
    }


    /**
     * Genera un payload de ejemplo basado en el nombre del esquema.
     *
     * @param schemaName Nombre del esquema en la sección components/schemas
     * @return Mapa con el payload generado o null si no se encuentra el esquema
     */
    public Map<String, Object> generatePayloadFromSchemaName(String schemaName) {
        if (openAPI == null || openAPI.getComponents() == null ||
                openAPI.getComponents().getSchemas() == null) {
            return null;
        }

        Schema<?> schema = openAPI.getComponents().getSchemas().get(schemaName);
        if (schema == null || !"object".equals(schema.getType())) {
            return null;
        }
        return generatePayloadFromSchema(schema);
    }

    /**
     * Genera un payload a partir de un objeto Schema.
     *
     * @param schema Esquema OpenAPI
     * @return Mapa con propiedades y valores generados
     */
    private Map<String, Object> generatePayloadFromSchema(Schema<?> schema) {
        Map<String, Object> payload = new HashMap<>();

        if (schema.getProperties() != null) {
            // Identificar campos requeridos para tratamiento especial
            List<String> requiredFields = schema.getRequired() != null ?
                    schema.getRequired() : Collections.emptyList();

            for (Map.Entry<String, Schema> entry : schema.getProperties().entrySet()) {
                String fieldName = entry.getKey();
                Schema<?> fieldSchema = entry.getValue();
                boolean isRequired = requiredFields.contains(fieldName);

                Object value = generateExampleValue(fieldName, fieldSchema, isRequired);
                payload.put(fieldName, value);
            }
        }

        return payload;
    }

    /**
     * Genera un valor de ejemplo inteligente basado en el nombre del campo,
     * tipo de esquema y si es requerido.
     *
     * @param fieldName Nombre del campo
     * @param schema Esquema del campo
     * @param isRequired Si el campo es requerido
     * @return Valor de ejemplo generado
     */
    private Object generateExampleValue(String fieldName, Schema<?> schema, boolean isRequired) {
        // Manejar referencias a otros esquemas
        if (schema.get$ref() != null) {
            String ref = schema.get$ref();
            String refName = ref.substring(ref.lastIndexOf('/') + 1);
            return generatePayloadFromSchemaName(refName);
        }

        // Usar formato si está disponible
        String format = schema.getFormat();
        if (format != null) {
            return generateValueByFormat(format, fieldName);
        }

        // Usar nombre de campo para generar valores más significativos
        String fieldLower = fieldName.toLowerCase();
        if (fieldLower.contains("id")) {
            return schema.getType().equals("string") ? UUID.randomUUID().toString() : 12345;
        } else if (fieldLower.contains("email")) {
            return "usuario@ejemplo.com";
        } else if (fieldLower.contains("name") || fieldLower.contains("nombre")) {
            return "Nombre Ejemplo";
        } else if (fieldLower.contains("date") || fieldLower.contains("fecha")) {
            return LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        } else if (fieldLower.contains("phone") || fieldLower.contains("telefono")) {
            return "+34612345678";
        }

        // Por defecto, usar el tipo
        String type = schema.getType();
        if (type == null) return "desconocido";

        return switch (type) {
            case "string" -> "valor_ejemplo";
            case "integer" -> 42;
            case "number" -> 42.5;
            case "boolean" -> true;
            case "array" -> {
                Schema<?> items = schema.getItems();
                if (items != null) {
                    yield List.of(generateExampleValue("item", items, isRequired));
                } else {
                    yield Collections.emptyList();
                }
            }
            case "object" -> generatePayloadFromSchema(schema);
            default -> "desconocido";
        };
    }

    /**
     * Genera un valor basado en el formato del campo.
     */
    private Object generateValueByFormat(String format, String fieldName) {
        return switch (format) {
            case "date", "date-time" -> LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            case "email" -> "usuario@ejemplo.com";
            case "uuid" -> UUID.randomUUID().toString();
            case "uri" -> "https://ejemplo.com/recurso";
            case "byte" -> "ZGF0b3MgZW4gYmFzZTY0";  // "datos en base64" en Base64
            case "binary" -> "[datos binarios]";
            case "int32", "int64" -> 42;
            case "float", "double" -> 42.5;
            default -> generatePlaceholderByFieldName(fieldName);
        };
    }

    /**
     * Genera un valor placeholder basado en el nombre del campo.
     */
    private Object generatePlaceholderByFieldName(String fieldName) {
        String fieldLower = fieldName.toLowerCase();
        if (fieldLower.contains("password") || fieldLower.contains("contraseña")) {
            return "********";
        } else if (fieldLower.contains("descripcion") || fieldLower.contains("description")) {
            return "Descripción de ejemplo";
        }
        return "ejemplo";
    }
}