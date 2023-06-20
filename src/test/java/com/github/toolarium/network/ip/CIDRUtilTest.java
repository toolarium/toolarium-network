/*
 * CIDRUtilTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.ip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.UnknownHostException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Test CIDR utility
 * 
 * @author patrick
 */
public class CIDRUtilTest {

    /**
     * Test ip4 address
     */
    @Test
    public void testIP4AddressRange() {
        assertFalse(CIDRUtil.getInstance().isIPv4Range(null));
        assertFalse(CIDRUtil.getInstance().isIPv4Range(""));
        assertFalse(CIDRUtil.getInstance().isIPv4Range("192.168.0.1909"));
        assertFalse(CIDRUtil.getInstance().isIPv4Range("adsdsa"));

        assertTrue(CIDRUtil.getInstance().isIPv4Range("192.168.0.1/10"));
        assertTrue(CIDRUtil.getInstance().isIPv4Range("192.168.0.1/31"));
        assertTrue(CIDRUtil.getInstance().isIPv4Range("192.168.0.1/32"));
        assertTrue(CIDRUtil.getInstance().isIPv4Range("192.109.190.18/32"));
    }


    /**
     * Test ip6 address
     */
    @Test
    public void testIP6AddressRange() {
        assertFalse(CIDRUtil.getInstance().isIPv6Range(null));
        assertFalse(CIDRUtil.getInstance().isIPv6Range(""));
        assertFalse(CIDRUtil.getInstance().isIPv6Range("192.168.0.1909"));
        assertFalse(CIDRUtil.getInstance().isIPv6Range("adsdsa"));

        assertTrue(CIDRUtil.getInstance().isIPv6Range("FE80:0000:0000:0000:0202:B3FF:FE1E:8329/10"));
        assertTrue(CIDRUtil.getInstance().isIPv6Range("FE80::0202:B3FF:FE1E:8329/10"));
    }


    /**
     * Test address
     */
    @Test
    public void testAddressRange() {
        assertFalse(CIDRUtil.getInstance().isValidRange(null));
        assertFalse(CIDRUtil.getInstance().isValidRange(""));
        assertFalse(CIDRUtil.getInstance().isValidRange("192.168.0.1909"));
        assertFalse(CIDRUtil.getInstance().isValidRange("adsdsa"));

        assertTrue(CIDRUtil.getInstance().isValidRange("FE80:0000:0000:0000:0202:B3FF:FE1E:8329/10"));
        assertTrue(CIDRUtil.getInstance().isValidRange("FE80::0202:B3FF:FE1E:8329/10"));
        assertTrue(CIDRUtil.getInstance().isValidRange("192.168.0.1/10"));
    }


