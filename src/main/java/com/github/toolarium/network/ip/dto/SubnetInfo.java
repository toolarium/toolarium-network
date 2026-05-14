/*
 * SubnetInfo.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.ip.dto;

import java.io.Serializable;
import java.util.Objects;


/**
 * Implements the {@link ISubnetInfo}.
 *
 * @author patrick
 */
public class SubnetInfo implements ISubnetInfo, Serializable {
    private static final long serialVersionUID = 1L;
    private String cidr;
    private String networkAddress;
    private String broadcastAddress;
    private String firstUsableAddress;
    private String lastUsableAddress;
    private String subnetMask;
    private int prefixLength;
    private long totalAddresses;
    private long usableHostCount;
    private boolean ipv6;


    /**
     * Constructor for SubnetInfo
     *
     * @param cidr the CIDR notation
     * @param networkAddress the network address
     * @param broadcastAddress the broadcast address
     * @param firstUsableAddress the first usable address
     * @param lastUsableAddress the last usable address
     * @param subnetMask the subnet mask
     * @param prefixLength the prefix length
     * @param totalAddresses the total address count
     * @param usableHostCount the usable host count
     * @param ipv6 true if IPv6
     */
    public SubnetInfo(String cidr, String networkAddress, String broadcastAddress,
                      String firstUsableAddress, String lastUsableAddress, String subnetMask,
                      int prefixLength, long totalAddresses, long usableHostCount, boolean ipv6) {
        this.cidr = cidr;
        this.networkAddress = networkAddress;
        this.broadcastAddress = broadcastAddress;
        this.firstUsableAddress = firstUsableAddress;
        this.lastUsableAddress = lastUsableAddress;
        this.subnetMask = subnetMask;
        this.prefixLength = prefixLength;
        this.totalAddresses = totalAddresses;
        this.usableHostCount = usableHostCount;
        this.ipv6 = ipv6;
    }

    @Override
    public String getCidr() {
        return cidr;
    }

    @Override
    public String getNetworkAddress() {
        return networkAddress;
    }

    @Override
    public String getBroadcastAddress() {
        return broadcastAddress;
    }

    @Override
    public String getFirstUsableAddress() {
        return firstUsableAddress;
    }

    @Override
    public String getLastUsableAddress() {
        return lastUsableAddress;
    }

    @Override
    public String getSubnetMask() {
        return subnetMask;
    }

    @Override
    public int getPrefixLength() {
        return prefixLength;
    }

    @Override
    public long getTotalAddresses() {
        return totalAddresses;
    }

    @Override
    public long getUsableHostCount() {
        return usableHostCount;
    }

    @Override
    public boolean isIPv6() {
        return ipv6;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cidr, networkAddress, broadcastAddress, prefixLength);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SubnetInfo other = (SubnetInfo) obj;
        return Objects.equals(cidr, other.cidr) && Objects.equals(networkAddress, other.networkAddress)
                && Objects.equals(broadcastAddress, other.broadcastAddress) && prefixLength == other.prefixLength;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("SubnetInfo [").append(cidr);
        sb.append(", network=").append(networkAddress);
        if (broadcastAddress != null) {
            sb.append(", broadcast=").append(broadcastAddress);
        }
        sb.append(", usable=").append(firstUsableAddress).append("-").append(lastUsableAddress);
        sb.append(", hosts=").append(usableHostCount);
        if (subnetMask != null) {
            sb.append(", mask=").append(subnetMask);
        }
        return sb.append("]").toString();
    }
}
