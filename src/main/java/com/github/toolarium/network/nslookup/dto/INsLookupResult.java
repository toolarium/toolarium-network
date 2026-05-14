/*
 * INsLookupResult.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.nslookup.dto;

import java.util.List;


/**
 * Defines the result of a DNS lookup operation.
 *
 * @author patrick
 */
public interface INsLookupResult {

    /**
     * Get the queried hostname or IP address.
     *
     * @return the query input
     */
    String getQuery();


    /**
     * Get the resolved canonical hostname.
     * For forward lookups this is the canonical name of the host.
     * For reverse lookups this is the hostname resolved from the IP.
     *
     * @return the resolved hostname or null if lookup failed
     */
    String getHostname();


    /**
     * Get all resolved IP addresses.
     *
     * @return the list of resolved IP addresses, empty if lookup failed
     */
    List<String> getAddresses();


    /**
     * Check if the lookup was successful.
     *
     * @return true if the DNS lookup succeeded
     */
    boolean isSuccess();


    /**
     * Get the lookup duration in milliseconds.
     *
     * @return the duration in milliseconds
     */
    long getDuration();


    /**
     * Get the exception if the lookup failed.
     *
     * @return the exception or null if successful
     */
    Exception getException();
}
