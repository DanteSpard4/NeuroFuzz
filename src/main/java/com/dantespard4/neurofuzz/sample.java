package com.dantespard4.neurofuzz;

import com.dantespard4.neurofuzz.openapi.generator.ParameterValueGenerator;
import com.dantespard4.neurofuzz.openapi.generator.PayloadGenerator;

import java.util.Map;

public class sample {
    public static void main(String[] args) {
        var endpoints = new com.dantespard4.neurofuzz.openapi.OpenApiLoader()
                .parse("src/main/resources/openapi.json")
                .orElseThrow(() -> new RuntimeException("Failed to load OpenAPI spec"));


        var payloadGenerator = new PayloadGenerator("src/main/resources/openapi.json");

        for (var ep: endpoints){
            System.out.println("Endpoint: " + ep.path() + " Method: " + ep.method() +
                    " Parameters: " + ep.parameters() +
                    " Request Body Schema: " + ep.requestBodySchema() +" Required Auth: "+ ep.requiresAuth() + " AuthType: "+ ep.authType());

            var payload = payloadGenerator.generatePayloadFromSchemaName(ep.requestBodySchema());

            if (payload != null) {
                System.out.println("Generated Payload: " + payload);
            } else {
                System.out.println("No payload generated for schema: " + ep.requestBodySchema());
            }

            var pathParams = new ParameterValueGenerator()
                    .generatePathParamExamples(ep.parameters());

            if (!pathParams.isEmpty()) {
                System.out.println("Generated Path Parameters: " + pathParams);
                var url = ep.path();
                for (Map.Entry<String, Object> pathParam: pathParams.entrySet()) {
                    url = url.replace("{" + pathParam.getKey() + "}", String.valueOf(pathParam.getValue()));
                }
                System.out.println("URL with Path Parameter: " + url);
            } else {
                System.out.println("No path parameters found for endpoint: " + ep.path());
            }
        }
    }
}
