/*
 * HttpClientFactory.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.httpclient;

import com.github.toolarium.network.httpclient.dto.IHttpClientResult;
import com.github.toolarium.network.httpclient.impl.HttpClientUtilImpl;


/**
 * Factory for creating HTTP client utility instances.
 *
 * @author patrick
 */
public final class HttpClientFactory {
    /**
     * Default timeout: 10 seconds.
     */
    static final int DEFAULT_TIMEOUT = 10000;

    private static final class HOLDER {
        static final HttpClientFactory INSTANCE = new HttpClientFactory();
    }

    /**
     * Private constructor.
     */
    private HttpClientFactory() {
    }

    /**
     * Get the instance.
     *
     * @return the instance
     */
    public static HttpClientFactory getInstance() {
        return HOLDER.INSTANCE;
    }

    /**
     * Get an HTTP client with the specified timeout.
     *
     * @param timeout the timeout in milliseconds
     * @return the HTTP client
     */
    public IHttpClientUtil getHttpClient(int timeout) {
        return new HttpClientUtilImpl(timeout);
    }

    /**
     * Get an HTTP client with the default timeout (10 seconds).
     *
     * @return the HTTP client
     */
    public IHttpClientUtil getHttpClient() {
        return new HttpClientUtilImpl(DEFAULT_TIMEOUT);
    }

    /**
     * Convenience: perform a GET request with default timeout.
     *
     * @param url the URL
     * @return the result
     */
    public IHttpClientResult get(String url) {
        return getHttpClient().get(url);
    }

    /**
     * Convenience: perform a POST request with default timeout.
     *
     * @param url the URL
     * @param body the request body
     * @param contentType the content type
     * @return the result
     */
    public IHttpClientResult post(String url, String body, String contentType) {
        return getHttpClient().post(url, body, contentType);
    }
}
