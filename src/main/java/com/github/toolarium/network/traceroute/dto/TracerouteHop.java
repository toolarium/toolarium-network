/*
 * TracerouteHop.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.traceroute.dto;

import java.io.Serializable;
import java.util.Objects;


/**
 * Implements the {@link ITracerouteHop}.
 *
 * @author patrick
 */
public class TracerouteHop implements ITracerouteHop, Serializable {
    private static final long serialVersionUID = 1L;
    private int hopNumber;
    private String address;
    private String hostname;
    private long rtt;
    private boolean reached;


    /**
     * Constructor for TracerouteHop
     *
     * @param hopNumber the hop number
     * @param address the IP address
     * @param hostname the hostname
     * @param rtt the round-trip time in ms
     * @param reached true if responded
     */
    public TracerouteHop(int hopNumber, String address, String hostname, long rtt, boolean reached) {
        this.hopNumber = hopNumber;
        this.address = address;
        this.hostname = hostname;
        this.rtt = rtt;
        this.reached = reached;
    }

    @Override
    public int getHopNumber() {
        return hopNumber;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public String getHostname() {
        return hostname;
    }

    @Override
    public long getRtt() {
        return rtt;
    }

    @Override
    public boolean isReached() {
        return reached;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hopNumber, address, rtt, reached);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TracerouteHop other = (TracerouteHop) obj;
        return hopNumber == other.hopNumber && Objects.equals(address, other.address)
                && rtt == other.rtt && reached == other.reached;
    }

    @Override
    public String toString() {
        if (reached) {
            String displayName;
            if (hostname != null) {
                displayName = hostname;
            } else {
                displayName = address;
            }
            return hopNumber + "  " + displayName + " (" + address + ")  " + rtt + "ms";
        }
        return hopNumber + "  * * *";
    }
}
