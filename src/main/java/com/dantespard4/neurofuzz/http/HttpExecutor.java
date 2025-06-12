package com.dantespard4.neurofuzz.http;

import okhttp3.*;

import java.io.IOException;

public class HttpExecutor {

    private final OkHttpClient client = new OkHttpClient();

    public HttpResult sendPost(String url, String jsonPayload) {
        RequestBody body = RequestBody.create(jsonPayload, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        long start = System.nanoTime();

        try (Response response = client.newCall(request).execute()){
            long end = System.nanoTime();
            long durationMs = (end - start) / 1_000_000;

            int status = response.code();
            String responseBody = response.body() != null ? response.body().string() : "";
            return new HttpResult(status, durationMs, responseBody);
        }catch (IOException e){
            return new HttpResult(0,0,"[!] Error: " + e.getMessage());
        }
    }
}
