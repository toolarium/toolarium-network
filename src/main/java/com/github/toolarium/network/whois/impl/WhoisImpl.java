/*
 * WhoisImpl.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.whois.impl;

import com.github.toolarium.network.whois.IWhois;
import com.github.toolarium.network.whois.dto.IWhoisResult;
import com.github.toolarium.network.whois.dto.WhoisResult;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implements WHOIS lookups via TCP connection to WHOIS servers on port 43.
 *
 * @author patrick
 */
public class WhoisImpl implements IWhois {
    private static final Logger LOG = LoggerFactory.getLogger(WhoisImpl.class);
    private static final int WHOIS_PORT = 43;
    private static final String DEFAULT_WHOIS_SERVER = "whois.iana.org";
    private final int timeout;


    /**
     * Constructor for WhoisImpl
     *
     * @param timeout the connection/read timeout in milliseconds
     */
    public WhoisImpl(int timeout) {
        this.timeout = timeout;
    }


    /**
     * @see com.github.toolarium.network.whois.IWhois#query(java.lang.String)
     */
    @Override
    public IWhoisResult query(String query) {
        return query(query, DEFAULT_WHOIS_SERVER);
    }


    /**
     * @see com.github.toolarium.network.whois.IWhois#query(java.lang.String, java.lang.String)
     */
    @Override
    public IWhoisResult query(String query, String whoisServer) {
        if (query == null || query.trim().isEmpty()) {
            return new WhoisResult(query, whoisServer, null, Collections.emptyMap(), false, 0,
                    new IllegalArgumentException("Query is null or empty"));
        }

        String server;
        if (whoisServer != null && !whoisServer.trim().isEmpty()) {
            server = whoisServer.trim();
        } else {
            server = DEFAULT_WHOIS_SERVER;
        }
        String q = query.trim();
        long start = System.currentTimeMillis();

        try {
            String rawResponse = executeWhoisQuery(q, server);
            long duration = System.currentTimeMillis() - start;

            Map<String, String> fields = parseFields(rawResponse);

            // Check for referral to another WHOIS server
            String referral = findReferral(fields);
            if (referral != null && !referral.equalsIgnoreCase(server)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Following WHOIS referral from " + server + " to " + referral);
                }
                rawResponse = executeWhoisQuery(q, referral);
                duration = System.currentTimeMillis() - start;
                fields = parseFields(rawResponse);
                server = referral;
            }

            WhoisResult result = new WhoisResult(q, server, rawResponse, fields, true, duration, null);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Whois: " + result);
            }
            return result;

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            LOG.debug("Whois failed for " + q + ": " + e.getMessage());
            return new WhoisResult(q, server, null, Collections.emptyMap(), false, duration, e);
        }
    }


    /**
     * Execute a raw WHOIS query.
     *
     * @param query the query
     * @param server the WHOIS server
     * @return the raw response
     * @throws IOException In case of an I/O error
     */
    private String executeWhoisQuery(String query, String server) throws IOException {
        Socket socket = new Socket();
        try {
            socket.setSoTimeout(timeout);
            socket.connect(new java.net.InetSocketAddress(server, WHOIS_PORT), timeout);

            OutputStream out = socket.getOutputStream();
            out.write((query + "\r\n").getBytes(StandardCharsets.UTF_8));
            out.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }

            return response.toString();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                // NOP
            }
        }
    }


    /**
     * Parse key-value fields from WHOIS response.
     *
     * @param rawResponse the raw response
     * @return the parsed fields
     */
    static Map<String, String> parseFields(String rawResponse) {
        if (rawResponse == null || rawResponse.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, String> fields = new LinkedHashMap<>();
        String[] lines = rawResponse.split("\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("%") || trimmed.startsWith("#") || trimmed.startsWith(">")) {
                continue;
            }
            int idx = trimmed.indexOf(':');
            if (idx > 0 && idx < trimmed.length() - 1) {
                String key = trimmed.substring(0, idx).trim();
                String value = trimmed.substring(idx + 1).trim();
                if (!key.isEmpty() && !value.isEmpty()) {
                    // Keep first occurrence
                    if (!fields.containsKey(key)) {
                        fields.put(key, value);
                    }
                }
            }
        }
        return fields;
    }


    /**
     * Find a referral WHOIS server in the parsed fields.
     *
     * @param fields the parsed fields
     * @return the referral server or null
     */
    private String findReferral(Map<String, String> fields) {
        String referral = fields.get("refer");
        if (referral == null) {
            referral = fields.get("whois");
        }
        if (referral == null) {
            referral = fields.get("ReferralServer");
        }
        // Clean up URL format: "whois://whois.verisign-grs.com" -> "whois.verisign-grs.com"
        if (referral != null && referral.contains("://")) {
            referral = referral.substring(referral.indexOf("://") + 3);
        }
        return referral;
    }
}
