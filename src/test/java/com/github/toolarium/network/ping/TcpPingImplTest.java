/*
 * TcpPingImplTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.ping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toolarium.network.ping.dto.IPingResult;
import com.github.toolarium.network.ping.dto.PingResult;
import com.github.toolarium.network.server.HttpServerFactory;
import com.github.toolarium.network.server.IHttpServer;
import com.github.toolarium.network.server.service.EchoService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Extended tests for TcpPingImpl covering timeout, IPv6, edge cases, and PingResult DTO.
 *
 * @author patrick
 */
public class TcpPingImplTest {
    private static final Logger LOG = LoggerFactory.getLogger(TcpPingImplTest.class);
    private static final String HOST1 = "host1";


    /**
     * Test that ping to a closed port returns unreachable within timeout.
     */
    @Test
    public void timeoutTest() {
        // Use a very short timeout to verify timeout behavior
        IPing ping = PingFactory.getInstance().getPing(500);
        IPingResult result = ping.ping("localhost", 19876);

        assertNotNull(result);
        assertFalse(result.isReachable());
        assertEquals("localhost", result.getHost());
        assertEquals(19876, result.getPort());
        assertEquals(-1, result.getDuration());
        LOG.info("Timeout test: " + result);
    }


    /**
     * Test ping with null/empty host array returns empty list.
     */
    @Test
    public void emptyInputTest() {
        IPing ping = PingFactory.getInstance().getPing(1000);

        List<IPingResult> results1 = ping.ping(80);
        assertNotNull(results1);
        assertTrue(results1.isEmpty());

        List<IPingResult> results2 = ping.pingTargets(80);
        assertNotNull(results2);
        assertTrue(results2.isEmpty());
    }


    /**
     * Test ping with IPv6 loopback via bracket notation.
     *
     * @throws Exception In case of an error
     */
    @Test
    public void ipv6BracketNotationTest() throws Exception {
        int port = 9190;
        IHttpServer server = HttpServerFactory.getInstance().getServerInstance();
        server.start(new EchoService(), port);
        Thread.sleep(100L);

        try {
            IPing ping = PingFactory.getInstance().getPing(2000);
            List<IPingResult> results = ping.pingTargets(80, "[127.0.0.1]:" + port);

            assertEquals(1, results.size());
            assertTrue(results.get(0).isReachable());
            assertEquals("127.0.0.1", results.get(0).getHost());
            assertEquals(port, results.get(0).getPort());
        } finally {
            server.stop();
        }
    }


    /**
     * Test IPv6 bracket notation without port uses default.
     *
     * @throws Exception In case of an error
     */
    @Test
    public void ipv6BracketWithoutPortTest() throws Exception {
        int port = 9191;
        IHttpServer server = HttpServerFactory.getInstance().getServerInstance();
        server.start(new EchoService(), port);
        Thread.sleep(100L);

        try {
            IPing ping = PingFactory.getInstance().getPing(2000);
            // [127.0.0.1] without port — should use defaultPort
            List<IPingResult> results = ping.pingTargets(port, "[127.0.0.1]");

            assertEquals(1, results.size());
            assertTrue(results.get(0).isReachable());
            assertEquals(port, results.get(0).getPort());
        } finally {
            server.stop();
        }
    }


    /**
     * Test multiple unreachable hosts in parallel to ensure timeout works for batch.
     */
    @Test
    public void multipleUnreachableTest() {
        IPing ping = PingFactory.getInstance().getPing(500);
        long start = System.currentTimeMillis();
        List<IPingResult> results = ping.ping(19877, "localhost", "127.0.0.1");
        long elapsed = System.currentTimeMillis() - start;

        assertEquals(2, results.size());
        for (IPingResult r : results) {
            assertFalse(r.isReachable());
        }
        // Both should timeout in parallel, not sequentially (so < 2x timeout)
        assertTrue(elapsed < 1500, "Parallel timeout should be < 1500ms, was " + elapsed + "ms");
        LOG.info("Multiple unreachable completed in " + elapsed + "ms");
    }


    /**
     * Test host with invalid port string falls back to default port.
     *
     * @throws Exception In case of an error
     */
    @Test
    public void invalidPortStringTest() throws Exception {
        int port = 9192;
        IHttpServer server = HttpServerFactory.getInstance().getServerInstance();
        server.start(new EchoService(), port);
        Thread.sleep(100L);

        try {
            IPing ping = PingFactory.getInstance().getPing(2000);
            // "localhost:abc" should fall back to default port
            List<IPingResult> results = ping.pingTargets(port, "localhost:abc");
            assertEquals(1, results.size());
            // Should use default port since "abc" is not a number
            assertEquals(port, results.get(0).getPort());
        } finally {
            server.stop();
        }
    }


    /**
     * Test PingResult equals, hashCode, and toString.
     */
    @Test
    public void pingResultEqualsHashCodeTest() {
        PingResult r1 = new PingResult(HOST1, 80, true, 42, null);
        PingResult r2 = new PingResult(HOST1, 80, true, 42, null);
        final PingResult r3 = new PingResult("host2", 80, true, 42, null);
        final PingResult r4 = new PingResult(HOST1, 443, true, 42, null);
        final PingResult r5 = new PingResult(HOST1, 80, false, -1, new Exception("fail"));

        // equals
        assertEquals(r1, r2);
        assertEquals(r1, r1);
        assertFalse(r1.equals(null));
        assertFalse(r1.equals("string"));
        assertFalse(r1.equals(r3));
        assertFalse(r1.equals(r4));
        assertFalse(r1.equals(r5));

        // hashCode
        assertEquals(r1.hashCode(), r2.hashCode());

        // toString
        assertTrue(r1.toString().contains(HOST1));
        assertTrue(r1.toString().contains("80"));
        assertTrue(r1.toString().contains("42ms"));
        assertTrue(r1.toString().contains("reachable"));

        assertTrue(r5.toString().contains("unreachable"));
        assertTrue(r5.toString().contains("fail"));

        // getters
        assertEquals(HOST1, r1.getHost());
        assertEquals(80, r1.getPort());
        assertTrue(r1.isReachable());
        assertEquals(42, r1.getDuration());
        assertEquals(null, r1.getException());
        assertNotNull(r5.getException());
    }
}
