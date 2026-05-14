/*
 * IDnsRecord.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.dig.dto;


/**
 * Defines a single DNS record returned by a dig query.
 *
 * @author patrick
 */
public interface IDnsRecord {

    /**
     * Get the record type (e.g. A, AAAA, MX).
     *
     * @return the record type
     */
    DnsRecordType getType();


    /**
     * Get the record value (e.g. IP address, mail server hostname).
     *
     * @return the record value
     */
    String getValue();


    /**
     * Get the priority (relevant for MX and SRV records).
     * Returns -1 if not applicable.
     *
     * @return the priority or -1
     */
    int getPriority();
}
