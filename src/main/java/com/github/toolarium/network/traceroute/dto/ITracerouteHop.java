/*
 * ITracerouteHop.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.traceroute.dto;


/**
 * Defines a single hop in a traceroute path.
 *
 * @author patrick
 */
public interface ITracerouteHop {

    /**
     * Get the hop number (1-based TTL).
     *
     * @return the hop number
     */
    int getHopNumber();


    /**
     * Get the IP address of the hop.
     *
     * @return the IP address or null if the hop timed out
     */
    String getAddress();


    /**
     * Get the hostname of the hop.
     *
     * @return the hostname or null if unknown
     */
    String getHostname();


    /**
     * Get the round-trip time in milliseconds.
     *
     * @return the RTT in ms, or -1 if timed out
     */
    long getRtt();


    /**
     * Check if this hop responded.
     *
     * @return true if the hop responded
     */
    boolean isReached();
}
