package com.dantespard4.neurofuzz.openapi;

import com.dantespard4.neurofuzz.core.FuzzingTarget;
import com.dantespard4.neurofuzz.openapi.generator.ParameterValueGenerator;
import com.dantespard4.neurofuzz.openapi.generator.PayloadGenerator;
import com.dantespard4.neurofuzz.openapi.model.ApiEndpoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.util.List;
import java.util.Map;

public class OpenApiService {

    private final OpenApiLoader openApiLoader;
    private final PayloadGenerator payloadGenerator;
    private final ParameterValueGenerator parameterValueGenerator;
    private final ObjectWriter JSON_WRITER = new ObjectMapper().writer();


    public OpenApiService (String openApiFilePath) {
        this.openApiLoader = new OpenApiLoader(openApiFilePath);
        this.payloadGenerator = new PayloadGenerator(openApiFilePath);
        this.parameterValueGenerator = new ParameterValueGenerator();
    }

    public List<FuzzingTarget> generateFuzzingTargets(String baseUrl) {
        var endpoints = openApiLoader.parse()
                .orElseThrow(() -> new IllegalStateException("Failed to parse OpenAPI specification"));

        return endpoints.stream()
                .map(endpoint -> generateFuzzingTarget(baseUrl, endpoint))
                .toList();

    }

    private FuzzingTarget generateFuzzingTarget(String baseUrl, ApiEndpoint endpoint) {

        String endpointPath = endpoint.path();
        Map<String, Object> pathParams = parameterValueGenerator.generateAllParamExamples(endpoint.parameters());

        for (Map.Entry<String, Object> entry : pathParams.entrySet()) {
            endpointPath = endpointPath.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }

        String fullUrl = baseUrl + endpointPath;

        String payload = null;
        if (endpoint.requestBodySchema() != null) {
            payload = payloadToJsonString(payloadGenerator.generatePayloadFromSchemaName(endpoint.requestBodySchema()));
        }

        var headers = Map.of("Authorization", "Bearer: 5446145");
        return new FuzzingTarget(
                fullUrl,
                endpoint.method(),
                payload,
                headers
        );

    }

    public String payloadToJsonString(Map<String, Object> payload) {
        try {
            return JSON_WRITER.writeValueAsString(payload);
        } catch (Exception e) {
            System.err.println("Error al serializar payload: " + e.getMessage());
            return "{}";
        }
    }

}
