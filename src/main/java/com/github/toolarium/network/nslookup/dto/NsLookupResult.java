/*
 * NsLookupResult.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.nslookup.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * Implements the {@link INsLookupResult}.
 *
 * @author patrick
 */
public class NsLookupResult implements INsLookupResult, Serializable {
    private static final long serialVersionUID = 1L;
    private String query;
    private String hostname;
    private List<String> addresses;
    private boolean success;
    private long duration;
    private Exception exception;


    /**
     * Constructor for NsLookupResult
     *
     * @param query the query input
     * @param hostname the resolved hostname
     * @param addresses the resolved addresses
     * @param success true if successful
     * @param duration the duration in ms
     * @param exception the exception or null
     */
    public NsLookupResult(String query, String hostname, List<String> addresses, boolean success, long duration, Exception exception) {
        this.query = query;
        this.hostname = hostname;
        if (addresses != null) {
            this.addresses = new ArrayList<>(addresses);
        } else {
            this.addresses = Collections.emptyList();
        }
        this.success = success;
        this.duration = duration;
        this.exception = exception;
    }


    /**
     * @see com.github.toolarium.network.nslookup.dto.INsLookupResult#getQuery()
     */
    @Override
    public String getQuery() {
        return query;
    }


    /**
     * @see com.github.toolarium.network.nslookup.dto.INsLookupResult#getHostname()
     */
    @Override
    public String getHostname() {
        return hostname;
    }


    /**
     * @see com.github.toolarium.network.nslookup.dto.INsLookupResult#getAddresses()
     */
    @Override
    public List<String> getAddresses() {
        return Collections.unmodifiableList(addresses);
    }


    /**
     * @see com.github.toolarium.network.nslookup.dto.INsLookupResult#isSuccess()
     */
    @Override
    public boolean isSuccess() {
        return success;
    }


    /**
     * @see com.github.toolarium.network.nslookup.dto.INsLookupResult#getDuration()
     */
    @Override
    public long getDuration() {
        return duration;
    }


    /**
     * @see com.github.toolarium.network.nslookup.dto.INsLookupResult#getException()
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
        return Objects.hash(query, hostname, addresses, success, duration);
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
        NsLookupResult other = (NsLookupResult) obj;
        return Objects.equals(query, other.query) && Objects.equals(hostname, other.hostname)
                && Objects.equals(addresses, other.addresses) && success == other.success
                && duration == other.duration;
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("NsLookupResult [").append(query);
        if (success) {
            sb.append(" -> ").append(hostname);
            if (!addresses.isEmpty()) {
                sb.append(" ").append(addresses);
            }
            sb.append(" in ").append(duration).append("ms");
        } else {
            sb.append(" FAILED");
            if (exception != null) {
                sb.append(", error=").append(exception.getMessage());
            }
        }
        return sb.append("]").toString();
    }
}
