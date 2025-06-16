package com.dantespard4.neurofuzz.openapi;

import com.dantespard4.neurofuzz.openapi.model.ApiEndpoint;
import com.dantespard4.neurofuzz.openapi.model.ApiParameter;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.parser.OpenAPIV3Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OpenApiLoader {

    public Optional<List<ApiEndpoint>> parse(String specFilePath) {
        OpenAPI openAPI = new OpenAPIV3Parser().read(specFilePath);
        List<ApiEndpoint> endpoints = new ArrayList<>();

        if (openAPI.getPaths() == null) return Optional.empty();

        for (Map.Entry<String, PathItem> pathItemEntry : openAPI.getPaths().entrySet()) {
            String path = pathItemEntry.getKey();
            PathItem pathItem = pathItemEntry.getValue();

            for (PathItem.HttpMethod method : pathItem.readOperationsMap().keySet()) {
                Operation operation = pathItem.readOperationsMap().get(method);
                List<ApiParameter> parameters = new ArrayList<>();

                if (operation.getParameters() != null) {
                    for (Parameter param : operation.getParameters()) {
                        parameters.add(new ApiParameter(param.getName(), param.getIn(), param.getSchema().getType()));
                    }
                }

                String requesBodySchema = null;
                if (operation.getRequestBody() != null && operation.getRequestBody().getContent() != null) {
                    Schema<?> schema = operation.getRequestBody()
                            .getContent()
                            .get("application/json")
                            .getSchema();
                    if (schema != null) {
                        requesBodySchema = schema.getType();
                    }
                }
                endpoints.add(new ApiEndpoint(
                        path,
                        method.name().toLowerCase(),
                        parameters,
                        requesBodySchema
                ));
            }
        }
        return Optional.of(endpoints);
    }
}
