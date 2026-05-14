/*
 * IDigResult.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.dig.dto;

import java.util.List;


/**
 * Defines the result of a dig (DNS record query) operation.
 *
 * @author patrick
 */
public interface IDigResult {

    /**
     * Get the queried hostname.
     *
     * @return the query hostname
     */
    String getQuery();


    /**
     * Get the queried record type.
     *
     * @return the record type
     */
    DnsRecordType getRecordType();


    /**
     * Get the resolved DNS records.
     *
     * @return the list of DNS records, empty if lookup failed
     */
    List<IDnsRecord> getRecords();


    /**
     * Check if the query was successful.
     *
     * @return true if the DNS query succeeded
     */
    boolean isSuccess();


    /**
     * Get the query duration in milliseconds.
     *
     * @return the duration in milliseconds
     */
    long getDuration();


    /**
     * Get the exception if the query failed.
     *
     * @return the exception or null if successful
     */
    Exception getException();
}
