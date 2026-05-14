/*
 * PingFactory.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.ping;

import com.github.toolarium.network.ping.dto.IPingResult;
import com.github.toolarium.network.ping.impl.TcpPingImpl;
import java.util.List;


/**
 * Factory for creating TCP ping instances.
 *
 * @author patrick
 */
public final class PingFactory {
    /**
     * Default ping timeout: 3 seconds.
     */
    static final int DEFAULT_TIMEOUT = 3000;


    /**
     * Private class, the only instance of the singleton which will be created by accessing the holder class.
     *
     * @author patrick
     */
    private static final class HOLDER {
        static final PingFactory INSTANCE = new PingFactory();
    }


    /**
     * Constructor
     */
    private PingFactory() {
        // NOP
    }


    /**
     * Get the instance
     *
     * @return the instance
     */
    public static PingFactory getInstance() {
        return HOLDER.INSTANCE;
    }


    /**
     * Get a ping instance with the specified timeout.
     *
     * @param timeout the connection timeout in milliseconds
     * @return the ping instance
     */
    public IPing getPing(int timeout) {
        return new TcpPingImpl(timeout);
    }


    /**
     * Get a ping instance with the default timeout (3 seconds).
     *
     * @return the ping instance
     */
    public IPing getPing() {
        return new TcpPingImpl(DEFAULT_TIMEOUT);
    }


    /**
     * Convenience method: ping a single host on the given port with default timeout.
     *
     * @param host the host to ping
     * @param port the TCP port
     * @return the ping result
     */
    public IPingResult ping(String host, int port) {
        return getPing().ping(host, port);
    }


    /**
     * Convenience method: ping multiple hosts on the given port with default timeout.
     *
     * @param port the TCP port
     * @param hosts the hosts to ping
     * @return the list of ping results
     */
    public List<IPingResult> ping(int port, String... hosts) {
        return getPing().ping(port, hosts);
    }
}
