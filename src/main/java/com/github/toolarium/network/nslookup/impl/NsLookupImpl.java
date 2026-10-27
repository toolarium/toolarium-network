/*
 * NsLookupImpl.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.nslookup.impl;

import com.github.toolarium.network.nslookup.INsLookup;
import com.github.toolarium.network.nslookup.dto.INsLookupResult;
import com.github.toolarium.network.nslookup.dto.NsLookupResult;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
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
    private static final int MAX_THREADS = 20;
    private final int timeout;
    private final ExecutorService executor;


    /**
     * Constructor for NsLookupImpl
     *
     * @param timeout the lookup timeout in milliseconds
     */
    public NsLookupImpl(int timeout) {
        this.timeout = timeout;
        this.executor = Executors.newFixedThreadPool(MAX_THREADS, new ThreadFactory() {
            private final AtomicInteger count = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "nslookup-" + count.incrementAndGet());
                t.setDaemon(true);
                return t;
            }
        });
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

            String canonicalHostname = addresses[0].getHostName();
            List<String> addressList = new ArrayList<>();
            for (InetAddress addr : addresses) {
                addressList.add(addr.getHostAddress());
            }

            NsLookupResult result = new NsLookupResult(host, canonicalHostname, addressList, true, duration, null);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Forward lookup: {}", result);
            }
            return result;

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            NsLookupResult result = new NsLookupResult(host, null, Collections.emptyList(), false, duration, e);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Forward lookup failed for {}: {}", host, e.getMessage());
            }
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
            InetAddress[] addresses = resolveWithTimeout(ip);
            InetAddress addr = addresses[0];
            String hostname = addr.getCanonicalHostName();
            long duration = System.currentTimeMillis() - start;

            List<String> addressList = new ArrayList<>();
            addressList.add(addr.getHostAddress());

            NsLookupResult result = new NsLookupResult(ip, hostname, addressList, true, duration, null);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Reverse lookup: {}", result);
            }
            return result;

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            NsLookupResult result = new NsLookupResult(ip, null, Collections.emptyList(), false, duration, e);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Reverse lookup failed for {}: {}", ip, e.getMessage());
            }
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

        List<INsLookupResult> results = new ArrayList<>();
        for (int i = 0; i < futures.size(); i++) {
            Future<INsLookupResult> future = futures.get(i);
            try {
                INsLookupResult result = future.get(timeout, TimeUnit.MILLISECONDS);
                results.add(result);
            } catch (java.util.concurrent.TimeoutException e) {
                future.cancel(true);
                String query = queries[i];
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Batch lookup timed out for: {}", query);
                }
                results.add(new NsLookupResult(query, null, Collections.emptyList(), false, timeout,
                        new java.net.UnknownHostException("DNS resolution timed out for: " + query)));
            } catch (Exception e) {
                future.cancel(true);
                String query = queries[i];
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Batch lookup failed for {}: {}", query, e.getMessage());
                }
                results.add(new NsLookupResult(query, null, Collections.emptyList(), false, 0, e));
            }
        }

        return results;
    }


    /**
     * Resolve a hostname/IP with timeout using the shared executor.
     *
     * @param host the hostname or IP address
     * @return the resolved addresses
     * @throws java.net.UnknownHostException if resolution times out
     * @throws Exception if resolution fails
     */
    private InetAddress[] resolveWithTimeout(final String host) throws Exception {
        Future<InetAddress[]> future = executor.submit(new Callable<InetAddress[]>() {
            @Override
            public InetAddress[] call() throws Exception {
                return InetAddress.getAllByName(host);
            }
        });

        try {
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (java.util.concurrent.TimeoutException e) {
            future.cancel(true);
            throw new java.net.UnknownHostException("DNS resolution timed out for: " + host);
        } catch (java.util.concurrent.ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof Exception) {
                throw (Exception) cause;
            }
            throw e;
        }
    }


    /**
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }



}
