/*
 * NetworkInterfaceUtilTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.iface;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toolarium.network.iface.dto.INetworkInterfaceInfo;
import com.github.toolarium.network.iface.dto.NetworkInterfaceInfo;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for NetworkInterfaceUtil.
 *
 * @author patrick
 */
public class NetworkInterfaceUtilTest {
    private static final Logger LOG = LoggerFactory.getLogger(NetworkInterfaceUtilTest.class);

    /**
     * Test get all network interfaces.
     */
    @Test
    public void getAllInterfacesTest() {
        List<INetworkInterfaceInfo> interfaces = NetworkInterfaceUtil.getInstance().getNetworkInterfaces();
        assertNotNull(interfaces);
        assertFalse(interfaces.isEmpty(), "Should have at least one network interface");
        for (INetworkInterfaceInfo info : interfaces) {
            assertNotNull(info.getName());
            LOG.info("Interface: " + info);
        }
    }

    /**
     * Test get active network interfaces.
     */
    @Test
    public void getActiveInterfacesTest() {
        List<INetworkInterfaceInfo> active = NetworkInterfaceUtil.getInstance().getActiveNetworkInterfaces();
        assertNotNull(active);
        for (INetworkInterfaceInfo info : active) {
            assertTrue(info.isUp());
            assertFalse(info.isLoopback());
        }
    }

    /**
     * Test get loopback interface.
     */
    @Test
    public void getLoopbackTest() {
        INetworkInterfaceInfo loopback = NetworkInterfaceUtil.getInstance().getLoopbackInterface();
        assertNotNull(loopback, "Should have a loopback interface");
        assertTrue(loopback.isLoopback());
        assertTrue(loopback.isUp());
        LOG.info("Loopback: " + loopback);
    }

    /**
     * Test NetworkInterfaceInfo DTO equals, hashCode, toString.
     */
    @Test
    public void networkInterfaceInfoDtoTest() {
        NetworkInterfaceInfo i1 = new NetworkInterfaceInfo("eth0", "Ethernet 0", "AA:BB:CC:DD:EE:FF",
                Arrays.asList("192.168.1.10", "fe80::1"), 1500, true, false, false);
        NetworkInterfaceInfo i2 = new NetworkInterfaceInfo("eth0", "Ethernet 0", "AA:BB:CC:DD:EE:FF",
                Arrays.asList("192.168.1.10", "fe80::1"), 1500, true, false, false);
        NetworkInterfaceInfo i3 = new NetworkInterfaceInfo("lo", "Loopback", null,
                Arrays.asList("127.0.0.1"), -1, true, true, false);

        assertEquals(i1, i2);
        assertEquals(i1.hashCode(), i2.hashCode());
        assertFalse(i1.equals(i3));
        assertFalse(i1.equals(null));
        assertFalse(i1.equals("string"));

        assertEquals("eth0", i1.getName());
        assertEquals("Ethernet 0", i1.getDisplayName());
        assertEquals("AA:BB:CC:DD:EE:FF", i1.getMacAddress());
        assertEquals(2, i1.getIpAddresses().size());
        assertEquals(1500, i1.getMtu());
        assertTrue(i1.isUp());
        assertFalse(i1.isLoopback());
        assertFalse(i1.isVirtual());

        assertTrue(i1.toString().contains("eth0"));
        assertTrue(i1.toString().contains("AA:BB:CC:DD:EE:FF"));
        assertTrue(i3.toString().contains("loopback"));
    }
}
