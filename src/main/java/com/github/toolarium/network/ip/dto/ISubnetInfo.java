/*
 * ISubnetInfo.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.ip.dto;


/**
 * Defines detailed subnet information calculated from a CIDR expression.
 *
 * @author patrick
 */
public interface ISubnetInfo {

    /**
     * Get the CIDR notation (e.g. "192.168.1.0/24").
     *
     * @return the CIDR notation
     */
    String getCidr();


    /**
     * Get the network address.
     *
     * @return the network address
     */
    String getNetworkAddress();


    /**
     * Get the broadcast address (IPv4 only, null for IPv6).
     *
     * @return the broadcast address
     */
    String getBroadcastAddress();


    /**
     * Get the first usable host address.
     *
     * @return the first usable address
     */
    String getFirstUsableAddress();


    /**
     * Get the last usable host address.
     *
     * @return the last usable address
     */
    String getLastUsableAddress();


    /**
     * Get the subnet mask in dotted notation (IPv4 only, null for IPv6).
     *
     * @return the subnet mask
     */
    String getSubnetMask();


    /**
     * Get the prefix length (e.g. 24 for /24).
     *
     * @return the prefix length
     */
    int getPrefixLength();


    /**
     * Get the total number of addresses in the subnet.
     *
     * @return the total address count
     */
    long getTotalAddresses();


    /**
     * Get the number of usable host addresses (total minus network and broadcast for IPv4).
     *
     * @return the usable host count
     */
    long getUsableHostCount();


    /**
     * Check if this is an IPv6 subnet.
     *
     * @return true if IPv6
     */
    boolean isIPv6();
}
