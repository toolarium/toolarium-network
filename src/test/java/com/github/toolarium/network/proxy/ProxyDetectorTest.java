/*
 * ProxyDetectorTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toolarium.network.proxy.dto.IProxyInfo;
import com.github.toolarium.network.proxy.dto.ProxyInfo;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for ProxyDetector.
 *
 * @author patrick
 */
public class ProxyDetectorTest {
    private static final Logger LOG = LoggerFactory.getLogger(ProxyDetectorTest.class);

    /**
     * Test detection of HTTP proxies.
     */
    @Test
    public void detectHttpProxiesTest() {
        List<IProxyInfo> proxies = ProxyDetector.getInstance().detectHttpProxies();
        assertNotNull(proxies);
        assertFalse(proxies.isEmpty());
        LOG.info("HTTP proxies: " + proxies);
    }

    /**
     * Test detection of HTTPS proxies.
     */
    @Test
    public void detectHttpsProxiesTest() {
        List<IProxyInfo> proxies = ProxyDetector.getInstance().detectHttpsProxies();
        assertNotNull(proxies);
        assertFalse(proxies.isEmpty());
        LOG.info("HTTPS proxies: " + proxies);
    }

    /**
     * Test detection of proxies for a specific URL.
     */
    @Test
    public void detectProxiesForUrlTest() {
        List<IProxyInfo> proxies = ProxyDetector.getInstance().detectProxies("http://localhost:8080");
        assertNotNull(proxies);
        assertFalse(proxies.isEmpty());
    }

    /**
     * Test that a null URL returns a direct proxy.
     */
    @Test
    public void nullUrlTest() {
        List<IProxyInfo> proxies = ProxyDetector.getInstance().detectProxies(null);
        assertNotNull(proxies);
        assertEquals(1, proxies.size());
        assertTrue(proxies.get(0).isDirect());
    }

    /**
     * Test that an empty URL returns a direct proxy.
     */
    @Test
    public void emptyUrlTest() {
        List<IProxyInfo> proxies = ProxyDetector.getInstance().detectProxies("  ");
        assertNotNull(proxies);
        assertEquals(1, proxies.size());
        assertTrue(proxies.get(0).isDirect());
    }

    /**
     * Test hasProxy check does not throw.
     */
    @Test
    public void hasProxyTest() {
        // On most dev machines, there's no proxy configured for localhost
        boolean hasProxy = ProxyDetector.getInstance().hasProxy("http://localhost");
        LOG.info("Has proxy for localhost: " + hasProxy);
        // Just verify it doesn't crash
    }

    /**
     * Test ProxyInfo DTO equals, hashCode, toString.
     */
    @Test
    public void proxyInfoDtoTest() {
        ProxyInfo direct = new ProxyInfo("DIRECT", null, -1);
        ProxyInfo http = new ProxyInfo("HTTP", "proxy.example.com", 8080);
        ProxyInfo socks = new ProxyInfo("SOCKS", "socks.example.com", 1080);
        final ProxyInfo http2 = new ProxyInfo("HTTP", "proxy.example.com", 8080);

        assertTrue(direct.isDirect());
        assertFalse(http.isDirect());
        assertFalse(socks.isDirect());

        assertEquals("DIRECT", direct.getType());
        assertEquals(null, direct.getHost());
        assertEquals(-1, direct.getPort());

        assertEquals("HTTP", http.getType());
        assertEquals("proxy.example.com", http.getHost());
        assertEquals(8080, http.getPort());

        assertEquals(http, http2);
        assertEquals(http.hashCode(), http2.hashCode());
        assertFalse(http.equals(direct));
        assertFalse(http.equals(null));
        assertFalse(http.equals("string"));

        assertTrue(direct.toString().contains("DIRECT"));
        assertTrue(http.toString().contains("proxy.example.com"));
        assertTrue(http.toString().contains("8080"));
    }
}
