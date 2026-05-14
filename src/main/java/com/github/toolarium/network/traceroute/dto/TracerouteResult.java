/*
 * TracerouteResult.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.traceroute.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * Implements the {@link ITracerouteResult}.
 *
 * @author patrick
 */
public class TracerouteResult implements ITracerouteResult, Serializable {
    private static final long serialVersionUID = 1L;
    private String target;
    private String targetAddress;
    private List<ITracerouteHop> hops;
    private boolean targetReached;
    private long duration;


    /**
     * Constructor for TracerouteResult
     *
     * @param target the target host
     * @param targetAddress the resolved IP
     * @param hops the hops
     * @param targetReached true if destination reached
     * @param duration the total duration
     */
    public TracerouteResult(String target, String targetAddress, List<ITracerouteHop> hops, boolean targetReached, long duration) {
        this.target = target;
        this.targetAddress = targetAddress;
        if (hops != null) {
            this.hops = new ArrayList<>(hops);
        } else {
            this.hops = Collections.emptyList();
        }
        this.targetReached = targetReached;
        this.duration = duration;
    }

    @Override
    public String getTarget() {
        return target;
    }

    @Override
    public String getTargetAddress() {
        return targetAddress;
    }

    @Override
    public List<ITracerouteHop> getHops() {
        return Collections.unmodifiableList(hops);
    }

    @Override
    public boolean isTargetReached() {
        return targetReached;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, targetAddress, hops, targetReached, duration);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TracerouteResult other = (TracerouteResult) obj;
        return Objects.equals(target, other.target) && Objects.equals(targetAddress, other.targetAddress)
                && Objects.equals(hops, other.hops) && targetReached == other.targetReached && duration == other.duration;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TracerouteResult [").append(target);
        if (targetAddress != null) {
            sb.append(" (").append(targetAddress).append(")");
        }
        sb.append(", ").append(hops.size()).append(" hops");
        if (targetReached) {
            sb.append(", reached");
        } else {
            sb.append(", not reached");
        }
        sb.append(" in ").append(duration).append("ms]");
        return sb.toString();
    }
}
