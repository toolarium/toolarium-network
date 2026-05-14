/*
 * HttpClientUtilImpl.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.httpclient.impl;

import com.github.toolarium.network.httpclient.IHttpClientUtil;
import com.github.toolarium.network.httpclient.dto.HttpClientResult;
import com.github.toolarium.network.httpclient.dto.IHttpClientResult;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implements the HTTP client utility using {@link java.net.http.HttpClient}.
 *
 * @author patrick
 */
public class HttpClientUtilImpl implements IHttpClientUtil {
    private static final Logger LOG = LoggerFactory.getLogger(HttpClientUtilImpl.class);
    private final int timeout;


    /**
     * Constructor for HttpClientUtilImpl
     *
     * @param timeout the timeout in milliseconds
     */
    public HttpClientUtilImpl(int timeout) {
        this.timeout = timeout;
    }


    @Override
    public IHttpClientResult get(String url) {
        return get(url, null);
    }


    @Override
    public IHttpClientResult get(String url, Map<String, String> headers) {
        return execute("GET", url, null, null, headers);
    }


    @Override
    public IHttpClientResult post(String url, String body, String contentType) {
        return execute("POST", url, body, contentType, null);
    }


    @Override
    public IHttpClientResult put(String url, String body, String contentType) {
        return execute("PUT", url, body, contentType, null);
    }


    @Override
    public IHttpClientResult delete(String url) {
        return execute("DELETE", url, null, null, null);
    }


    /**
     * Execute an HTTP request.
     *
     * @param method the HTTP method
     * @param url the URL
     * @param body the request body (nullable)
     * @param contentType the content type (nullable)
     * @param headers the custom headers (nullable)
     * @return the result
     */
    private IHttpClientResult execute(String method, String url, String body, String contentType, Map<String, String> headers) {
        if (url == null || url.trim().isEmpty()) {
            return new HttpClientResult(url, method, 0, new IllegalArgumentException("URL is null or empty"));
        }

        long start = System.currentTimeMillis();

        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url.trim()))
                    .timeout(Duration.ofMillis(timeout));

            // Set body
            HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.noBody();
            if (body != null) {
                bodyPublisher = HttpRequest.BodyPublishers.ofString(body);
            }

            builder.method(method, bodyPublisher);

            // Set content type
            if (contentType != null && !contentType.isEmpty()) {
                builder.header("Content-Type", contentType);
            }

            // Set custom headers
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    builder.header(entry.getKey(), entry.getValue());
                }
            }

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofMillis(timeout))
                    .build();

            HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            long duration = System.currentTimeMillis() - start;

            HttpClientResult result = new HttpClientResult(url, method, response.statusCode(),
                    response.body(), response.headers().map(), duration);

            if (LOG.isDebugEnabled()) {
                LOG.debug("HTTP " + method + ": " + result);
            }
            return result;

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            LOG.debug("HTTP " + method + " failed for " + url + ": " + e.getMessage());
            return new HttpClientResult(url, method, duration, e);
        }
    }
}
