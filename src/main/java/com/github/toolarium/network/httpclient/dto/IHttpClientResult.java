/*
 * IHttpClientResult.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.httpclient.dto;

import java.util.List;
import java.util.Map;


/**
 * Defines the result of an HTTP client request.
 *
 * @author patrick
 */
public interface IHttpClientResult {

    /**
     * Get the request URL.
     *
     * @return the URL
     */
    String getUrl();


    /**
     * Get the HTTP method used.
     *
     * @return the method (GET, POST, etc.)
     */
    String getMethod();


    /**
     * Get the HTTP status code.
     *
     * @return the status code or -1 if request failed
     */
    int getStatusCode();


    /**
     * Get the response body.
     *
     * @return the body or null
     */
    String getBody();


    /**
     * Get the response headers.
     *
     * @return the headers
     */
    Map<String, List<String>> getHeaders();


    /**
     * Check if the request was successful (status 2xx).
     *
     * @return true if successful
     */
    boolean isSuccess();


    /**
     * Get the request duration in milliseconds.
     *
     * @return the duration
     */
    long getDuration();


    /**
     * Get the exception if the request failed.
     *
     * @return the exception or null
     */
    Exception getException();
}
