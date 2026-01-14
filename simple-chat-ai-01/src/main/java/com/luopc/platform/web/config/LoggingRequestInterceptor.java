package com.luopc.platform.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LoggingRequestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        // 打印请求信息
        logRequest(request, body);
        // 执行请求并包装响应
        return execution.execute(request, body);
    }

    private void logRequest(HttpRequest request, byte[] body) {
        logger.debug("Request URI: {} {}", request.getMethod(), request.getURI());
        logger.debug("Request Headers: {}", request.getHeaders());
        if (body.length > 0) {
            logger.debug("Request Body: {}", new String(body, StandardCharsets.UTF_8));
        }
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        logger.debug("Response Code: {}, Headers: {}", response.getStatusCode(), response.getHeaders());
        StringBuilder responseBody = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                responseBody.append(line);
            }
        }
        if (responseBody.length() > 0) {
            logger.debug("Response Body: {}", responseBody.toString());
        }
    }
}
