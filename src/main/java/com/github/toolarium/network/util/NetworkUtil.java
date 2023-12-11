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
    private static class HOLDER {
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

        String hostname = prop.getProperty("hostname");
        if (hostname != null && hostname.trim().length() > 0) {
            return hostname;
        }

        hostname = prop.getProperty("HOSTNAME");
        if (hostname != null && hostname.trim().length() > 0) {
            return hostname;
        }

        hostname = prop.getProperty("COMPUTERNAME");
        if (hostname != null && hostname.trim().length() > 0) {
            return hostname;
        }

        try {
            hostname = InetAddress.getLocalHost().getHostName();
            if (hostname != null && hostname.trim().length() > 0) {
                return hostname;
            }
        } catch (UnknownHostException e) {
            // NOP
        }

        return "localhost";
    }
}
