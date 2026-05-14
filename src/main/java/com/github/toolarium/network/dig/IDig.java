/*
 * IDig.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.dig;

import com.github.toolarium.network.dig.dto.DnsRecordType;
import com.github.toolarium.network.dig.dto.IDigResult;
import java.util.List;


/**
 * Defines the DNS dig interface for querying specific DNS record types.
 *
 * @author patrick
 */
public interface IDig {

    /**
     * Query a specific DNS record type for a hostname.
     *
     * @param hostname the hostname to query
     * @param recordType the DNS record type
     * @return the dig result
     */
    IDigResult dig(String hostname, DnsRecordType recordType);


    /**
     * Query multiple DNS record types for a hostname.
     *
     * @param hostname the hostname to query
     * @param recordTypes the DNS record types to query
     * @return the list of dig results (one per record type)
     */
    List<IDigResult> dig(String hostname, DnsRecordType... recordTypes);


    /**
     * Query all common DNS record types (A, AAAA, MX, CNAME, TXT, NS) for a hostname.
     *
     * @param hostname the hostname to query
     * @return the list of dig results
     */
    List<IDigResult> digAll(String hostname);
}
