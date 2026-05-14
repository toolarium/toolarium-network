/*
 * INetworkInterfaceInfo.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.iface.dto;

import java.util.List;


/**
 * Defines information about a network interface.
 *
 * @author patrick
 */
public interface INetworkInterfaceInfo {

    /**
     * Get the interface name (e.g. "eth0", "wlan0").
     *
     * @return the name
     */
    String getName();


    /**
     * Get the display name.
     *
     * @return the display name
     */
    String getDisplayName();


    /**
     * Get the hardware (MAC) address as formatted string.
     *
     * @return the MAC address or null if not available
     */
    String getMacAddress();


    /**
     * Get the list of IP addresses assigned to this interface.
     *
     * @return the IP addresses
     */
    List<String> getIpAddresses();


    /**
     * Get the MTU (Maximum Transmission Unit).
     *
     * @return the MTU or -1 if unknown
     */
    int getMtu();


    /**
     * Check if the interface is up.
     *
     * @return true if up
     */
    boolean isUp();


    /**
     * Check if this is a loopback interface.
     *
     * @return true if loopback
     */
    boolean isLoopback();


    /**
     * Check if this is a virtual interface.
     *
     * @return true if virtual
     */
    boolean isVirtual();
}
