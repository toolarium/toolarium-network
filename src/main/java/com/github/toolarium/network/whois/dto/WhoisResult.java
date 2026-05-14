/*
 * WhoisResult.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.whois.dto;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;


/**
 * Implements the {@link IWhoisResult}.
 *
 * @author patrick
 */
public class WhoisResult implements IWhoisResult, Serializable {
    private static final long serialVersionUID = 1L;
    private String query;
    private String whoisServer;
    private String rawResponse;
    private Map<String, String> fields;
    private boolean success;
    private long duration;
    private Exception exception;


    /**
     * Constructor for WhoisResult
     *
     * @param query the query
     * @param whoisServer the whois server
     * @param rawResponse the raw response
     * @param fields the parsed fields
     * @param success true if successful
     * @param duration the duration
     * @param exception the exception or null
     */
    public WhoisResult(String query, String whoisServer, String rawResponse, Map<String, String> fields,
                       boolean success, long duration, Exception exception) {
        this.query = query;
        this.whoisServer = whoisServer;
        this.rawResponse = rawResponse;
        if (fields != null) {
            this.fields = new LinkedHashMap<>(fields);
        } else {
            this.fields = Collections.emptyMap();
        }
        this.success = success;
        this.duration = duration;
        this.exception = exception;
    }

    @Override
    public String getQuery() {
        return query;
    }

    @Override
    public String getWhoisServer() {
        return whoisServer;
    }

    @Override
    public String getRawResponse() {
        return rawResponse;
    }

    @Override
    public Map<String, String> getFields() {
        return Collections.unmodifiableMap(fields);
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public Exception getException() {
        return exception;
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, whoisServer, success, duration);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        WhoisResult other = (WhoisResult) obj;
        return Objects.equals(query, other.query) && Objects.equals(whoisServer, other.whoisServer)
                && success == other.success && duration == other.duration;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("WhoisResult [").append(query);
        if (success) {
            sb.append(" via ").append(whoisServer);
            sb.append(", ").append(fields.size()).append(" fields");
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
