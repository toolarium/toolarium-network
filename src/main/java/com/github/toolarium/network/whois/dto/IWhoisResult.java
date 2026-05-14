/*
 * IWhoisResult.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.whois.dto;

import java.util.Map;


/**
 * Defines the result of a WHOIS query.
 *
 * @author patrick
 */
public interface IWhoisResult {

    /**
     * Get the queried domain or IP.
     *
     * @return the query
     */
    String getQuery();


    /**
     * Get the WHOIS server used.
     *
     * @return the whois server
     */
    String getWhoisServer();


    /**
     * Get the raw WHOIS response.
     *
     * @return the raw response text
     */
    String getRawResponse();


    /**
     * Get parsed key-value fields from the WHOIS response.
     *
     * @return the parsed fields
     */
    Map<String, String> getFields();


    /**
     * Check if the query was successful.
     *
     * @return true if successful
     */
    boolean isSuccess();


    /**
     * Get the query duration in milliseconds.
     *
     * @return the duration
     */
    long getDuration();


    /**
     * Get the exception if the query failed.
     *
     * @return the exception or null
     */
    Exception getException();
}
