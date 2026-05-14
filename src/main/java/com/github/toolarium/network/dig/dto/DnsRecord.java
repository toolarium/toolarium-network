/*
 * DnsRecord.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.dig.dto;

import java.io.Serializable;
import java.util.Objects;


/**
 * Implements the {@link IDnsRecord}.
 *
 * @author patrick
 */
public class DnsRecord implements IDnsRecord, Serializable {
    private static final long serialVersionUID = 1L;
    private DnsRecordType type;
    private String value;
    private int priority;


    /**
     * Constructor for DnsRecord
     *
     * @param type the record type
     * @param value the record value
     */
    public DnsRecord(DnsRecordType type, String value) {
        this(type, value, -1);
    }


    /**
     * Constructor for DnsRecord
     *
     * @param type the record type
     * @param value the record value
     * @param priority the priority (for MX/SRV)
     */
    public DnsRecord(DnsRecordType type, String value, int priority) {
        this.type = type;
        this.value = value;
        this.priority = priority;
    }


    /**
     * @see com.github.toolarium.network.dig.dto.IDnsRecord#getType()
     */
    @Override
    public DnsRecordType getType() {
        return type;
    }


    /**
     * @see com.github.toolarium.network.dig.dto.IDnsRecord#getValue()
     */
    @Override
    public String getValue() {
        return value;
    }


    /**
     * @see com.github.toolarium.network.dig.dto.IDnsRecord#getPriority()
     */
    @Override
    public int getPriority() {
        return priority;
    }


    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(type, value, priority);
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
        DnsRecord other = (DnsRecord) obj;
        return type == other.type && Objects.equals(value, other.value) && priority == other.priority;
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type);
        if (priority >= 0) {
            sb.append(" ").append(priority);
        }
        sb.append(" ").append(value);
        return sb.toString();
    }
}
