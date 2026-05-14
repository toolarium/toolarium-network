/*
 * PortScanResultTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.scanner.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;


/**
 * Tests for PortScanResult getters, setters, equals, hashCode, toString.
 *
 * @author patrick
 */
public class PortScanResultTest {
    private static final String HOST_IP = "10.0.0.1";

    /**
     * Test PortScanResult getters, setters, equals, hashCode, toString.
     */
    @Test
    public void portScanResultTest() {
        PortScanResult r1 = new PortScanResult(HOST_IP, 8080, true);
        final PortScanResult r2 = new PortScanResult(HOST_IP, 8080, true);
        final PortScanResult r3 = new PortScanResult("10.0.0.2", 8080, true);
        final PortScanResult r4 = new PortScanResult(HOST_IP, 9090, true);
        final PortScanResult r5 = new PortScanResult(HOST_IP, 8080, false);

        // getters
        assertEquals(HOST_IP, r1.getHostAddress());
        assertEquals(8080, r1.getPort());
        assertTrue(r1.isAvailable());
        assertTrue(r1.isActive());
        assertNull(r1.getProtocol());
        assertNull(r1.getApplication());

        // setters
        r1.setProtocol("TCP");
        r1.setApplication("HTTP");
        r1.setIsActive(false);
        assertEquals("TCP", r1.getProtocol());
        assertEquals("HTTP", r1.getApplication());
        assertFalse(r1.isActive());

        // reset for equals test
        r1 = new PortScanResult(HOST_IP, 8080, true);

        // equals
        assertEquals(r1, r2);
        assertEquals(r1, r1);
        assertFalse(r1.equals(null));
        assertFalse(r1.equals("string"));
        assertNotEquals(r1, r3);
        assertNotEquals(r1, r4);
        assertNotEquals(r1, r5);

        // hashCode
        assertEquals(r1.hashCode(), r2.hashCode());

        // toString
        String str = r1.toString();
        assertTrue(str.contains(HOST_IP));
        assertTrue(str.contains("8080"));

        // copy constructor
        r1.setProtocol("TCP");
        r1.setApplication("HTTP");
        PortScanResult copy = new PortScanResult(r1);
        assertEquals(r1.getHostAddress(), copy.getHostAddress());
        assertEquals(r1.getPort(), copy.getPort());
        assertEquals(r1.isAvailable(), copy.isAvailable());
        assertEquals(r1.getProtocol(), copy.getProtocol());
        assertEquals(r1.getApplication(), copy.getApplication());
    }
}
