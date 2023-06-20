/*
 * IPUtil.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.ip;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;


/**
 * IP util
 *  
 * @author patrick
 */
public final class IPUtil {

    /** Regular expression: ipv4 address pattern */
    public static final String IPV4_EXPRESSION = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";

    /** Regular expression: ipv6 address pattern */
    public static final String IPV6_EXPRESSION_STD = "(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}";

    /** Regular expression: ipv6 address pattern */
    public static final String IPV6_EXPRESSION_HEX_COMPRESSED = "((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)";

    /** Regular expression: ipv6 address pattern */
    public static final String IPV6_EXPRESSION = "^((" + IPV6_EXPRESSION_STD + ")|(" + IPV6_EXPRESSION_HEX_COMPRESSED + "))$";

    private Pattern ipv4Expression;
    private Pattern ipv6Expression;


    /**
     * Private class, the only instance of the singleton which will be created by accessing the holder class.
     *
     * @author patrick
     */
    private static class HOLDER {
        static final IPUtil INSTANCE = new IPUtil();
    }

    
    /**
     * Constructor
     */
    private IPUtil() {
        ipv4Expression = Pattern.compile(IPV4_EXPRESSION);
        ipv6Expression = Pattern.compile(IPV6_EXPRESSION);
    }

    
    /**
     * Get the instance
     *
     * @return the instance
     */
    public static IPUtil getInstance() {
        return HOLDER.INSTANCE;
    }


    /**
     * Validate if the host string is instance of ipv4 or ipv6 address
     *
     * @param address the address
     * @return true if it is valid otherwise false
     */
    public boolean isValidAddress(String address) {
        if (address == null || address.trim().length() == 0) {
            return false;
        }

        if (isIPv4Address(address) || isIPv6Address(address)) {
            return  true;
        }

        // try to resolve address
        InetAddress ipAddress = parse(address);
        if (ipAddress != null) {
            String ipAddressStr = ipAddress.getHostAddress();
            if (ipAddressStr != null && (isIPv4Address(ipAddressStr) || isIPv6Address(ipAddressStr))) {
                return true;
            }
        }

        return false;
    }


    /**
     * Validate if the given host is an instance of ipv4
     *
     * @param host the host
     * @return true if it is valid otherwise false
     */
    public boolean isIPv4Address(String host) {
        if (host == null || host.trim().length() == 0) {
            return false;
        }

        return ipv4Expression.matcher(host.trim()).matches();
    }


    /**
     * Validate if the given host is an instance of ipv6 address
     *
     * @param host the host
     * @return true if it is valid otherwise false
     */
    public boolean isIPv6Address(String host) {
        if (host == null || host.trim().length() == 0) {
            return false;
        }

        return ipv6Expression.matcher(host.trim()).matches();
    }


    /**
     * Parse the ip address
     *
     * @param address the address to parse
     * @return the inet addres
     */
    public InetAddress parse(String address) {
        try {
            return InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            return null;
        }
    }
}
