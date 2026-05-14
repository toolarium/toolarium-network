/*
 * NsLookupTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.nslookup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toolarium.network.nslookup.dto.INsLookupResult;
import com.github.toolarium.network.nslookup.dto.NsLookupResult;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for DNS lookup functionality.
 *
 * @author patrick
 */
public class NsLookupTest {
    private static final Logger LOG = LoggerFactory.getLogger(NsLookupTest.class);
    private static final String LOOPBACK_IP = "127.0.0.1";


    /**
     * Test forward lookup of localhost.
     */
    @Test
    public void localhostLookupTest() {
        INsLookupResult result = NsLookupFactory.getInstance().lookup("localhost");
        assertNotNull(result);
        assertEquals("localhost", result.getQuery());
        assertTrue(result.isSuccess());
        assertNotNull(result.getHostname());
        assertFalse(result.getAddresses().isEmpty());
        assertTrue(result.getDuration() >= 0);
        assertNull(result.getException());
        LOG.info("Localhost lookup: " + result);
    }


    /**
     * Test forward lookup of loopback IP.
     */
    @Test
    public void loopbackLookupTest() {
        INsLookupResult result = NsLookupFactory.getInstance().lookup(LOOPBACK_IP);
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertFalse(result.getAddresses().isEmpty());
        assertTrue(result.getAddresses().contains(LOOPBACK_IP));
        LOG.info("Loopback lookup: " + result);
    }


    /**
     * Test reverse lookup of 127.0.0.1.
     */
    @Test
    public void reverseLookupTest() {
        INsLookupResult result = NsLookupFactory.getInstance().reverseLookup(LOOPBACK_IP);
        assertNotNull(result);
        assertEquals(LOOPBACK_IP, result.getQuery());
        assertTrue(result.isSuccess());
        assertNotNull(result.getHostname());
        assertFalse(result.getAddresses().isEmpty());
        LOG.info("Reverse lookup: " + result);
    }


    /**
     * Test lookup of an unknown host returns failure.
     */
    @Test
    public void unknownHostTest() {
        INsLookupResult result = NsLookupFactory.getInstance().lookup("this.host.does.not.exist.invalid");
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNull(result.getHostname());
        assertTrue(result.getAddresses().isEmpty());
        assertNotNull(result.getException());
        LOG.info("Unknown host: " + result);
    }


    /**
     * Test lookup with null input.
     */
    @Test
    public void nullInputTest() {
        INsLookupResult result = NsLookupFactory.getInstance().lookup((String) null);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNotNull(result.getException());
    }


    /**
     * Test lookup with empty input.
     */
    @Test
    public void emptyInputTest() {
        INsLookupResult result = NsLookupFactory.getInstance().lookup("  ");
        assertNotNull(result);
        assertFalse(result.isSuccess());
    }


    /**
     * Test reverse lookup with null input.
     */
    @Test
    public void reverseLookupNullTest() {
        INsLookupResult result = NsLookupFactory.getInstance().reverseLookup((String) null);
        assertNotNull(result);
        assertFalse(result.isSuccess());
    }


    /**
     * Test multi-host forward lookup.
     */
    @Test
    public void multiHostLookupTest() {
        INsLookup nsLookup = NsLookupFactory.getInstance().getNsLookup(3000);
        List<INsLookupResult> results = nsLookup.lookup("localhost", LOOPBACK_IP);

        assertNotNull(results);
        assertEquals(2, results.size());
        for (INsLookupResult r : results) {
            assertTrue(r.isSuccess(), "Expected success for: " + r.getQuery());
            LOG.info("Multi lookup: " + r);
        }
    }


    /**
     * Test multi-host reverse lookup.
     */
    @Test
    public void multiReverseLookupTest() {
        INsLookup nsLookup = NsLookupFactory.getInstance().getNsLookup();
        List<INsLookupResult> results = nsLookup.reverseLookup(LOOPBACK_IP, "0.0.0.0");

        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.get(0).isSuccess());
    }


    /**
     * Test empty array input returns empty list.
     */
    @Test
    public void emptyArrayLookupTest() {
        INsLookup nsLookup = NsLookupFactory.getInstance().getNsLookup();
        List<INsLookupResult> results = nsLookup.lookup();
        assertNotNull(results);
        assertTrue(results.isEmpty());

        List<INsLookupResult> reverseResults = nsLookup.reverseLookup();
        assertNotNull(reverseResults);
        assertTrue(reverseResults.isEmpty());
    }


    /**
     * Test factory convenience multi-host lookup.
     */
    @Test
    public void factoryMultiLookupTest() {
        List<INsLookupResult> results = NsLookupFactory.getInstance().lookup("localhost", LOOPBACK_IP);
        assertNotNull(results);
        assertEquals(2, results.size());
    }


    /**
     * Test NsLookupResult equals, hashCode, toString.
     */
    @Test
    public void nsLookupResultEqualsHashCodeTest() {
        NsLookupResult r1 = new NsLookupResult("host1", "host1.local", Arrays.asList("10.0.0.1"), true, 5, null);
        NsLookupResult r2 = new NsLookupResult("host1", "host1.local", Arrays.asList("10.0.0.1"), true, 5, null);
        final NsLookupResult r3 = new NsLookupResult("host2", "host2.local", Arrays.asList("10.0.0.2"), true, 5, null);
        final NsLookupResult r4 = new NsLookupResult("host1", null, Collections.emptyList(), false, 0, new Exception("fail"));

        // equals
        assertEquals(r1, r2);
        assertEquals(r1, r1);
        assertFalse(r1.equals(null));
        assertFalse(r1.equals("string"));
        assertFalse(r1.equals(r3));
        assertFalse(r1.equals(r4));

        // hashCode
        assertEquals(r1.hashCode(), r2.hashCode());

        // toString — success
        String str1 = r1.toString();
        assertTrue(str1.contains("host1"));
        assertTrue(str1.contains("host1.local"));
        assertTrue(str1.contains("10.0.0.1"));
        assertTrue(str1.contains("5ms"));

        // toString — failure
        String str4 = r4.toString();
        assertTrue(str4.contains("FAILED"));
        assertTrue(str4.contains("fail"));

        // getters
        assertEquals("host1", r1.getQuery());
        assertEquals("host1.local", r1.getHostname());
        assertEquals(1, r1.getAddresses().size());
        assertEquals("10.0.0.1", r1.getAddresses().get(0));
        assertTrue(r1.isSuccess());
        assertEquals(5, r1.getDuration());
        assertNull(r1.getException());
        assertNotNull(r4.getException());
    }
}
