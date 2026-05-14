/*
 * NetworkInterfaceUtil.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.iface;

import com.github.toolarium.network.iface.dto.INetworkInterfaceInfo;
import com.github.toolarium.network.iface.dto.NetworkInterfaceInfo;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility for querying local network interface information.
 *
 * @author patrick
 */
public final class NetworkInterfaceUtil {
    private static final Logger LOG = LoggerFactory.getLogger(NetworkInterfaceUtil.class);

    private static final class HOLDER {
        static final NetworkInterfaceUtil INSTANCE = new NetworkInterfaceUtil();
    }

    /**
     * Private constructor.
     */
    private NetworkInterfaceUtil() {
    }

    /**
     * Get the instance.
     *
     * @return the instance
     */
    public static NetworkInterfaceUtil getInstance() {
        return HOLDER.INSTANCE;
    }


    /**
     * Get all network interfaces.
     *
     * @return the list of network interface info
     */
    public List<INetworkInterfaceInfo> getNetworkInterfaces() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces == null) {
                return Collections.emptyList();
            }

            List<INetworkInterfaceInfo> result = new ArrayList<>();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                result.add(toInfo(ni));
            }
            return result;

        } catch (SocketException e) {
            LOG.warn("Could not enumerate network interfaces: " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }


    /**
     * Get only active (up) network interfaces.
     *
     * @return the list of active network interface info
     */
    public List<INetworkInterfaceInfo> getActiveNetworkInterfaces() {
        List<INetworkInterfaceInfo> all = getNetworkInterfaces();
        List<INetworkInterfaceInfo> active = new ArrayList<>();
        for (INetworkInterfaceInfo info : all) {
            if (info.isUp() && !info.isLoopback()) {
                active.add(info);
            }
        }
        return active;
    }


    /**
     * Get the loopback interface.
     *
     * @return the loopback interface info or null
     */
    public INetworkInterfaceInfo getLoopbackInterface() {
        List<INetworkInterfaceInfo> all = getNetworkInterfaces();
        for (INetworkInterfaceInfo info : all) {
            if (info.isLoopback()) {
                return info;
            }
        }
        return null;
    }


    /**
     * Convert a NetworkInterface to INetworkInterfaceInfo.
     *
     * @param ni the network interface
     * @return the info
     */
    private INetworkInterfaceInfo toInfo(NetworkInterface ni) {
        String macAddress = formatMacAddress(ni);
        List<String> ipAddresses = new ArrayList<>();
        Enumeration<InetAddress> addrs = ni.getInetAddresses();
        while (addrs.hasMoreElements()) {
            ipAddresses.add(addrs.nextElement().getHostAddress());
        }

        int mtu = -1;
        boolean up = false;
        boolean loopback = false;
        boolean virtual = false;
        try {
            mtu = ni.getMTU();
            up = ni.isUp();
            loopback = ni.isLoopback();
            virtual = ni.isVirtual();
        } catch (SocketException e) {
            LOG.debug("Could not read interface properties for " + ni.getName() + ": " + e.getMessage());
        }

        return new NetworkInterfaceInfo(ni.getName(), ni.getDisplayName(), macAddress, ipAddresses, mtu, up, loopback, virtual);
    }


    /**
     * Format the MAC address of a network interface.
     *
     * @param ni the network interface
     * @return the formatted MAC address or null
     */
    private String formatMacAddress(NetworkInterface ni) {
        try {
            byte[] mac = ni.getHardwareAddress();
            if (mac == null) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                if (i > 0) {
                    sb.append(':');
                }
                sb.append(String.format("%02X", mac[i]));
            }
            return sb.toString();
        } catch (SocketException e) {
            return null;
        }
    }
}
