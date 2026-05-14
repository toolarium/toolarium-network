/*
 * IPingResult.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.ping.dto;


/**
 * Defines the result of a TCP ping operation.
 *
 * @author patrick
 */
public interface IPingResult {

    /**
     * Get the target host.
     *
     * @return the host
     */
    String getHost();


    /**
     * Get the target port.
     *
     * @return the port
     */
    int getPort();


    /**
     * Check if the target was reachable.
     *
     * @return true if the TCP connection was established successfully
     */
    boolean isReachable();


    /**
     * Get the connection duration in milliseconds.
     * Returns -1 if the target was not reachable.
     *
     * @return the duration in milliseconds
     */
    long getDuration();


    /**
     * Get the exception if the ping failed.
     *
     * @return the exception or null if successful
     */
    Exception getException();
}
