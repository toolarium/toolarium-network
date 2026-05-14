/*
 * PingTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.ping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toolarium.network.ping.dto.IPingResult;
import com.github.toolarium.network.server.HttpServerFactory;
import com.github.toolarium.network.server.IHttpServer;
import com.github.toolarium.network.server.service.EchoService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for TCP ping functionality.
 *
 * @author patrick
 */
public class PingTest {
    private static final Logger LOG = LoggerFactory.getLogger(PingTest.class);


    /**
     * Test single host ping against a local server.
     *
     * @throws Exception In case of an error
     */
    @Test
    public void singleHostPingTest() throws Exception {
        int port = 9090;
        IHttpServer server = HttpServerFactory.getInstance().getServerInstance();
        server.start(new EchoService(), port);
        Thread.sleep(100L);

        try {
            IPingResult result = PingFactory.getInstance().ping("localhost", port);
            assertNotNull(result);
            assertEquals("localhost", result.getHost());
            assertEquals(port, result.getPort());
            assertTrue(result.isReachable());
            assertTrue(result.getDuration() >= 0);
            LOG.info("Single ping: " + result);
        } finally {
            server.stop();
        }
    }


    /**
     * Test multi-host ping against local servers.
     *
     * @throws Exception In case of an error
     */
    @Test
    public void multiHostPingTest() throws Exception {
        int port1 = 9091;
        int port2 = 9092;
        IHttpServer server1 = HttpServerFactory.getInstance().getServerInstance();
        server1.start(new EchoService(), port1);
        IHttpServer server2 = HttpServerFactory.getInstance().getServerInstance();
        server2.start(new EchoService(), port2);
        Thread.sleep(100L);

        try {
            IPing ping = PingFactory.getInstance().getPing(2000);
            List<IPingResult> results = ping.pingTargets(port1,
                    "localhost:" + port1,
                    "localhost:" + port2);

            assertNotNull(results);
            assertEquals(2, results.size());
            for (IPingResult result : results) {
                assertTrue(result.isReachable(), "Expected reachable: " + result);
                assertTrue(result.getDuration() >= 0);
                LOG.info("Multi ping: " + result);
            }
        } finally {
            server1.stop();
            server2.stop();
        }
    }


    /**
     * Test ping to an unreachable port times out.
     *
     * @throws Exception In case of an error
     */
    @Test
    public void unreachableHostPingTest() throws Exception {
        // Port 19999 should not have anything listening
        IPing ping = PingFactory.getInstance().getPing(1000);
        IPingResult result = ping.ping("localhost", 19999);

        assertNotNull(result);
        assertFalse(result.isReachable());
        LOG.info("Unreachable ping: " + result);
    }


    /**
     * Test convenience methods on PingFactory.
     *
     * @throws Exception In case of an error
     */
    @Test
    public void factoryConvenienceTest() throws Exception {
        int port = 9093;
        IHttpServer server = HttpServerFactory.getInstance().getServerInstance();
        server.start(new EchoService(), port);
        Thread.sleep(100L);

        try {
            List<IPingResult> results = PingFactory.getInstance().ping(port, "localhost");
            assertNotNull(results);
            assertEquals(1, results.size());
            assertTrue(results.get(0).isReachable());
        } finally {
            server.stop();
        }
    }


    /**
     * Test target parsing with host:port format via pingTargets.
     *
     * @throws Exception In case of an error
     */
    @Test
    public void targetParsingTest() throws Exception {
        int port1 = 9094;
        int port2 = 9095;
        IHttpServer server1 = HttpServerFactory.getInstance().getServerInstance();
        server1.start(new EchoService(), port1);
        IHttpServer server2 = HttpServerFactory.getInstance().getServerInstance();
        server2.start(new EchoService(), port2);
        Thread.sleep(100L);

        try {
            IPing ping = PingFactory.getInstance().getPing(2000);

            // Test host:port parsing — different ports per target
            List<IPingResult> results = ping.pingTargets(port1,
                    "localhost:" + port1,
                    "localhost:" + port2);

            assertEquals(2, results.size());
            for (IPingResult result : results) {
                assertTrue(result.isReachable(), "Expected reachable: " + result);
                assertEquals("localhost", result.getHost());
            }

            // Verify both ports were pinged
            boolean foundPort1 = false;
            boolean foundPort2 = false;
            for (IPingResult r : results) {
                if (r.getPort() == port1) {
                    foundPort1 = true;
                }
                if (r.getPort() == port2) {
                    foundPort2 = true;
                }
            }
            assertTrue(foundPort1, "Should have pinged port " + port1);
            assertTrue(foundPort2, "Should have pinged port " + port2);
        } finally {
            server1.stop();
            server2.stop();
        }
    }
}
