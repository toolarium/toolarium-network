/*
 * HttpClientResult.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.httpclient.dto;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * Implements the {@link IHttpClientResult}.
 *
 * @author patrick
 */
public class HttpClientResult implements IHttpClientResult, Serializable {
    private static final long serialVersionUID = 1L;
    private String url;
    private String method;
    private int statusCode;
    private String body;
    private Map<String, List<String>> headers;
    private long duration;
    private Exception exception;


    /**
     * Constructor for successful result.
     *
     * @param url the URL
     * @param method the method
     * @param statusCode the status code
     * @param body the body
     * @param headers the headers
     * @param duration the duration
     */
    public HttpClientResult(String url, String method, int statusCode, String body,
                             Map<String, List<String>> headers, long duration) {
        this.url = url;
        this.method = method;
        this.statusCode = statusCode;
        this.body = body;
        if (headers != null) {
            this.headers = new LinkedHashMap<>(headers);
        } else {
            this.headers = Collections.emptyMap();
        }
        this.duration = duration;
        this.exception = null;
    }


    /**
     * Constructor for failed result.
     *
     * @param url the URL
     * @param method the method
     * @param duration the duration
     * @param exception the exception
     */
    public HttpClientResult(String url, String method, long duration, Exception exception) {
        this.url = url;
        this.method = method;
        this.statusCode = -1;
        this.headers = Collections.emptyMap();
        this.duration = duration;
        this.exception = exception;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    @Override
    public boolean isSuccess() {
        return statusCode >= 200 && statusCode < 300;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public Exception getException() {
        return exception;
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, method, statusCode, duration);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        HttpClientResult other = (HttpClientResult) obj;
        return Objects.equals(url, other.url) && Objects.equals(method, other.method)
                && statusCode == other.statusCode && duration == other.duration;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("HttpClientResult [").append(method).append(" ").append(url);
        if (statusCode > 0) {
            sb.append(" -> ").append(statusCode);
            if (body != null) {
                sb.append(", ").append(body.length()).append(" bytes");
            }
            sb.append(" in ").append(duration).append("ms");
        } else {
            sb.append(" FAILED");
            if (exception != null) {
                sb.append(", error=").append(exception.getMessage());
            }
        }
        return sb.append("]").toString();
    }
}
