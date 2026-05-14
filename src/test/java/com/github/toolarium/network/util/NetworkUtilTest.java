/*
 * NetworkUtilTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;


/**
 * Tests for NetworkUtil.
 *
 * @author patrick
 */
public class NetworkUtilTest {

    /**
     * Test getHostname returns a non-empty value.
     */
    @Test
    public void hostnameTest() {
        String hostname = NetworkUtil.getInstance().getHostname();
        assertNotNull(hostname);
        assertFalse(hostname.trim().isEmpty(), "Hostname should not be empty");
    }


    /**
     * Test getHostIPAddress returns a non-empty value.
     */
    @Test
    public void hostIPAddressTest() {
        String ipAddress = NetworkUtil.getInstance().getHostIPAddress();
        assertNotNull(ipAddress);
        assertFalse(ipAddress.trim().isEmpty(), "IP address should not be empty");
    }
}
