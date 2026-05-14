/*
 * PingResult.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.ping.dto;

import java.io.Serializable;
import java.util.Objects;


/**
 * Implements the {@link IPingResult}.
 *
 * @author patrick
 */
public class PingResult implements IPingResult, Serializable {
    private static final long serialVersionUID = 1L;
    private String host;
    private int port;
    private boolean reachable;
    private long duration;
    private Exception exception;


    /**
     * Constructor for PingResult
     *
     * @param host the target host
     * @param port the target port
     * @param reachable true if reachable
     * @param duration the duration in ms (-1 if not reachable)
     * @param exception the exception or null
     */
    public PingResult(String host, int port, boolean reachable, long duration, Exception exception) {
        this.host = host;
        this.port = port;
        this.reachable = reachable;
        this.duration = duration;
        this.exception = exception;
    }


    /**
     * @see com.github.toolarium.network.ping.dto.IPingResult#getHost()
     */
    @Override
    public String getHost() {
        return host;
    }


    /**
     * @see com.github.toolarium.network.ping.dto.IPingResult#getPort()
     */
    @Override
    public int getPort() {
        return port;
    }


    /**
     * @see com.github.toolarium.network.ping.dto.IPingResult#isReachable()
     */
    @Override
    public boolean isReachable() {
        return reachable;
    }


    /**
     * @see com.github.toolarium.network.ping.dto.IPingResult#getDuration()
     */
    @Override
    public long getDuration() {
        return duration;
    }


    /**
     * @see com.github.toolarium.network.ping.dto.IPingResult#getException()
     */
    @Override
    public Exception getException() {
        return exception;
    }


    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(host, port, reachable, duration);
    }


    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PingResult other = (PingResult) obj;
        return Objects.equals(host, other.host) && port == other.port
                && reachable == other.reachable && duration == other.duration;
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (reachable) {
            return "PingResult [" + host + ":" + port + " reachable in " + duration + "ms]";
        }
        StringBuilder sb = new StringBuilder("PingResult [");
        sb.append(host).append(":").append(port).append(" unreachable");
        if (exception != null) {
            sb.append(", error=").append(exception.getMessage());
        }
        sb.append("]");
        return sb.toString();
    }
}
