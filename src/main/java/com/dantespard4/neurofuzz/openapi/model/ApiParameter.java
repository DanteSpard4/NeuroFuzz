package com.dantespard4.neurofuzz.openapi.model;

public record ApiParameter (
        String name,
        String location, // e.g., "query", "header", "path", "cookie"
        String schemaType, // e.g., "string", "integer", "boolean", etc.
        boolean required // Indicates if the parameter is required or optional
) {
}
