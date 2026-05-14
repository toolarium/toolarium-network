/*
 * IWakeOnLan.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.wol;

import com.github.toolarium.network.wol.dto.IWakeOnLanResult;


/**
 * Defines the Wake-on-LAN interface for sending magic packets.
 *
 * @author patrick
 */
public interface IWakeOnLan {

    /**
     * Send a Wake-on-LAN magic packet to the given MAC address
     * using the subnet broadcast address.
     *
     * @param macAddress the MAC address (e.g. "AA:BB:CC:DD:EE:FF" or "AA-BB-CC-DD-EE-FF")
     * @return the result
     */
    IWakeOnLanResult wake(String macAddress);


    /**
     * Send a Wake-on-LAN magic packet to the given MAC address
     * using a specific broadcast address.
     *
     * @param macAddress the MAC address
     * @param broadcastAddress the broadcast address (e.g. "192.168.1.255")
     * @return the result
     */
    IWakeOnLanResult wake(String macAddress, String broadcastAddress);
}
