/*
 * NetworkUtil.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;


/**
 * Defines the network utility.
 * 
 * @author patrick
 */
public final class NetworkUtil {

    /**
     * Private class, the only instance of the singelton which will be created by accessing the holder class.
     *
     * @author patrick
     */
    private static final class HOLDER {
        static final NetworkUtil INSTANCE = new NetworkUtil();
    }

    
    /**
     * Constructor
     */
    private NetworkUtil() {
        // NOP
    }

    
    /**
     * Get the instance
     *
     * @return the instance
     */
    public static NetworkUtil getInstance() {
        return HOLDER.INSTANCE;
    }


    /**
     * Get the ip address of the host
     *
     * @return the ip address of the host
     */
    public String getHostIPAddress() {
        try {
            String hostname = InetAddress.getLocalHost().getHostAddress();
            if (hostname != null && hostname.trim().length() > 0) {
                return hostname;
                
            }
            
        } catch (UnknownHostException e) {
            // NOP
        }

        return "0.0.0.0";
    }

    
    /**
     * Get the name of the host
     *
     * @return the name of the host
     */
    public String getHostname() {
        Properties prop = System.getProperties();

        String hostname = sanitizeHostname(prop.getProperty("hostname"));
        if (hostname != null && hostname.length() > 0) {
            return hostname;
        }

        hostname = sanitizeHostname(prop.getProperty("HOSTNAME"));
        if (hostname != null && hostname.length() > 0) {
            return hostname;
        }

        hostname = sanitizeHostname(prop.getProperty("COMPUTERNAME"));
        if (hostname != null && hostname.length() > 0) {
            return hostname;
        }

        try {
            hostname = sanitizeHostname(InetAddress.getLocalHost().getHostName());
            if (hostname != null && hostname.length() > 0) {
                return hostname;
            }
        } catch (UnknownHostException e) {
            // NOP
        }

        return "localhost";
    }


    /**
     * Sanitize a hostname value: keep only valid hostname characters
     * (letters, digits, hyphens, dots, underscores) and limit to 253 characters.
     *
     * @param value the raw value
     * @return the sanitized value, or null if the input is null or blank
     */
    private String sanitizeHostname(String value) {
        if (value == null) {
            return null;
        }
        String sanitized = value.trim().replaceAll("[^a-zA-Z0-9.\\-_]", "");
        if (sanitized.length() > 253) {
            sanitized = sanitized.substring(0, 253);
        }
        if (sanitized.length() > 0) {
            return sanitized;
        }
        return null;
    }
}
