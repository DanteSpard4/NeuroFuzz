package com.dantespard4.neurofuzz.openapi.model;

public class ApiParameter {
    public String name;
    public String in; // e.g., "query", "header", "path", "cookie"
    public String schemaType; // e.g., "string", "integer", "boolean"

    public ApiParameter(String name, String in, String schemaType) {
        this.name = name;
        this.in = in;
        this.schemaType = schemaType;

    }
}
