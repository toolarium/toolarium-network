/*
 * ITraceroute.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.traceroute;

import com.github.toolarium.network.traceroute.dto.ITracerouteResult;


/**
 * Defines the traceroute interface for tracing the path to a network host.
 *
 * @author patrick
 */
public interface ITraceroute {

    /**
     * Trace the route to a host on a specific port.
     *
     * @param host the target host
     * @param port the TCP port to probe
     * @return the traceroute result
     */
    ITracerouteResult trace(String host, int port);


    /**
     * Trace the route to a host using the default port (80).
     *
     * @param host the target host
     * @return the traceroute result
     */
    ITracerouteResult trace(String host);
}
