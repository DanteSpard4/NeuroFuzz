package com.dantespard4.neurofuzz.openapi.model;

import java.util.List;

public class ApiEndpoint {
    public String path;
    public String method;
    public List<ApiParameter> parameters;
    public String requestBodySchema;

    public ApiEndpoint(String path, String method, List<ApiParameter> parameters, String requestBodySchema) {
        this.path = path;
        this.method = method;
        this.parameters = parameters;
        this.requestBodySchema = requestBodySchema;
    }
}
