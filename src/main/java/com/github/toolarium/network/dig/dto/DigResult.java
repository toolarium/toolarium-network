/*
 * DigResult.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.dig.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * Implements the {@link IDigResult}.
 *
 * @author patrick
 */
public class DigResult implements IDigResult, Serializable {
    private static final long serialVersionUID = 1L;
    private String query;
    private DnsRecordType recordType;
    private List<IDnsRecord> records;
    private boolean success;
    private long duration;
    private Exception exception;


    /**
     * Constructor for DigResult
     *
     * @param query the query hostname
     * @param recordType the queried record type
     * @param records the resolved records
     * @param success true if successful
     * @param duration the duration in ms
     * @param exception the exception or null
     */
    public DigResult(String query, DnsRecordType recordType, List<IDnsRecord> records, boolean success, long duration, Exception exception) {
        this.query = query;
        this.recordType = recordType;
        if (records != null) {
            this.records = new ArrayList<>(records);
        } else {
            this.records = Collections.emptyList();
        }
        this.success = success;
        this.duration = duration;
        this.exception = exception;
    }


    /**
     * @see com.github.toolarium.network.dig.dto.IDigResult#getQuery()
     */
    @Override
    public String getQuery() {
        return query;
    }


    /**
     * @see com.github.toolarium.network.dig.dto.IDigResult#getRecordType()
     */
    @Override
    public DnsRecordType getRecordType() {
        return recordType;
    }


    /**
     * @see com.github.toolarium.network.dig.dto.IDigResult#getRecords()
     */
    @Override
    public List<IDnsRecord> getRecords() {
        return Collections.unmodifiableList(records);
    }


    /**
     * @see com.github.toolarium.network.dig.dto.IDigResult#isSuccess()
     */
    @Override
    public boolean isSuccess() {
        return success;
    }


    /**
     * @see com.github.toolarium.network.dig.dto.IDigResult#getDuration()
     */
    @Override
    public long getDuration() {
        return duration;
    }


    /**
     * @see com.github.toolarium.network.dig.dto.IDigResult#getException()
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
        return Objects.hash(query, recordType, records, success, duration);
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
        DigResult other = (DigResult) obj;
        return Objects.equals(query, other.query) && recordType == other.recordType
                && Objects.equals(records, other.records) && success == other.success
                && duration == other.duration;
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("DigResult [").append(query).append(" ").append(recordType);
        if (success) {
            sb.append(" -> ").append(records);
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
