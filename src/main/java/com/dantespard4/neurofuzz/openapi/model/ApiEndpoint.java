package com.dantespard4.neurofuzz.openapi.model;

import java.util.List;

public record ApiEndpoint (
    String path,
    String method,
    List<ApiParameter> parameters,
    String requestBodySchema,
    boolean requiresAuth,
    String authType // e.g., "apiKey", "oauth2",
){
}
