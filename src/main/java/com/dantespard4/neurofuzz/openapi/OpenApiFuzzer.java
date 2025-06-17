package com.dantespard4.neurofuzz.openapi;

import com.dantespard4.neurofuzz.openapi.generator.ParameterValueGenerator;
import com.dantespard4.neurofuzz.openapi.generator.PayloadGenerator;
import com.dantespard4.neurofuzz.openapi.model.ApiEndpoint;

import java.util.List;
import java.util.Map;

public class OpenApiFuzzer {

    private final List<ApiEndpoint> endpoints;
    private final ParameterValueGenerator parameterValueGenerator;
    private final PayloadGenerator payloadGenerator;

    private String payload;
    private Map<String, String> headers;
    private String url;

    public OpenApiFuzzer(List<ApiEndpoint> endpoints, String payloadSpecPath) {
        this.endpoints = endpoints;
        this.payloadGenerator = new PayloadGenerator(payloadSpecPath);
        this.parameterValueGenerator = new ParameterValueGenerator();
    }

    public void runFuzzing() {
        for (ApiEndpoint endpoint : endpoints) {

            if (endpoint.requestBodySchema() != null && !endpoint.requestBodySchema().isEmpty()) {
                var pay = payloadGenerator.generatePayloadFromSchemaName(endpoint.requestBodySchema());
                payload = pay != null ? pay.toString() : "{}";
            }

            if (endpoint.parameters() != null && !endpoint.parameters().isEmpty()) {
                var pathParams = parameterValueGenerator.generatePathParamExamples(endpoint.parameters());
                if (!pathParams.isEmpty()) {
                    url = endpoint.path();
                    for (Map.Entry<String, Object> pathParam : pathParams.entrySet()) {
                        url = url.replace("{" + pathParam.getKey() + "}", String.valueOf(pathParam.getValue()));
                    }
                } else {
                    url = endpoint.path();
                }
            } else {
                url = endpoint.path();
            }

            headers = endpoint.requiresAuth() ? Map.of("Authorizarion", "Bearer: 5446145") : Map.of();




        }
    }
}
