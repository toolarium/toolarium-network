/*
 * DigFactory.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.dig;

import com.github.toolarium.network.dig.dto.DnsRecordType;
import com.github.toolarium.network.dig.dto.IDigResult;
import com.github.toolarium.network.dig.impl.DigImpl;
import java.util.List;


/**
 * Factory for creating DNS dig instances.
 *
 * @author patrick
 */
public final class DigFactory {
    /**
     * Default dig timeout: 5 seconds.
     */
    static final int DEFAULT_TIMEOUT = 5000;


    /**
     * Private class, the only instance of the singleton which will be created by accessing the holder class.
     *
     * @author patrick
     */
    private static final class HOLDER {
        static final DigFactory INSTANCE = new DigFactory();
    }


    /**
     * Constructor
     */
    private DigFactory() {
        // NOP
    }


    /**
     * Get the instance
     *
     * @return the instance
     */
    public static DigFactory getInstance() {
        return HOLDER.INSTANCE;
    }


    /**
     * Get a dig instance with the specified timeout using system default DNS.
     *
     * @param timeout the query timeout in milliseconds
     * @return the dig instance
     */
    public IDig getDig(int timeout) {
        return new DigImpl(timeout);
    }


    /**
     * Get a dig instance with the specified timeout and DNS server.
     *
     * @param timeout the query timeout in milliseconds
     * @param dnsServer the DNS server address (e.g. "8.8.8.8")
     * @return the dig instance
     */
    public IDig getDig(int timeout, String dnsServer) {
        return new DigImpl(timeout, dnsServer);
    }


    /**
     * Get a dig instance with the default timeout (5 seconds).
     *
     * @return the dig instance
     */
    public IDig getDig() {
        return new DigImpl(DEFAULT_TIMEOUT);
    }


    /**
     * Convenience method: dig a single record type with default timeout.
     *
     * @param hostname the hostname to query
     * @param recordType the DNS record type
     * @return the dig result
     */
    public IDigResult dig(String hostname, DnsRecordType recordType) {
        return getDig().dig(hostname, recordType);
    }


    /**
     * Convenience method: dig all common record types with default timeout.
     *
     * @param hostname the hostname to query
     * @return the list of dig results
     */
    public List<IDigResult> digAll(String hostname) {
        return getDig().digAll(hostname);
    }
}