    /**
     * Test if an address is in a range
     */
    @Test
    public void testIsInRange() {
        assertFalse(CIDRUtil.getInstance().isInRange(null, null));
        assertFalse(CIDRUtil.getInstance().isInRange("", null));
        assertFalse(CIDRUtil.getInstance().isInRange(null, ""));
        assertFalse(CIDRUtil.getInstance().isInRange("", ""));

        assertFalse(CIDRUtil.getInstance().isInRange("localhostadas", "10.41.20.82"));
        assertTrue(CIDRUtil.getInstance().isInRange("10.41.20.82", "10.41.20.82"));
        assertFalse(CIDRUtil.getInstance().isInRange("localhost", "10.41.20.82"));
        assertTrue(CIDRUtil.getInstance().isInRange("localhost", "127.0.0.1"));
        assertTrue(CIDRUtil.getInstance().isInRange("127.0.0.1", "127.0.0.1"));
        assertTrue(CIDRUtil.getInstance().isInRange("localhost", "localhost"));

        assertTrue(CIDRUtil.getInstance().isInRange("160.46.252.0/24", "160.46.252.16"));
        assertTrue(CIDRUtil.getInstance().isInRange("192.109.190.18/32", "192.109.190.18"));
        assertTrue(CIDRUtil.getInstance().isInRange("195.141.185.168/32", "195.141.185.168"));
        assertTrue(CIDRUtil.getInstance().isInRange("192.109.190.18/32", "192.109.190.18"));

        assertFalse(CIDRUtil.getInstance().isInRange("10.41.20.205", "10.41.20.82"));
        assertTrue(CIDRUtil.getInstance().isInRange("10.0.0.0/24 ", "10.0.0.0"));
        assertTrue(CIDRUtil.getInstance().isInRange("10.0.0.0/24 ", "10.0.0.1"));
        assertTrue(CIDRUtil.getInstance().isInRange("10.0.0.0/24 ", "10.0.0.255"));
        assertFalse(CIDRUtil.getInstance().isInRange("10.0.0.0/24", "10.0.1.1"));
        assertTrue(CIDRUtil.getInstance().isInRange("10.0.0.0/28 ", "10.0.0.1"));
        assertTrue(CIDRUtil.getInstance().isInRange("10.0.0.0/28 ", "10.0.0.2"));
        assertTrue(CIDRUtil.getInstance().isInRange("10.0.0.0/28 ", "10.0.0.15"));
        assertTrue(CIDRUtil.getInstance().isInRange("10.0.0.0/28 ", "10.0.0.15"));
        assertFalse(CIDRUtil.getInstance().isInRange("10.0.0.0/28 ", "10.0.0.16"));
    }
    
    
    /**
     * Test the CIDR for ipv4
     *
     * @throws UnknownHostException In case of invalid host address
     */
    @Test
    public void testIP4() throws UnknownHostException {
        Assertions.assertThrows(UnknownHostException.class, () -> {
            CIDRUtil.getInstance().parse(null).getStartAddressString();
        });

        Assertions.assertThrows(UnknownHostException.class, () -> {
            CIDRUtil.getInstance().parse("   ").getStartAddressString();
        });

        Assertions.assertThrows(UnknownHostException.class, () -> {
            CIDRUtil.getInstance().parse("10.77.12.11").getStartAddressString();
        });

        assertEquals("10.77.0.0", CIDRUtil.getInstance().parse("10.77.12.11/18").getStartAddressString());
        assertEquals("10.77.63.255", CIDRUtil.getInstance().parse("10.77.12.11/18").getEndAddressString());
        assertTrue(CIDRUtil.getInstance().parse("10.77.12.11/18").isInRange("10.77.12.22"));
    }

    
    /**
     * Test the CIDR for ipv6
     *
     * @throws UnknownHostException In case of invalid host address
     */
    @Test
    public void testIP6() throws UnknownHostException {
        assertEquals("435:23f:0:0:0:0:0:0", CIDRUtil.getInstance().parse("435:23f::45:23/101").getStartAddressString());
        assertEquals("435:23f:0:0:0:0:7ff:ffff", CIDRUtil.getInstance().parse("435:23f::45:23/101").getEndAddressString());
        assertTrue(CIDRUtil.getInstance().parse("435:23f::45:23/101").isInRange("435:23f::45:27"));
        
        assertEquals("fe80:0:0:0:0:0:0:0", CIDRUtil.getInstance().parse("FE80:0000:0000:0000:0202:B3FF:FE1E:8329/10").getStartAddressString());
        assertEquals("febf:ffff:ffff:ffff:ffff:ffff:ffff:ffff", CIDRUtil.getInstance().parse("FE80:0000:0000:0000:0202:B3FF:FE1E:8329/10").getEndAddressString());
        assertTrue(CIDRUtil.getInstance().parse("FE80:0000:0000:0000:0202:B3FF:FE1E:8329/10").isInRange("fe80:0:0:0:0:0:0:1"));

        assertEquals("fe80:0:0:0:0:0:0:0", CIDRUtil.getInstance().parse("FE80::0202:B3FF:FE1E:8329/10").getStartAddressString());
        assertEquals("febf:ffff:ffff:ffff:ffff:ffff:ffff:ffff", CIDRUtil.getInstance().parse("FE80::0202:B3FF:FE1E:8329/10").getEndAddressString());
        assertTrue(CIDRUtil.getInstance().parse("FE80::0202:B3FF:FE1E:8329/10").isInRange("fe80:0:0:0:0:0:0:1"));
    }


