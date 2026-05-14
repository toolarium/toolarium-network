/*
 * NsLookupFactory.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.nslookup;

import com.github.toolarium.network.nslookup.dto.INsLookupResult;
import com.github.toolarium.network.nslookup.impl.NsLookupImpl;
import java.util.List;


/**
 * Factory for creating DNS lookup instances.
 *
 * @author patrick
 */
public final class NsLookupFactory {
    /**
     * Default lookup timeout: 5 seconds.
     */
    static final int DEFAULT_TIMEOUT = 5000;


    /**
     * Private class, the only instance of the singleton which will be created by accessing the holder class.
     *
     * @author patrick
     */
    private static final class HOLDER {
        static final NsLookupFactory INSTANCE = new NsLookupFactory();
    }


    /**
     * Constructor
     */
    private NsLookupFactory() {
        // NOP
    }


    /**
     * Get the instance
     *
     * @return the instance
     */
    public static NsLookupFactory getInstance() {
        return HOLDER.INSTANCE;
    }


    /**
     * Get an nslookup instance with the specified timeout.
     *
     * @param timeout the lookup timeout in milliseconds
     * @return the nslookup instance
     */
    public INsLookup getNsLookup(int timeout) {
        return new NsLookupImpl(timeout);
    }


    /**
     * Get an nslookup instance with the default timeout (5 seconds).
     *
     * @return the nslookup instance
     */
    public INsLookup getNsLookup() {
        return new NsLookupImpl(DEFAULT_TIMEOUT);
    }


    /**
     * Convenience method: forward lookup of a single hostname with default timeout.
     *
     * @param hostname the hostname to resolve
     * @return the lookup result
     */
    public INsLookupResult lookup(String hostname) {
        return getNsLookup().lookup(hostname);
    }


    /**
     * Convenience method: forward lookup of multiple hostnames with default timeout.
     *
     * @param hostnames the hostnames to resolve
     * @return the list of lookup results
     */
    public List<INsLookupResult> lookup(String... hostnames) {
        return getNsLookup().lookup(hostnames);
    }


    /**
     * Convenience method: reverse lookup of a single IP address with default timeout.
     *
     * @param ipAddress the IP address to resolve
     * @return the lookup result
     */
    public INsLookupResult reverseLookup(String ipAddress) {
        return getNsLookup().reverseLookup(ipAddress);
    }
}
