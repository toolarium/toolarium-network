/*
 * TracerouteTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.traceroute;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toolarium.network.traceroute.dto.ITracerouteResult;
import com.github.toolarium.network.traceroute.dto.TracerouteHop;
import com.github.toolarium.network.traceroute.dto.TracerouteResult;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for traceroute functionality.
 *
 * @author patrick
 */
public class TracerouteTest {
    private static final Logger LOG = LoggerFactory.getLogger(TracerouteTest.class);
    private static final String LOCALHOST = "localhost";

    /**
     * Test traceroute to localhost.
     */
    @Test
    public void localhostTraceTest() {
        ITracerouteResult result = TracerouteFactory.getInstance().trace(LOCALHOST, 80);
        assertNotNull(result);
        assertEquals(LOCALHOST, result.getTarget());
        assertNotNull(result.getTargetAddress());
        assertTrue(result.getDuration() >= 0);
        LOG.info("Localhost trace: " + result);
    }

    /**
     * Test traceroute with null host.
     */
    @Test
    public void nullHostTest() {
        ITracerouteResult result = TracerouteFactory.getInstance().trace(null);
        assertNotNull(result);
        assertFalse(result.isTargetReached());
    }

    /**
     * Test traceroute with empty host.
     */
    @Test
    public void emptyHostTest() {
        ITracerouteResult result = TracerouteFactory.getInstance().trace("  ");
        assertNotNull(result);
        assertFalse(result.isTargetReached());
    }

    /**
     * Test traceroute with custom settings.
     */
    @Test
    public void customSettingsTest() {
        ITraceroute traceroute = TracerouteFactory.getInstance().getTraceroute(5, 1000);
        ITracerouteResult result = traceroute.trace(LOCALHOST);
        assertNotNull(result);
        LOG.info("Custom trace: " + result);
    }

    /**
     * Test TracerouteHop DTO equals, hashCode, toString.
     */
    @Test
    public void tracerouteHopDtoTest() {
        TracerouteHop h1 = new TracerouteHop(1, "10.0.0.1", "gateway", 5, true);
        TracerouteHop h2 = new TracerouteHop(1, "10.0.0.1", "gateway", 5, true);
        TracerouteHop h3 = new TracerouteHop(2, null, null, -1, false);

        assertEquals(h1, h2);
        assertEquals(h1.hashCode(), h2.hashCode());
        assertFalse(h1.equals(h3));
        assertFalse(h1.equals(null));
        assertFalse(h1.equals("string"));

        assertEquals(1, h1.getHopNumber());
        assertEquals("10.0.0.1", h1.getAddress());
        assertEquals("gateway", h1.getHostname());
        assertEquals(5, h1.getRtt());
        assertTrue(h1.isReached());
        assertTrue(h1.toString().contains("gateway"));

        assertFalse(h3.isReached());
        assertTrue(h3.toString().contains("* * *"));
    }

    /**
     * Test TracerouteResult DTO equals, hashCode, toString.
     */
    @Test
    public void tracerouteResultDtoTest() {
        TracerouteHop hop = new TracerouteHop(1, "127.0.0.1", LOCALHOST, 1, true);
        TracerouteResult r1 = new TracerouteResult(LOCALHOST, "127.0.0.1",
                Arrays.asList(hop), true, 10);
        TracerouteResult r2 = new TracerouteResult(LOCALHOST, "127.0.0.1",
                Arrays.asList(hop), true, 10);
        TracerouteResult r3 = new TracerouteResult("other", null, Collections.emptyList(), false, 0);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertFalse(r1.equals(r3));
        assertFalse(r1.equals(null));
        assertFalse(r1.equals("string"));

        assertTrue(r1.toString().contains("reached"));
        assertTrue(r3.toString().contains("not reached"));
    }
}
