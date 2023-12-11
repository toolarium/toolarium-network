/*
 * Request.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.server.dto;

import java.util.Date;
import java.util.Map;

/**
 * Defines the http request
 * 
 * @author patrick
 */
public interface IHttpRequest {
    
    /**
     * Get the request time stamp
     *
     * @return the request time stamp
     */
    Date getRequestTimestamp();
    
    
    /**
     * Get the version
     *
     * @return the version
     */
    String getVersion();
    

    /**
     * Get the path
     *
     * @return the path
     */
    String getPath();
    
    
    /**
     * Get the method
     *
     * @return the method
     */
    String getMethod();
    
    
    /**
     * Get header
     *
     * @param headerName the header name
     * @return the header value
     */
    String getHeader(String headerName);

    
    /**
     * Check if the header name exist
     *
     * @param headerName the header name
     * @return true if exists
     */
    boolean containsHeader(String headerName);

    
    /**
     * Get headers
     *
     * @return the headers
     */
    Map<String, String> getHeaders();

    
    /**
     * Get the parameter
     *
     * @param paramName the parameter name
     * @return the parameter name
     */
    String getParameter(String paramName);

    
    /**
     * Check if the parameter exists
     *
     * @param parameterName the parameter name
     * @return true if the parameter exists
     */
    boolean containsParameter(String parameterName);

    
    /**
     * Get parameters
     *
     * @return the parameters
     */
    Map<String, String> getParameters();

    
    /**
     * Get the base location
     *
     * @return the base location
     */
    String getBaseLocation();
    
    
    /**
     * Get the body
     *
     * @return the body of the request
     */
    String getBody();
}
