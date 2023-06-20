/*
 * IPUtilTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.ip;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;


/**
 * IP util test
 * 
 * @author patrick
 */
public class IPUtilTest {
    
    /**
     * Test ip4 address
     */
    @Test
    public void testIP4Address() {
        assertFalse(IPUtil.getInstance().isIPv4Address(null));
        assertFalse(IPUtil.getInstance().isIPv4Address(""));
        assertFalse(IPUtil.getInstance().isIPv4Address("192.168.0.1907"));
        assertFalse(IPUtil.getInstance().isIPv4Address("adsdsb"));

        assertTrue(IPUtil.getInstance().isIPv4Address("192.168.0.1"));
    }


    /**
     * Test ip6 address
     */
    @Test
    public void testIP6Address() {
        assertFalse(IPUtil.getInstance().isIPv6Address(null));
        assertFalse(IPUtil.getInstance().isIPv6Address(""));
        assertFalse(IPUtil.getInstance().isIPv6Address("192.168.0.1909"));
        assertFalse(IPUtil.getInstance().isIPv6Address("adsdsa"));

        assertTrue(IPUtil.getInstance().isIPv6Address("FE80:0000:0000:0000:0202:B3FF:FE1E:8329"));
        assertTrue(IPUtil.getInstance().isIPv6Address("FE80::0202:B3FF:FE1E:8329"));
    }


    /**
     * Test address
     */
    @Test
    public void testAddress() {
        assertFalse(IPUtil.getInstance().isValidAddress(null));
        assertFalse(IPUtil.getInstance().isValidAddress(""));
        assertFalse(IPUtil.getInstance().isValidAddress("192.168.0.1909"));
        assertFalse(IPUtil.getInstance().isValidAddress("adsdsa"));

        assertTrue(IPUtil.getInstance().isValidAddress("FE80:0000:0000:0000:0202:B3FF:FE1E:8329"));
        assertTrue(IPUtil.getInstance().isValidAddress("FE80::0202:B3FF:FE1E:8329"));
        assertTrue(IPUtil.getInstance().isValidAddress("192.168.0.1"));
    }
}
