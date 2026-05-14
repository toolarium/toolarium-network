/*
 * IHttpClientUtil.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.httpclient;

import com.github.toolarium.network.httpclient.dto.IHttpClientResult;
import java.util.Map;


/**
 * Defines a simple HTTP client utility interface.
 *
 * @author patrick
 */
public interface IHttpClientUtil {

    /**
     * Perform an HTTP GET request.
     *
     * @param url the URL
     * @return the result
     */
    IHttpClientResult get(String url);


    /**
     * Perform an HTTP GET request with custom headers.
     *
     * @param url the URL
     * @param headers the request headers
     * @return the result
     */
    IHttpClientResult get(String url, Map<String, String> headers);


    /**
     * Perform an HTTP POST request.
     *
     * @param url the URL
     * @param body the request body
     * @param contentType the content type
     * @return the result
     */
    IHttpClientResult post(String url, String body, String contentType);


    /**
     * Perform an HTTP PUT request.
     *
     * @param url the URL
     * @param body the request body
     * @param contentType the content type
     * @return the result
     */
    IHttpClientResult put(String url, String body, String contentType);


    /**
     * Perform an HTTP DELETE request.
     *
     * @param url the URL
     * @return the result
     */
    IHttpClientResult delete(String url);
}
