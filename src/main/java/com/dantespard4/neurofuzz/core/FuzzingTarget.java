package com.dantespard4.neurofuzz.core;

import java.util.Map;

public record FuzzingTarget (
         String url,
         String method,
         String payload,
         Map<String, String> headers
){
}
