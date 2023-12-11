/*
 * IHttpResponse.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.server.dto;


import java.util.Map;


/**
 * Defines the http response
 * 
 * @author patrick
 */
public interface IHttpResponse {
    
    /**
     * Get the status
     *
     * @return the status
     */
    int getStatus();

    
    /**
     * Get the version
     *
     * @return the version
     */
    String getVersion();

    
    /**
     * Gets the headers
     *
     * @return the http response headers
     */
    Map<String, String> getHeaders();

        
    /**
     * Get the body
     *
     * @return the body
     */
    String getBody();
}