    /**
     * Test in range
     *
     * @throws UnknownHostException In case of invalid host address
     */
    @Test
    public void testIP4InRange() throws UnknownHostException {
        assertTrue(CIDRUtil.getInstance().parse("192.168.0.1/10").isInRange("192.168.0.2"));
        assertTrue(CIDRUtil.getInstance().parse("160.46.252.0/24").isInRange("160.46.252.16"));
        assertTrue(CIDRUtil.getInstance().parse("192.109.190.18/32").isInRange("192.109.190.18"));
        assertTrue(CIDRUtil.getInstance().parse("195.141.185.168/32").isInRange("195.141.185.168"));
        assertTrue(CIDRUtil.getInstance().parse("192.109.190.18/32").isInRange("192.109.190.18"));
        assertTrue(CIDRUtil.getInstance().parse("10.0.0.0/24").isInRange("10.0.0.0"));
        assertTrue(CIDRUtil.getInstance().parse("10.0.0.0/24").isInRange("10.0.0.1"));
        assertTrue(CIDRUtil.getInstance().parse("10.0.0.0/24").isInRange("10.0.0.255"));
        assertFalse(CIDRUtil.getInstance().parse("10.0.0.0/24").isInRange("10.0.1.1"));
        assertTrue(CIDRUtil.getInstance().parse("10.0.0.0/28").isInRange("10.0.0.1"));
        assertTrue(CIDRUtil.getInstance().parse("10.0.0.0/28").isInRange("10.0.0.2"));
        assertTrue(CIDRUtil.getInstance().parse("10.0.0.0/28").isInRange("10.0.0.15"));
        assertTrue(CIDRUtil.getInstance().parse("10.0.0.0/28").isInRange("10.0.0.15"));
        assertFalse(CIDRUtil.getInstance().parse("10.0.0.0/28").isInRange("10.0.0.16"));
    }


    /**
     * Test address
     * 
     * @throws UnknownHostException In case of invalid address
     */
    @Test
    public void testAllAddresses() throws UnknownHostException {
        assertEquals("[192.168.0.1]", "" + CIDRUtil.getInstance().getAllAddresses("192.168.0.1/32"));
        assertEquals("[192.168.0.0, 192.168.0.1]", "" + CIDRUtil.getInstance().getAllAddresses("192.168.0.1/31"));
        assertEquals("[192.168.0.0, 192.168.0.1, 192.168.0.2, 192.168.0.3]", "" + CIDRUtil.getInstance().getAllAddresses("192.168.0.1/30"));
        assertEquals("[192.168.0.0, 192.168.0.1, 192.168.0.2, 192.168.0.3, 192.168.0.4, 192.168.0.5, 192.168.0.6, 192.168.0.7]", "" + CIDRUtil.getInstance().getAllAddresses("192.168.0.1/29"));
        assertEquals("[192.168.0.0, 192.168.0.1, 192.168.0.2, 192.168.0.3, 192.168.0.4, 192.168.0.5, 192.168.0.6, 192.168.0.7, 192.168.0.8, 192.168.0.9, 192.168.0.10, 192.168.0.11, 192.168.0.12, 192.168.0.13, 192.168.0.14, 192.168.0.15]", 
                     "" + CIDRUtil.getInstance().getAllAddresses("192.168.0.1/28"));

        assertEquals("[2001:db8::]", "" + CIDRUtil.getInstance().getAllAddresses("2001:db8::/128"));
        assertEquals("[2001:db8::, 2001:db8::1]", "" + CIDRUtil.getInstance().getAllAddresses("2001:db8::/127"));
        assertEquals("[2001:db8::, 2001:db8::1, 2001:db8::2, 2001:db8::3]", "" + CIDRUtil.getInstance().getAllAddresses("2001:db8::/126"));
        
        assertEquals(64, CIDRUtil.getInstance().getAllAddresses("2001:db8::/122").size());
        assertEquals(8192, CIDRUtil.getInstance().getAllAddresses("2001:db8::/115").size());

        //assertEquals(new BigInteger("9223372036854775808"), CIDRUtil.getInstance().getAllAddresses("2001:db8::/65").size());
        assertEquals("[1:2:3:0:0:6::]", "" + CIDRUtil.getInstance().getAllAddresses("1:2:3:0:0:6::"));
    }
}
