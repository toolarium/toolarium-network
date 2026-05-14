/*
 * ITracerouteResult.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.traceroute.dto;

import java.util.List;


/**
 * Defines the result of a traceroute operation.
 *
 * @author patrick
 */
public interface ITracerouteResult {

    /**
     * Get the target host.
     *
     * @return the target host
     */
    String getTarget();


    /**
     * Get the resolved target IP address.
     *
     * @return the target IP or null
     */
    String getTargetAddress();


    /**
     * Get the list of hops.
     *
     * @return the hops
     */
    List<ITracerouteHop> getHops();


    /**
     * Check if the target was reached.
     *
     * @return true if the final destination was reached
     */
    boolean isTargetReached();


    /**
     * Get the total duration in milliseconds.
     *
     * @return the duration
     */
    long getDuration();
}
