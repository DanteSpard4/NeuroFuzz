package com.dantespard4.neurofuzz;

import com.dantespard4.neurofuzz.http.HttpExecutor;
import com.dantespard4.neurofuzz.openapi.OpenApiLoader;
import com.dantespard4.neurofuzz.openapi.generator.ParameterValueGenerator;
import com.dantespard4.neurofuzz.openapi.generator.PayloadGenerator;

import java.util.Map;
public class sample {
    public static void main(String[] args) {
        var endpoints = new OpenApiLoader()
                .parse("src/main/resources/openapi.json")
                .orElseThrow(() -> new RuntimeException("Failed to load OpenAPI spec"));


        var payloadGenerator = new PayloadGenerator("src/main/resources/openapi.json");

        var httpExecutor = new HttpExecutor(10);
        var url = "https://webhook.site/fc99172f-55b3-469d-9d47-53908f7755f1";
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

            String url2 =  ep.path();
            if (!pathParams.isEmpty()) {
                System.out.println("Generated Path Parameters: " + pathParams);
                for (Map.Entry<String, Object> pathParam: pathParams.entrySet()) {
                    url2 = url2.replace("{" + pathParam.getKey() + "}", String.valueOf(pathParam.getValue()));
                }
                System.out.println("URL with Path Parameter: " + url2);
            } else {
                System.out.println("No path parameters found for endpoint: " + ep.path());
            }

            var header = Map.of("Authorizarion", "Bearer: 5446145");
            var payloadString = payload != null ? payload.toString() : "{}";

            httpExecutor.sendHttpMethod(url+url2,ep.method(), payloadString, header);

        }
    }
}
