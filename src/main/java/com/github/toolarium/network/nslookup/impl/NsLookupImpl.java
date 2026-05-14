/*
 * NsLookupImpl.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.nslookup.impl;

import com.github.toolarium.network.nslookup.INsLookup;
import com.github.toolarium.network.nslookup.dto.INsLookupResult;
import com.github.toolarium.network.nslookup.dto.NsLookupResult;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implements DNS lookups using {@link InetAddress} with configurable timeout
 * via an executor service for parallel resolution.
 *
 * @author patrick
 */
public class NsLookupImpl implements INsLookup {
    private static final Logger LOG = LoggerFactory.getLogger(NsLookupImpl.class);
    private final int timeout;


    /**
     * Constructor for NsLookupImpl
     *
     * @param timeout the lookup timeout in milliseconds
     */
    public NsLookupImpl(int timeout) {
        this.timeout = timeout;
    }


    /**
     * @see com.github.toolarium.network.nslookup.INsLookup#lookup(java.lang.String)
     */
    @Override
    public INsLookupResult lookup(String hostname) {
        if (hostname == null || hostname.trim().isEmpty()) {
            return new NsLookupResult(hostname, null, Collections.emptyList(), false, 0, new IllegalArgumentException("Hostname is null or empty"));
        }

        final String host = hostname.trim();
        final long start = System.currentTimeMillis();

        try {
            InetAddress[] addresses = resolveWithTimeout(host);
            long duration = System.currentTimeMillis() - start;

            String canonicalHostname = addresses[0].getCanonicalHostName();
            List<String> addressList = new ArrayList<>();
            for (InetAddress addr : addresses) {
                addressList.add(addr.getHostAddress());
            }

            NsLookupResult result = new NsLookupResult(host, canonicalHostname, addressList, true, duration, null);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Forward lookup: " + result);
            }
            return result;

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            NsLookupResult result = new NsLookupResult(host, null, Collections.emptyList(), false, duration, e);
            LOG.debug("Forward lookup failed for " + host + ": " + e.getMessage());
            return result;
        }
    }


    /**
     * @see com.github.toolarium.network.nslookup.INsLookup#lookup(java.lang.String[])
     */
    @Override
    public List<INsLookupResult> lookup(String... hostnames) {
        return executeBatch(hostnames, false);
    }


    /**
     * @see com.github.toolarium.network.nslookup.INsLookup#reverseLookup(java.lang.String)
     */
    @Override
    public INsLookupResult reverseLookup(String ipAddress) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            return new NsLookupResult(ipAddress, null, Collections.emptyList(), false, 0, new IllegalArgumentException("IP address is null or empty"));
        }

        final String ip = ipAddress.trim();
        final long start = System.currentTimeMillis();

        try {
            InetAddress addr = InetAddress.getByName(ip);
            String hostname = addr.getCanonicalHostName();
            long duration = System.currentTimeMillis() - start;

            List<String> addressList = new ArrayList<>();
            addressList.add(addr.getHostAddress());

            NsLookupResult result = new NsLookupResult(ip, hostname, addressList, true, duration, null);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Reverse lookup: " + result);
            }
            return result;

        } catch (UnknownHostException e) {
            long duration = System.currentTimeMillis() - start;
            NsLookupResult result = new NsLookupResult(ip, null, Collections.emptyList(), false, duration, e);
            LOG.debug("Reverse lookup failed for " + ip + ": " + e.getMessage());
            return result;
        }
    }


    /**
     * @see com.github.toolarium.network.nslookup.INsLookup#reverseLookup(java.lang.String[])
     */
    @Override
    public List<INsLookupResult> reverseLookup(String... ipAddresses) {
        return executeBatch(ipAddresses, true);
    }


    /**
     * Execute lookups in parallel.
     *
     * @param queries the queries to resolve
     * @param reverse true for reverse lookup
     * @return the results
     */
    private List<INsLookupResult> executeBatch(String[] queries, boolean reverse) {
        if (queries == null || queries.length == 0) {
            return Collections.emptyList();
        }

        ExecutorService executor = Executors.newFixedThreadPool(Math.min(queries.length, 20));
        List<Future<INsLookupResult>> futures = new ArrayList<>();

        for (final String query : queries) {
            futures.add(executor.submit(new Callable<INsLookupResult>() {
                @Override
                public INsLookupResult call() {
                    if (reverse) {
                        return reverseLookup(query);
                    }
                    return lookup(query);
                }
            }));
        }

        executor.shutdown();

        List<INsLookupResult> results = new ArrayList<>();
        for (Future<INsLookupResult> future : futures) {
            try {
                INsLookupResult result = future.get(timeout, TimeUnit.MILLISECONDS);
                results.add(result);
            } catch (Exception e) {
                LOG.debug("Batch lookup timed out or failed: " + e.getMessage());
            }
        }

        return results;
    }


    /**
     * Resolve with timeout using an executor.
     *
     * @param host the host to resolve
     * @return the resolved addresses
     * @throws Exception In case of an error
     */
    private InetAddress[] resolveWithTimeout(final String host) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<InetAddress[]> future = executor.submit(new Callable<InetAddress[]>() {
                @Override
                public InetAddress[] call() throws UnknownHostException {
                    return InetAddress.getAllByName(host);
                }
            });
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } finally {
            executor.shutdown();
        }
    }
}
