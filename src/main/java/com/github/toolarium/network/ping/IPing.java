/*
 * IPing.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.ping;

import com.github.toolarium.network.ping.dto.IPingResult;
import java.util.List;


/**
 * Defines the TCP ping interface for measuring network reachability and latency
 * via TCP connection establishment time.
 *
 * @author patrick
 */
public interface IPing {

    /**
     * Ping a single host on the given port.
     *
     * @param host the host to ping
     * @param port the TCP port
     * @return the ping result
     */
    IPingResult ping(String host, int port);


    /**
     * Ping multiple hosts in parallel on the given port.
     *
     * @param port the TCP port
     * @param hosts the hosts to ping
     * @return the list of ping results
     */
    List<IPingResult> ping(int port, String... hosts);


    /**
     * Ping multiple host:port targets in parallel.
     * Each target can be specified as "host" (uses default port) or "host:port".
     * IPv6 addresses with port use bracket notation: "[::1]:port".
     *
     * @param defaultPort the default port if not specified per target
     * @param targets the targets to ping
     * @return the list of ping results
     */
    List<IPingResult> pingTargets(int defaultPort, String... targets);
}
