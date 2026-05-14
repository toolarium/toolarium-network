/*
 * SubnetCalculator.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.ip;

import com.github.toolarium.network.ip.dto.CIDRInfo;
import com.github.toolarium.network.ip.dto.ISubnetInfo;
import com.github.toolarium.network.ip.dto.SubnetInfo;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * Subnet calculator — computes detailed subnet information from a CIDR expression.
 *
 * @author patrick
 */
public final class SubnetCalculator {

    private static final class HOLDER {
        static final SubnetCalculator INSTANCE = new SubnetCalculator();
    }

    /**
     * Constructor.
     */
    private SubnetCalculator() {
    }

    /**
     * Get the instance.
     *
     * @return the instance
     */
    public static SubnetCalculator getInstance() {
        return HOLDER.INSTANCE;
    }


    /**
     * Calculate subnet info from a CIDR expression.
     *
     * @param cidrExpression the CIDR expression (e.g. "192.168.1.0/24")
     * @return the subnet info
     * @throws UnknownHostException if the address is invalid
     */
    public ISubnetInfo calculate(String cidrExpression) throws UnknownHostException {
        CIDRInfo cidr = CIDRUtil.getInstance().parse(cidrExpression);
        boolean isIPv6 = cidr.getTargetSize() == 16;

        String networkAddress = cidr.getStartAddressString();
        String broadcastAddress = cidr.getEndAddressString();
        int prefix = cidr.getNetwork();

        BigInteger totalAddresses = cidr.getEndIp().subtract(cidr.getStartIp()).add(BigInteger.ONE);

        String firstUsable;
        String lastUsable;
        long usableHosts;
        String subnetMask = null;

        if (isIPv6) {
            // IPv6: no broadcast concept, all addresses are usable
            firstUsable = networkAddress;
            lastUsable = broadcastAddress;
            usableHosts = totalAddresses.min(BigInteger.valueOf(Long.MAX_VALUE)).longValue();
        } else {
            // IPv4: subtract network and broadcast
            subnetMask = calculateIPv4SubnetMask(prefix);

            if (totalAddresses.compareTo(BigInteger.valueOf(2)) > 0) {
                BigInteger firstIp = cidr.getStartIp().add(BigInteger.ONE);
                BigInteger lastIp = cidr.getEndIp().subtract(BigInteger.ONE);
                firstUsable = resolveAddress(firstIp, cidr.getTargetSize());
                lastUsable = resolveAddress(lastIp, cidr.getTargetSize());
                usableHosts = totalAddresses.subtract(BigInteger.valueOf(2)).longValue();
            } else if (totalAddresses.compareTo(BigInteger.valueOf(2)) == 0) {
                // /31 point-to-point: both addresses are usable (RFC 3021)
                firstUsable = networkAddress;
                lastUsable = broadcastAddress;
                usableHosts = 2;
            } else {
                // /32 single host
                firstUsable = networkAddress;
                lastUsable = networkAddress;
                usableHosts = 1;
            }
        }

        String broadcast;
        if (isIPv6) {
            broadcast = null;
        } else {
            broadcast = broadcastAddress;
        }
        return new SubnetInfo(cidrExpression.trim(), networkAddress, broadcast,
                firstUsable, lastUsable, subnetMask, prefix,
                totalAddresses.min(BigInteger.valueOf(Long.MAX_VALUE)).longValue(),
                usableHosts, isIPv6);
    }


    /**
     * Calculate IPv4 subnet mask from prefix length.
     *
     * @param prefix the prefix length (0-32)
     * @return the dotted-decimal subnet mask
     */
    private String calculateIPv4SubnetMask(int prefix) {
        int mask;
        if (prefix == 0) {
            mask = 0;
        } else {
            mask = (0xFFFFFFFF << (32 - prefix));
        }
        return ((mask >> 24) & 0xFF) + "." + ((mask >> 16) & 0xFF) + "."
                + ((mask >> 8) & 0xFF) + "." + (mask & 0xFF);
    }


    /**
     * Resolve a BigInteger IP to a string address.
     *
     * @param ip the IP as BigInteger
     * @param targetSize 4 for IPv4, 16 for IPv6
     * @return the address string
     * @throws UnknownHostException if resolution fails
     */
    private String resolveAddress(BigInteger ip, int targetSize) throws UnknownHostException {
        byte[] bytes = ip.toByteArray();
        byte[] padded = new byte[targetSize];

        // BigInteger may produce extra leading byte for sign
        int srcOffset = Math.max(0, bytes.length - targetSize);
        int dstOffset = Math.max(0, targetSize - bytes.length);
        int length = Math.min(bytes.length, targetSize);
        System.arraycopy(bytes, srcOffset, padded, dstOffset, length);

        return InetAddress.getByAddress(padded).getHostAddress();
    }
}
