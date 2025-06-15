package com.dantespard4.neurofuzz.http;

public record HttpResult(
        int statusCode,
        long responseTimeMs,
        String responseBody) {
}
