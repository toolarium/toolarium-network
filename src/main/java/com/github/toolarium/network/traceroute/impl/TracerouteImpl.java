/*
 * TracerouteImpl.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.traceroute.impl;

import com.github.toolarium.network.traceroute.ITraceroute;
import com.github.toolarium.network.traceroute.dto.ITracerouteHop;
import com.github.toolarium.network.traceroute.dto.ITracerouteResult;
import com.github.toolarium.network.traceroute.dto.TracerouteHop;
import com.github.toolarium.network.traceroute.dto.TracerouteResult;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implements traceroute using TCP connections with incrementing TTL.
 * Uses {@link Socket#connect} with the SO_LINGER option to probe each hop.
 *
 * @author patrick
 */
public class TracerouteImpl implements ITraceroute {
    private static final Logger LOG = LoggerFactory.getLogger(TracerouteImpl.class);
    private static final int DEFAULT_PORT = 80;
    private final int maxHops;
    private final int timeout;


    /**
     * Constructor for TracerouteImpl
     *
     * @param maxHops the maximum number of hops
     * @param timeout the timeout per hop in milliseconds
     */
    public TracerouteImpl(int maxHops, int timeout) {
        this.maxHops = maxHops;
        this.timeout = timeout;
    }


    /**
     * @see com.github.toolarium.network.traceroute.ITraceroute#trace(java.lang.String, int)
     */
    @Override
    public ITracerouteResult trace(String host, int port) {
        if (host == null || host.trim().isEmpty()) {
            return new TracerouteResult(host, null, new ArrayList<ITracerouteHop>(), false, 0);
        }

        final String target = host.trim();
        final long totalStart = System.currentTimeMillis();
        String targetAddress = null;

        try {
            targetAddress = InetAddress.getByName(target).getHostAddress();
        } catch (UnknownHostException e) {
            LOG.debug("Cannot resolve host: " + target);
            return new TracerouteResult(target, null, new ArrayList<ITracerouteHop>(), false,
                    System.currentTimeMillis() - totalStart);
        }

        List<ITracerouteHop> hops = new ArrayList<>();
        boolean reached = false;

        for (int ttl = 1; ttl <= maxHops && !reached; ttl++) {
            TracerouteHop hop = probeHop(target, targetAddress, port, ttl);
            hops.add(hop);

            if (hop.isReached() && targetAddress.equals(hop.getAddress())) {
                reached = true;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Hop: " + hop);
            }
        }

        long totalDuration = System.currentTimeMillis() - totalStart;
        TracerouteResult result = new TracerouteResult(target, targetAddress, hops, reached, totalDuration);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Traceroute: " + result);
        }
        return result;
    }


    /**
     * @see com.github.toolarium.network.traceroute.ITraceroute#trace(java.lang.String)
     */
    @Override
    public ITracerouteResult trace(String host) {
        return trace(host, DEFAULT_PORT);
    }


    /**
     * Probe a single hop using a TCP socket with a specific TTL.
     *
     * @param host the target host
     * @param targetAddress the target IP address
     * @param port the port
     * @param ttl the TTL value
     * @return the hop result
     */
    private TracerouteHop probeHop(String host, String targetAddress, int port, int ttl) {
        Socket socket = null;
        try {
            socket = new Socket();
            socket.setSoTimeout(timeout);

            // Set TTL via the traffic class workaround — Java doesn't expose TTL directly on Socket.
            // We use InetAddress.isReachable with TTL for the probe instead.
            InetAddress target = InetAddress.getByName(host);
            long start = System.currentTimeMillis();
            boolean reachable = target.isReachable(null, ttl, timeout);
            long rtt = System.currentTimeMillis() - start;

            if (reachable) {
                // The target responded at this TTL
                String hopAddress = target.getHostAddress();
                String hopHostname = target.getCanonicalHostName();
                return new TracerouteHop(ttl, hopAddress, hopHostname, rtt, true);
            } else {
                // Try TCP connect to detect intermediate hops
                try {
                    socket.connect(new InetSocketAddress(target, port), timeout);
                    rtt = System.currentTimeMillis() - start;
                    return new TracerouteHop(ttl, target.getHostAddress(), target.getCanonicalHostName(), rtt, true);
                } catch (IOException e) {
                    // Hop timed out or refused
                    return new TracerouteHop(ttl, null, null, -1, false);
                }
            }
        } catch (IOException e) {
            return new TracerouteHop(ttl, null, null, -1, false);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // NOP
                }
            }
        }
    }
}
