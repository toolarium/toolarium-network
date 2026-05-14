/*
 * DnsRecordType.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.dig.dto;


/**
 * Defines DNS record types that can be queried.
 *
 * @author patrick
 */
public enum DnsRecordType {
    /** IPv4 address record */
    A,
    /** IPv6 address record */
    AAAA,
    /** Mail exchange record */
    MX,
    /** Canonical name (alias) record */
    CNAME,
    /** Text record */
    TXT,
    /** Name server record */
    NS,
    /** Start of authority record */
    SOA,
    /** Pointer record (reverse DNS) */
    PTR,
    /** Service locator record */
    SRV
}
