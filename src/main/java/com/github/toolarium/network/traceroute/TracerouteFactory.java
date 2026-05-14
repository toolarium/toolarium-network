/*
 * TracerouteFactory.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.traceroute;

import com.github.toolarium.network.traceroute.dto.ITracerouteResult;
import com.github.toolarium.network.traceroute.impl.TracerouteImpl;


/**
 * Factory for creating traceroute instances.
 *
 * @author patrick
 */
public final class TracerouteFactory {
    /**
     * Default max hops.
     */
    static final int DEFAULT_MAX_HOPS = 30;
    /**
     * Default timeout per hop: 2 seconds.
     */
    static final int DEFAULT_TIMEOUT = 2000;

    private static final class HOLDER {
        static final TracerouteFactory INSTANCE = new TracerouteFactory();
    }

    /**
     * Constructor.
     */
    private TracerouteFactory() {
    }

    /**
     * Get the instance.
     *
     * @return the instance
     */
    public static TracerouteFactory getInstance() {
        return HOLDER.INSTANCE;
    }

    /**
     * Get a traceroute instance with custom settings.
     *
     * @param maxHops the maximum number of hops
     * @param timeout the timeout per hop in milliseconds
     * @return the traceroute instance
     */
    public ITraceroute getTraceroute(int maxHops, int timeout) {
        return new TracerouteImpl(maxHops, timeout);
    }

    /**
     * Get a traceroute instance with default settings (30 hops, 2s timeout).
     *
     * @return the traceroute instance
     */
    public ITraceroute getTraceroute() {
        return new TracerouteImpl(DEFAULT_MAX_HOPS, DEFAULT_TIMEOUT);
    }

    /**
     * Convenience: trace route to a host on a port with default settings.
     *
     * @param host the target host
     * @param port the TCP port
     * @return the traceroute result
     */
    public ITracerouteResult trace(String host, int port) {
        return getTraceroute().trace(host, port);
    }

    /**
     * Convenience: trace route to a host with default settings and port 80.
     *
     * @param host the target host
     * @return the traceroute result
     */
    public ITracerouteResult trace(String host) {
        return getTraceroute().trace(host);
    }
}
