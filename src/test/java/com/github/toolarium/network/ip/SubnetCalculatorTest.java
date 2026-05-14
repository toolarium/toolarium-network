/*
 * SubnetCalculatorTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.ip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toolarium.network.ip.dto.ISubnetInfo;
import com.github.toolarium.network.ip.dto.SubnetInfo;
import java.net.UnknownHostException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for SubnetCalculator.
 *
 * @author patrick
 */
public class SubnetCalculatorTest {
    private static final Logger LOG = LoggerFactory.getLogger(SubnetCalculatorTest.class);
    private static final String IP_10_0_0_0 = "10.0.0.0";

    /**
     * Test /24 subnet calculation.
     *
     * @throws UnknownHostException In case of an error
     */
    @Test
    public void slash24Test() throws UnknownHostException {
        ISubnetInfo info = SubnetCalculator.getInstance().calculate("192.168.1.0/24");
        assertNotNull(info);
        assertEquals("192.168.1.0", info.getNetworkAddress());
        assertEquals("192.168.1.255", info.getBroadcastAddress());
        assertEquals("192.168.1.1", info.getFirstUsableAddress());
        assertEquals("192.168.1.254", info.getLastUsableAddress());
        assertEquals("255.255.255.0", info.getSubnetMask());
        assertEquals(24, info.getPrefixLength());
        assertEquals(256, info.getTotalAddresses());
        assertEquals(254, info.getUsableHostCount());
        assertFalse(info.isIPv6());
        LOG.info("/24: " + info);
    }

    /**
     * Test /28 subnet calculation.
     *
     * @throws UnknownHostException In case of an error
     */
    @Test
    public void slash28Test() throws UnknownHostException {
        ISubnetInfo info = SubnetCalculator.getInstance().calculate(IP_10_0_0_0 + "/28");
        assertEquals(IP_10_0_0_0, info.getNetworkAddress());
        assertEquals("10.0.0.15", info.getBroadcastAddress());
        assertEquals("10.0.0.1", info.getFirstUsableAddress());
        assertEquals("10.0.0.14", info.getLastUsableAddress());
        assertEquals("255.255.255.240", info.getSubnetMask());
        assertEquals(16, info.getTotalAddresses());
        assertEquals(14, info.getUsableHostCount());
    }

    /**
     * Test /32 subnet calculation.
     *
     * @throws UnknownHostException In case of an error
     */
    @Test
    public void slash32Test() throws UnknownHostException {
        ISubnetInfo info = SubnetCalculator.getInstance().calculate("10.0.0.5/32");
        assertEquals("10.0.0.5", info.getNetworkAddress());
        assertEquals("10.0.0.5", info.getBroadcastAddress());
        assertEquals("10.0.0.5", info.getFirstUsableAddress());
        assertEquals("10.0.0.5", info.getLastUsableAddress());
        assertEquals("255.255.255.255", info.getSubnetMask());
        assertEquals(1, info.getTotalAddresses());
        assertEquals(1, info.getUsableHostCount());
    }

    /**
     * Test /31 subnet calculation.
     *
     * @throws UnknownHostException In case of an error
     */
    @Test
    public void slash31Test() throws UnknownHostException {
        ISubnetInfo info = SubnetCalculator.getInstance().calculate(IP_10_0_0_0 + "/31");
        assertEquals(IP_10_0_0_0, info.getNetworkAddress());
        assertEquals("10.0.0.1", info.getBroadcastAddress());
        assertEquals("10.0.0.0", info.getFirstUsableAddress());
        assertEquals("10.0.0.1", info.getLastUsableAddress());
        assertEquals(2, info.getTotalAddresses());
        assertEquals(2, info.getUsableHostCount());
    }

    /**
     * Test /16 subnet calculation.
     *
     * @throws UnknownHostException In case of an error
     */
    @Test
    public void slash16Test() throws UnknownHostException {
        ISubnetInfo info = SubnetCalculator.getInstance().calculate("172.16.0.0/16");
        assertEquals("172.16.0.0", info.getNetworkAddress());
        assertEquals("172.16.255.255", info.getBroadcastAddress());
        assertEquals("255.255.0.0", info.getSubnetMask());
        assertEquals(65536, info.getTotalAddresses());
        assertEquals(65534, info.getUsableHostCount());
    }

    /**
     * Test IPv6 subnet calculation.
     *
     * @throws UnknownHostException In case of an error
     */
    @Test
    public void ipv6SubnetTest() throws UnknownHostException {
        ISubnetInfo info = SubnetCalculator.getInstance().calculate("2001:db8::/126");
        assertNotNull(info);
        assertTrue(info.isIPv6());
        assertNull(info.getBroadcastAddress());
        assertNull(info.getSubnetMask());
        assertEquals(126, info.getPrefixLength());
        assertEquals(4, info.getTotalAddresses());
        assertEquals(4, info.getUsableHostCount()); // IPv6: all usable
        LOG.info("IPv6 /126: " + info);
    }

    /**
     * Test invalid CIDR input.
     */
    @Test
    public void invalidCidrTest() {
        Assertions.assertThrows(UnknownHostException.class, () -> {
            SubnetCalculator.getInstance().calculate(null);
        });
        Assertions.assertThrows(UnknownHostException.class, () -> {
            SubnetCalculator.getInstance().calculate("invalid");
        });
    }

    /**
     * Test SubnetInfo DTO equals, hashCode, toString.
     */
    @Test
    public void subnetInfoDtoTest() {
        SubnetInfo s1 = new SubnetInfo(IP_10_0_0_0 + "/24", IP_10_0_0_0, "10.0.0.255",
                "10.0.0.1", "10.0.0.254", "255.255.255.0", 24, 256, 254, false);
        SubnetInfo s2 = new SubnetInfo(IP_10_0_0_0 + "/24", IP_10_0_0_0, "10.0.0.255",
                "10.0.0.1", "10.0.0.254", "255.255.255.0", 24, 256, 254, false);
        SubnetInfo s3 = new SubnetInfo("172.16.0.0/16", "172.16.0.0", "172.16.255.255",
                "172.16.0.1", "172.16.255.254", "255.255.0.0", 16, 65536, 65534, false);

        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
        assertFalse(s1.equals(s3));
        assertFalse(s1.equals(null));
        assertFalse(s1.equals("string"));

        assertTrue(s1.toString().contains(IP_10_0_0_0));
        assertTrue(s1.toString().contains("254"));
    }
}
