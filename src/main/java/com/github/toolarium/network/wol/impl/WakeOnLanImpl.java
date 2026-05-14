/*
 * WakeOnLanImpl.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.wol.impl;

import com.github.toolarium.network.wol.IWakeOnLan;
import com.github.toolarium.network.wol.dto.IWakeOnLanResult;
import com.github.toolarium.network.wol.dto.WakeOnLanResult;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implements Wake-on-LAN by sending a UDP magic packet.
 * The magic packet consists of 6 bytes of 0xFF followed by the MAC address repeated 16 times.
 *
 * @author patrick
 */
public class WakeOnLanImpl implements IWakeOnLan {
    private static final Logger LOG = LoggerFactory.getLogger(WakeOnLanImpl.class);
    private static final int WOL_PORT = 9;
    private static final String DEFAULT_BROADCAST = "255.255.255.255";


    /**
     * @see com.github.toolarium.network.wol.IWakeOnLan#wake(java.lang.String)
     */
    @Override
    public IWakeOnLanResult wake(String macAddress) {
        return wake(macAddress, DEFAULT_BROADCAST);
    }


    /**
     * @see com.github.toolarium.network.wol.IWakeOnLan#wake(java.lang.String, java.lang.String)
     */
    @Override
    public IWakeOnLanResult wake(String macAddress, String broadcastAddress) {
        if (macAddress == null || macAddress.trim().isEmpty()) {
            return new WakeOnLanResult(macAddress, broadcastAddress, WOL_PORT, false,
                    new IllegalArgumentException("MAC address is null or empty"));
        }

        try {
            byte[] macBytes = parseMacAddress(macAddress.trim());
            byte[] magicPacket = buildMagicPacket(macBytes);

            String broadcast;
            if (broadcastAddress != null && !broadcastAddress.trim().isEmpty()) {
                broadcast = broadcastAddress.trim();
            } else {
                broadcast = DEFAULT_BROADCAST;
            }

            InetAddress address = InetAddress.getByName(broadcast);
            DatagramPacket packet = new DatagramPacket(magicPacket, magicPacket.length, address, WOL_PORT);

            DatagramSocket socket = new DatagramSocket();
            try {
                socket.setBroadcast(true);
                socket.send(packet);
            } finally {
                socket.close();
            }

            WakeOnLanResult result = new WakeOnLanResult(macAddress.trim(), broadcast, WOL_PORT, true, null);
            if (LOG.isDebugEnabled()) {
                LOG.debug("WOL: " + result);
            }
            return result;

        } catch (Exception e) {
            WakeOnLanResult result = new WakeOnLanResult(macAddress, broadcastAddress, WOL_PORT, false, e);
            LOG.debug("WOL failed for " + macAddress + ": " + e.getMessage());
            return result;
        }
    }


    /**
     * Parse a MAC address string into bytes.
     *
     * @param macAddress the MAC address (AA:BB:CC:DD:EE:FF or AA-BB-CC-DD-EE-FF)
     * @return the 6-byte MAC address
     * @throws IllegalArgumentException if the format is invalid
     */
    static byte[] parseMacAddress(String macAddress) {
        String[] parts = macAddress.split("[:\\-]");
        if (parts.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address format: " + macAddress);
        }

        byte[] bytes = new byte[6];
        for (int i = 0; i < 6; i++) {
            bytes[i] = (byte) Integer.parseInt(parts[i], 16);
        }
        return bytes;
    }


    /**
     * Build the 102-byte magic packet: 6 bytes of 0xFF + MAC repeated 16 times.
     *
     * @param macBytes the 6-byte MAC address
     * @return the magic packet
     */
    static byte[] buildMagicPacket(byte[] macBytes) {
        byte[] packet = new byte[6 + 16 * 6];

        // 6 bytes of 0xFF
        for (int i = 0; i < 6; i++) {
            packet[i] = (byte) 0xFF;
        }

        // MAC repeated 16 times
        for (int i = 0; i < 16; i++) {
            System.arraycopy(macBytes, 0, packet, 6 + i * 6, 6);
        }

        return packet;
    }
}
