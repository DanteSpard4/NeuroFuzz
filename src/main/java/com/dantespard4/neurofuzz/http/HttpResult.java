package com.dantespard4.neurofuzz.http;

public class HttpResult {
    private final int statusCode;
    private final long responseTimeMs;
    private final String responseBody;


    public HttpResult(int statusCode,long responseTimeMs, String responseBody) {
        this.statusCode = statusCode;
        this.responseTimeMs = responseTimeMs;
        this.responseBody = responseBody;
    }

    @Override
    public String toString() {
        return "[OK] " + statusCode + " - " + responseTimeMs + "ms - " + responseBody.length() + " bytes";
    }
}
