/*
 * HttpClientUtilTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.httpclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toolarium.network.httpclient.dto.HttpClientResult;
import com.github.toolarium.network.httpclient.dto.IHttpClientResult;
import com.github.toolarium.network.server.HttpServerFactory;
import com.github.toolarium.network.server.IHttpServer;
import com.github.toolarium.network.server.service.EchoService;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for HTTP client utility.
 *
 * @author patrick
 */
public class HttpClientUtilTest {
    private static final Logger LOG = LoggerFactory.getLogger(HttpClientUtilTest.class);

    /**
     * Test GET request.
     *
     * @throws Exception In case of an error
     */
    @Test
    public void getRequestTest() throws Exception {
        int port = 9300;
        IHttpServer server = HttpServerFactory.getInstance().getServerInstance();
        server.start(new EchoService(), port);
        Thread.sleep(100L);

        try {
            IHttpClientResult result = HttpClientFactory.getInstance().get("http://localhost:" + port + "/hello");
            assertNotNull(result);
            assertEquals(200, result.getStatusCode());
            assertEquals("hello", result.getBody());
            assertTrue(result.isSuccess());
            assertEquals("GET", result.getMethod());
            assertTrue(result.getDuration() >= 0);
            assertNull(result.getException());
            assertNotNull(result.getHeaders());
            LOG.info("GET: " + result);
        } finally {
            server.stop();
        }
    }

    /**
     * Test GET request with headers.
     *
     * @throws Exception In case of an error
     */
    @Test
    public void getWithHeadersTest() throws Exception {
        int port = 9301;
        IHttpServer server = HttpServerFactory.getInstance().getServerInstance();
        server.start(new EchoService(), port);
        Thread.sleep(100L);

        try {
            Map<String, String> headers = new LinkedHashMap<>();
            headers.put("X-Custom", "test");
            IHttpClientResult result = HttpClientFactory.getInstance().getHttpClient(5000)
                    .get("http://localhost:" + port + "/test", headers);
            assertNotNull(result);
            assertEquals(200, result.getStatusCode());
        } finally {
            server.stop();
        }
    }

    /**
     * Test POST request.
     *
     * @throws Exception In case of an error
     */
    @Test
    public void postRequestTest() throws Exception {
        int port = 9302;
        IHttpServer server = HttpServerFactory.getInstance().getServerInstance();
        server.start(new EchoService(), port);
        Thread.sleep(100L);

        try {
            IHttpClientResult result = HttpClientFactory.getInstance()
                    .post("http://localhost:" + port, "hello world", "text/plain");
            assertNotNull(result);
            assertEquals(200, result.getStatusCode());
            assertEquals("hello world", result.getBody());
            assertEquals("POST", result.getMethod());
            LOG.info("POST: " + result);
        } finally {
            server.stop();
        }
    }

    /**
     * Test PUT request.
     *
     * @throws Exception In case of an error
     */
    @Test
    public void putRequestTest() throws Exception {
        int port = 9303;
        IHttpServer server = HttpServerFactory.getInstance().getServerInstance();
        server.start(new EchoService(), port);
        Thread.sleep(100L);

        try {
            IHttpClientResult result = HttpClientFactory.getInstance().getHttpClient(5000)
                    .put("http://localhost:" + port, "update", "text/plain");
            assertNotNull(result);
            assertEquals(200, result.getStatusCode());
            assertEquals("PUT", result.getMethod());
        } finally {
            server.stop();
        }
    }

    /**
     * Test DELETE request.
     *
     * @throws Exception In case of an error
     */
    @Test
    public void deleteRequestTest() throws Exception {
        int port = 9304;
        IHttpServer server = HttpServerFactory.getInstance().getServerInstance();
        server.start(new EchoService(), port);
        Thread.sleep(100L);

        try {
            IHttpClientResult result = HttpClientFactory.getInstance().getHttpClient(5000)
                    .delete("http://localhost:" + port + "/resource");
            assertNotNull(result);
            assertEquals(200, result.getStatusCode());
            assertEquals("DELETE", result.getMethod());
        } finally {
            server.stop();
        }
    }

    /**
     * Test null URL returns failure.
     */
    @Test
    public void nullUrlTest() {
        IHttpClientResult result = HttpClientFactory.getInstance().get(null);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNotNull(result.getException());
    }

    /**
     * Test unreachable URL returns failure.
     */
    @Test
    public void unreachableUrlTest() {
        IHttpClientResult result = HttpClientFactory.getInstance().getHttpClient(1000)
                .get("http://localhost:19876/test");
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(-1, result.getStatusCode());
    }

    /**
     * Test HttpClientResult DTO equals, hashCode, toString.
     */
    @Test
    public void httpClientResultDtoTest() {
        HttpClientResult r1 = new HttpClientResult("http://example.com", "GET", 200, "OK",
                Collections.emptyMap(), 50);
        HttpClientResult r2 = new HttpClientResult("http://example.com", "GET", 200, "OK",
                Collections.emptyMap(), 50);
        HttpClientResult r3 = new HttpClientResult("http://other.com", "POST", 0, new Exception("fail"));

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertFalse(r1.equals(r3));
        assertFalse(r1.equals(null));
        assertFalse(r1.equals("string"));

        assertTrue(r1.isSuccess());
        assertFalse(r3.isSuccess());

        assertTrue(r1.toString().contains("200"));
        assertTrue(r1.toString().contains("GET"));
        assertTrue(r3.toString().contains("FAILED"));
    }
}
