package com.dantespard4.neurofuzz.http;

import okhttp3.*;

import java.io.IOException;

public class HttpExecutor {

    private final OkHttpClient client ;

    public HttpExecutor(int timeoutSeconds) {
        this.client = new OkHttpClient.Builder()
                .callTimeout(timeoutSeconds, java.util.concurrent.TimeUnit.SECONDS)
                .build();
    }

    public HttpResult sendPost(String url, String jsonPayload) {
        RequestBody body = RequestBody.create(jsonPayload, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        long start = System.nanoTime();

        try (Response response = client.newCall(request).execute()){
            ResponseBody responseBody = response.body();

            long durationMs = calculateDuration(start);
            int status = response.code();
            String bodyContent = responseBody != null ? responseBody.string() : "";
            return new HttpResult(status, durationMs, bodyContent);
        }catch (IOException e){
            return handleHttpError(e);
        }
    }

    private long calculateDuration(long startTime) {
        return (System.nanoTime() - startTime) / 1_000_000;
    }

    private HttpResult handleHttpError(IOException e) {
        System.out.printf("Error in HTTP request: %s%n", e);
        if (e.getMessage().contains("timeout")) {
            return new HttpResult(600, 0, "[!] Timeout: " + e.getMessage());
        }
        return new HttpResult(0, 0, "[!] Error: " + e.getMessage());
    }
}
