/*
 * DigImpl.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.dig.impl;

import com.github.toolarium.network.dig.IDig;
import com.github.toolarium.network.dig.dto.DigResult;
import com.github.toolarium.network.dig.dto.DnsRecord;
import com.github.toolarium.network.dig.dto.DnsRecordType;
import com.github.toolarium.network.dig.dto.IDigResult;
import com.github.toolarium.network.dig.dto.IDnsRecord;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implements DNS dig using JNDI {@link DirContext} for querying specific DNS record types.
 *
 * @author patrick
 */
public class DigImpl implements IDig {
    private static final Logger LOG = LoggerFactory.getLogger(DigImpl.class);
    private static final DnsRecordType[] ALL_COMMON_TYPES = {
        DnsRecordType.A, DnsRecordType.AAAA, DnsRecordType.MX,
        DnsRecordType.CNAME, DnsRecordType.TXT, DnsRecordType.NS
    };

    private final int timeout;
    private final String dnsServer;


    /**
     * Constructor for DigImpl using system default DNS.
     *
     * @param timeout the query timeout in milliseconds
     */
    public DigImpl(int timeout) {
        this(timeout, null);
    }


    /**
     * Constructor for DigImpl with a specific DNS server.
     *
     * @param timeout the query timeout in milliseconds
     * @param dnsServer the DNS server address (e.g. "8.8.8.8"), or null for system default
     */
    public DigImpl(int timeout, String dnsServer) {
        this.timeout = timeout;
        this.dnsServer = dnsServer;
    }


    /**
     * @see com.github.toolarium.network.dig.IDig#dig(java.lang.String, com.github.toolarium.network.dig.dto.DnsRecordType)
     */
    @Override
    public IDigResult dig(String hostname, DnsRecordType recordType) {
        if (hostname == null || hostname.trim().isEmpty()) {
            return new DigResult(hostname, recordType, Collections.emptyList(), false, 0,
                    new IllegalArgumentException("Hostname is null or empty"));
        }
        if (recordType == null) {
            return new DigResult(hostname, null, Collections.emptyList(), false, 0,
                    new IllegalArgumentException("Record type is null"));
        }

        final String host = hostname.trim();
        final long start = System.currentTimeMillis();

        try {
            DirContext ctx = createDirContext();
            try {
                Attributes attrs = ctx.getAttributes(host, new String[]{recordType.name()});
                List<IDnsRecord> records = parseRecords(attrs, recordType);
                long duration = System.currentTimeMillis() - start;

                DigResult result = new DigResult(host, recordType, records, true, duration, null);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Dig: " + result);
                }
                return result;
            } finally {
                ctx.close();
            }
        } catch (NamingException e) {
            long duration = System.currentTimeMillis() - start;
            // No records found is not an error — return success with empty records
            if (isNoRecordsException(e)) {
                DigResult result = new DigResult(host, recordType, Collections.emptyList(), true, duration, null);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Dig (no records): " + result);
                }
                return result;
            }

            DigResult result = new DigResult(host, recordType, Collections.emptyList(), false, duration, e);
            LOG.debug("Dig failed for " + host + " " + recordType + ": " + e.getMessage());
            return result;
        }
    }


    /**
     * @see com.github.toolarium.network.dig.IDig#dig(java.lang.String, com.github.toolarium.network.dig.dto.DnsRecordType[])
     */
    @Override
    public List<IDigResult> dig(String hostname, DnsRecordType... recordTypes) {
        if (recordTypes == null || recordTypes.length == 0) {
            return Collections.emptyList();
        }

        List<IDigResult> results = new ArrayList<>();
        for (DnsRecordType type : recordTypes) {
            results.add(dig(hostname, type));
        }
        return results;
    }


    /**
     * @see com.github.toolarium.network.dig.IDig#digAll(java.lang.String)
     */
    @Override
    public List<IDigResult> digAll(String hostname) {
        return dig(hostname, ALL_COMMON_TYPES);
    }


    /**
     * Create the JNDI DirContext for DNS queries.
     *
     * @return the dir context
     * @throws NamingException In case of an error
     */
    private DirContext createDirContext() throws NamingException {
        Hashtable<String, String> env = new Hashtable<>();
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        env.put("com.sun.jndi.dns.timeout.initial", String.valueOf(timeout));
        env.put("com.sun.jndi.dns.timeout.retries", "1");

        if (dnsServer != null && !dnsServer.trim().isEmpty()) {
            env.put("java.naming.provider.url", "dns://" + dnsServer.trim());
        }

        return new InitialDirContext(env);
    }


    /**
     * Parse DNS attributes into record list.
     *
     * @param attrs the JNDI attributes
     * @param recordType the record type
     * @return the parsed records
     * @throws NamingException In case of an error
     */
    private List<IDnsRecord> parseRecords(Attributes attrs, DnsRecordType recordType) throws NamingException {
        List<IDnsRecord> records = new ArrayList<>();
        Attribute attr = attrs.get(recordType.name());

        if (attr == null) {
            return records;
        }

        NamingEnumeration<?> values = attr.getAll();
        while (values.hasMore()) {
            String rawValue = values.next().toString().trim();
            records.add(parseRecord(recordType, rawValue));
        }

        return records;
    }


    /**
     * Parse a single DNS record value.
     *
     * @param type the record type
     * @param rawValue the raw value string
     * @return the parsed DNS record
     */
    private IDnsRecord parseRecord(DnsRecordType type, String rawValue) {
        if (type == DnsRecordType.MX) {
            return parseMxRecord(rawValue);
        }
        if (type == DnsRecordType.SRV) {
            return parseSrvRecord(rawValue);
        }
        return new DnsRecord(type, rawValue);
    }


    /**
     * Parse MX record: "10 mail.example.com."
     *
     * @param rawValue the raw value
     * @return the parsed record
     */
    private IDnsRecord parseMxRecord(String rawValue) {
        String[] parts = rawValue.split("\\s+", 2);
        if (parts.length == 2) {
            try {
                int priority = Integer.parseInt(parts[0]);
                String host = stripTrailingDot(parts[1]);
                return new DnsRecord(DnsRecordType.MX, host, priority);
            } catch (NumberFormatException e) {
                // fall through
            }
        }
        return new DnsRecord(DnsRecordType.MX, stripTrailingDot(rawValue));
    }


    /**
     * Parse SRV record: "10 5 5060 sip.example.com."
     *
     * @param rawValue the raw value
     * @return the parsed record
     */
    private IDnsRecord parseSrvRecord(String rawValue) {
        String[] parts = rawValue.split("\\s+", 4);
        if (parts.length >= 4) {
            try {
                int priority = Integer.parseInt(parts[0]);
                String target = stripTrailingDot(parts[3]);
                return new DnsRecord(DnsRecordType.SRV, parts[1] + " " + parts[2] + " " + target, priority);
            } catch (NumberFormatException e) {
                // fall through
            }
        }
        return new DnsRecord(DnsRecordType.SRV, rawValue);
    }


    /**
     * Strip trailing dot from DNS names (e.g. "example.com." -> "example.com").
     *
     * @param value the value
     * @return the cleaned value
     */
    private String stripTrailingDot(String value) {
        if (value != null && value.endsWith(".")) {
            return value.substring(0, value.length() - 1);
        }
        return value;
    }


    /**
     * Check if the exception indicates no records found (not an actual error).
     *
     * @param e the naming exception
     * @return true if it's a "no records" situation
     */
    private boolean isNoRecordsException(NamingException e) {
        String msg = e.getMessage();
        return msg != null && (msg.contains("DNS name not found") || msg.contains("No records"));
    }
}
