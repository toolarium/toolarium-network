/*
 * IWakeOnLanResult.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.wol.dto;


/**
 * Defines the result of a Wake-on-LAN magic packet send.
 *
 * @author patrick
 */
public interface IWakeOnLanResult {

    /**
     * Get the target MAC address.
     *
     * @return the MAC address
     */
    String getMacAddress();


    /**
     * Get the broadcast address used.
     *
     * @return the broadcast address
     */
    String getBroadcastAddress();


    /**
     * Get the port used.
     *
     * @return the port
     */
    int getPort();


    /**
     * Check if the magic packet was sent successfully.
     *
     * @return true if sent
     */
    boolean isSuccess();


    /**
     * Get the exception if sending failed.
     *
     * @return the exception or null
     */
    Exception getException();
}
