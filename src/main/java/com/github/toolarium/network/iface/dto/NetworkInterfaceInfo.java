/*
 * NetworkInterfaceInfo.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.iface.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * Implements the {@link INetworkInterfaceInfo}.
 *
 * @author patrick
 */
public class NetworkInterfaceInfo implements INetworkInterfaceInfo, Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String displayName;
    private String macAddress;
    private List<String> ipAddresses;
    private int mtu;
    private boolean up;
    private boolean loopback;
    private boolean virtual;


    /**
     * Constructor for NetworkInterfaceInfo
     *
     * @param name the interface name
     * @param displayName the display name
     * @param macAddress the MAC address
     * @param ipAddresses the IP addresses
     * @param mtu the MTU
     * @param up true if up
     * @param loopback true if loopback
     * @param virtual true if virtual
     */
    public NetworkInterfaceInfo(String name, String displayName, String macAddress,
                                 List<String> ipAddresses, int mtu, boolean up, boolean loopback, boolean virtual) {
        this.name = name;
        this.displayName = displayName;
        this.macAddress = macAddress;
        if (ipAddresses != null) {
            this.ipAddresses = new ArrayList<>(ipAddresses);
        } else {
            this.ipAddresses = Collections.emptyList();
        }
        this.mtu = mtu;
        this.up = up;
        this.loopback = loopback;
        this.virtual = virtual;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getMacAddress() {
        return macAddress;
    }

    @Override
    public List<String> getIpAddresses() {
        return Collections.unmodifiableList(ipAddresses);
    }

    @Override
    public int getMtu() {
        return mtu;
    }

    @Override
    public boolean isUp() {
        return up;
    }

    @Override
    public boolean isLoopback() {
        return loopback;
    }

    @Override
    public boolean isVirtual() {
        return virtual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, macAddress, ipAddresses);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        NetworkInterfaceInfo other = (NetworkInterfaceInfo) obj;
        return Objects.equals(name, other.name) && Objects.equals(macAddress, other.macAddress)
                && Objects.equals(ipAddresses, other.ipAddresses);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("NetworkInterface [");
        sb.append(name).append(" (").append(displayName).append(")");
        if (macAddress != null) {
            sb.append(", MAC=").append(macAddress);
        }
        sb.append(", IPs=").append(ipAddresses);
        sb.append(", MTU=").append(mtu);
        if (up) {
            sb.append(", UP");
        } else {
            sb.append(", DOWN");
        }
        if (loopback) {
            sb.append(", loopback");
        }
        if (virtual) {
            sb.append(", virtual");
        }
        sb.append("]");
        return sb.toString();
    }
}
