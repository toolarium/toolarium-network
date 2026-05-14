/*
 * WakeOnLanTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.wol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toolarium.network.wol.dto.IWakeOnLanResult;
import com.github.toolarium.network.wol.dto.WakeOnLanResult;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for Wake-on-LAN functionality.
 *
 * @author patrick
 */
public class WakeOnLanTest {
    private static final Logger LOG = LoggerFactory.getLogger(WakeOnLanTest.class);
    private static final String MAC_ADDRESS = "AA:BB:CC:DD:EE:FF";
    private static final String LOOPBACK_IP = "127.0.0.1";

    /**
     * Test send magic packet.
     */
    @Test
    public void sendMagicPacketTest() {
        // Send to a dummy MAC via localhost broadcast — packet is sent but nobody wakes up
        IWakeOnLanResult result = WakeOnLanFactory.getInstance().wake(MAC_ADDRESS, LOOPBACK_IP);
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(MAC_ADDRESS, result.getMacAddress());
        assertEquals(LOOPBACK_IP, result.getBroadcastAddress());
        assertEquals(9, result.getPort());
        assertNull(result.getException());
        LOG.info("WOL: " + result);
    }

    /**
     * Test send with dash separator MAC.
     */
    @Test
    public void sendWithDashSeparatorTest() {
        IWakeOnLanResult result = WakeOnLanFactory.getInstance().wake("AA-BB-CC-DD-EE-FF", LOOPBACK_IP);
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    /**
     * Test default broadcast address.
     */
    @Test
    public void defaultBroadcastTest() {
        IWakeOnLanResult result = WakeOnLanFactory.getInstance().wake(MAC_ADDRESS);
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    /**
     * Test null MAC address returns failure.
     */
    @Test
    public void nullMacAddressTest() {
        IWakeOnLanResult result = WakeOnLanFactory.getInstance().wake(null);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNotNull(result.getException());
    }

    /**
     * Test invalid MAC address returns failure.
     */
    @Test
    public void invalidMacAddressTest() {
        IWakeOnLanResult result = WakeOnLanFactory.getInstance().wake("invalid", LOOPBACK_IP);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNotNull(result.getException());
    }

    /**
     * Test too short MAC address returns failure.
     */
    @Test
    public void tooShortMacAddressTest() {
        IWakeOnLanResult result = WakeOnLanFactory.getInstance().wake("AA:BB:CC", LOOPBACK_IP);
        assertNotNull(result);
        assertFalse(result.isSuccess());
    }

    /**
     * Test factory getWakeOnLan method.
     */
    @Test
    public void factoryGetWakeOnLanTest() {
        IWakeOnLan wol = WakeOnLanFactory.getInstance().getWakeOnLan();
        assertNotNull(wol);
        IWakeOnLanResult result = wol.wake(MAC_ADDRESS, LOOPBACK_IP);
        assertTrue(result.isSuccess());
    }

    /**
     * Test WakeOnLanResult DTO equals, hashCode, toString.
     */
    @Test
    public void wolResultDtoTest() {
        WakeOnLanResult r1 = new WakeOnLanResult(MAC_ADDRESS, "192.168.1.255", 9, true, null);
        WakeOnLanResult r2 = new WakeOnLanResult(MAC_ADDRESS, "192.168.1.255", 9, true, null);
        WakeOnLanResult r3 = new WakeOnLanResult("11:22:33:44:55:66", "10.0.0.255", 9, false, new Exception("fail"));

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertFalse(r1.equals(r3));
        assertFalse(r1.equals(null));
        assertFalse(r1.equals("string"));

        assertTrue(r1.toString().contains("sent"));
        assertTrue(r3.toString().contains("FAILED"));
    }
}
