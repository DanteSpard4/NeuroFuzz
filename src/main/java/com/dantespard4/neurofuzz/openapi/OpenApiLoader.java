package com.dantespard4.neurofuzz.openapi;

import com.dantespard4.neurofuzz.openapi.model.ApiEndpoint;
import com.dantespard4.neurofuzz.openapi.model.ApiParameter;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.parser.OpenAPIV3Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OpenApiLoader {

    private OpenAPI openAPI;

    public Optional<List<ApiEndpoint>> parse(String specFilePath) {
        openAPI = new OpenAPIV3Parser().read(specFilePath);

        if (openAPI.getPaths() == null) return Optional.empty();

        List<ApiEndpoint> endpoints = extractEndpoints();
        return Optional.of(endpoints);
    }

    private List<ApiEndpoint> extractEndpoints() {
        List<ApiEndpoint> endpoints = new ArrayList<>();

        for (Map.Entry<String, PathItem> pathItemEntry : openAPI.getPaths().entrySet()) {
            String path = pathItemEntry.getKey();
            PathItem pathItem = pathItemEntry.getValue();

            for (PathItem.HttpMethod method : pathItem.readOperationsMap().keySet()) {
                Operation operation = pathItem.readOperationsMap().get(method);
                endpoints.add(createEndpoint(path, method, operation));
            }
        }

        return endpoints;
    }

    private ApiEndpoint createEndpoint(String path, PathItem.HttpMethod method, Operation operation) {
        List<ApiParameter> parameters = extractParameters(operation);
        String requestBodySchema = extractRequestBodySchema(operation);
        Map.Entry<Boolean, String> authInfo = determineAuthRequirements(operation);

        return new ApiEndpoint(
                path,
                method.name().toLowerCase(),
                parameters,
                requestBodySchema,
                authInfo.getKey(),
                authInfo.getValue()
        );
    }

    private List<ApiParameter> extractParameters(Operation operation) {
        List<ApiParameter> parameters = new ArrayList<>();

        if (operation.getParameters() != null) {
            for (Parameter param : operation.getParameters()) {
                parameters.add(new ApiParameter(
                        param.getName(),
                        param.getIn(),
                        param.getSchema().getType(),
                        param.getRequired()
                ));
            }
        }

        return parameters;
    }

    private String extractRequestBodySchema(Operation operation) {
        if (operation.getRequestBody() == null || operation.getRequestBody().getContent() == null) {
            return null;
        }

        var content = operation.getRequestBody().getContent().get("application/json");
        if (content == null || content.getSchema() == null) {
            return null;
        }

        Schema<?> schema = content.getSchema();
        if (schema.get$ref() != null) {
            String ref = schema.get$ref();
            return ref.substring(ref.lastIndexOf('/') + 1);
        }

        return schema.getType();
    }

    private Map.Entry<Boolean, String> determineAuthRequirements(Operation operation) {
        boolean requiresAuth = false;
        String authScheme = "none";

        List<SecurityRequirement> securityRequirements = operation.getSecurity();
        if ((securityRequirements == null || securityRequirements.isEmpty()) && openAPI.getSecurity() != null) {
            securityRequirements = openAPI.getSecurity();
        }

        if (securityRequirements != null) {
            for (SecurityRequirement secReq : securityRequirements) {
                for (String schemeName : secReq.keySet()) {
                    requiresAuth = true;
                    authScheme = schemeName;
                    return Map.entry(requiresAuth, authScheme);
                }
            }
        }

        return Map.entry(requiresAuth, authScheme);
    }
}