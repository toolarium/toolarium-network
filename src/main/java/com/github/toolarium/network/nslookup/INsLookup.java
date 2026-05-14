/*
 * INsLookup.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.nslookup;

import com.github.toolarium.network.nslookup.dto.INsLookupResult;
import java.util.List;


/**
 * Defines the DNS lookup interface for resolving hostnames to IP addresses
 * and IP addresses to hostnames (reverse lookup).
 *
 * @author patrick
 */
public interface INsLookup {

    /**
     * Perform a forward DNS lookup — resolve a hostname to its IP addresses.
     *
     * @param hostname the hostname to resolve
     * @return the lookup result
     */
    INsLookupResult lookup(String hostname);


    /**
     * Perform forward DNS lookups for multiple hosts in parallel.
     *
     * @param hostnames the hostnames to resolve
     * @return the list of lookup results
     */
    List<INsLookupResult> lookup(String... hostnames);


    /**
     * Perform a reverse DNS lookup — resolve an IP address to its hostname.
     *
     * @param ipAddress the IP address to resolve
     * @return the lookup result
     */
    INsLookupResult reverseLookup(String ipAddress);


    /**
     * Perform reverse DNS lookups for multiple IP addresses in parallel.
     *
     * @param ipAddresses the IP addresses to resolve
     * @return the list of lookup results
     */
    List<INsLookupResult> reverseLookup(String... ipAddresses);
}
