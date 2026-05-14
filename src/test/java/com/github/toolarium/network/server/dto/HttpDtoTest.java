/*
 * HttpDtoTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.server.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toolarium.security.keystore.SecurityManagerProviderFactory;
import com.github.toolarium.security.ssl.SSLContextFactory;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;


/**
 * Tests for HTTP server DTOs: HttpRequest, HttpReponse, HttpServerInformation.
 *
 * @author patrick
 */
public class HttpDtoTest {
    private static final String HTTP_1_1 = "HTTP/1.1";

    /**
     * Test HttpRequest getters, setters, equals, hashCode, toString.
     */
    @Test
    public void httpRequestTest() {
        HttpRequest r1 = new HttpRequest();
        final Date now = new Date();
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Host", "localhost");
        Map<String, String> params = new LinkedHashMap<>();
        params.put("key", "value");

        r1.setVersion(HTTP_1_1);
        r1.setMethod("GET");
        r1.setPath("/test");
        r1.setHeaders(headers);
        r1.setParameters(params);
        r1.setBaseLocation("/base");
        r1.setBody("hello");
        r1.setRequestTimestamp(now);

        // getters
        assertEquals(HTTP_1_1, r1.getVersion());
        assertEquals("GET", r1.getMethod());
        assertEquals("/test", r1.getPath());
        assertEquals("localhost", r1.getHeader("Host"));
        assertTrue(r1.containsHeader("Host"));
        assertNotNull(r1.getHeaders());
        assertEquals("value", r1.getParameter("key"));
        assertTrue(r1.containsParameter("key"));
        assertNotNull(r1.getParameters());
        assertEquals("/base", r1.getBaseLocation());
        assertEquals("hello", r1.getBody());
        assertEquals(now, r1.getRequestTimestamp());

        // equals with same values
        HttpRequest r2 = new HttpRequest();
        r2.setVersion(HTTP_1_1);
        r2.setMethod("GET");
        r2.setPath("/test");
        r2.setHeaders(headers);
        r2.setParameters(params);
        r2.setBaseLocation("/base");
        r2.setBody("hello");
        r2.setRequestTimestamp(now);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertEquals(r1, r1);
        assertFalse(r1.equals(null));
        assertFalse(r1.equals("string"));

        // not equals
        HttpRequest r3 = new HttpRequest();
        r3.setVersion("HTTP/2");
        r3.setMethod("POST");
        r3.setPath("/other");
        r3.setHeaders(Collections.emptyMap());
        r3.setParameters(Collections.emptyMap());
        r3.setBody("world");
        r3.setRequestTimestamp(new Date(0));
        assertNotEquals(r1, r3);

        // toString
        String str = r1.toString();
        assertTrue(str.contains("GET"));
        assertTrue(str.contains("/test"));
        assertTrue(str.contains("hello"));
    }


    /**
     * Test HttpReponse getters, setters, equals, hashCode, toString.
     */
    @Test
    public void httpResponseTest() {
        HttpReponse r1 = new HttpReponse();
        r1.setStatus(200);
        r1.setVersion(HTTP_1_1);
        r1.addHeader("Content-Type", "text/plain");
        r1.setBody("OK");

        // getters
        assertEquals(200, r1.getStatus());
        assertEquals(HTTP_1_1, r1.getVersion());
        assertEquals("text/plain", r1.getHeaders().get("Content-Type"));
        assertEquals("OK", r1.getBody());

        // equals with same values
        HttpReponse r2 = new HttpReponse();
        r2.setStatus(200);
        r2.setVersion(HTTP_1_1);
        r2.addHeader("Content-Type", "text/plain");
        r2.setBody("OK");

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertEquals(r1, r1);
        assertFalse(r1.equals(null));
        assertFalse(r1.equals("string"));

        // not equals
        HttpReponse r3 = new HttpReponse();
        r3.setStatus(404);
        r3.setBody("Not Found");
        assertNotEquals(r1, r3);

        // toString
        String str = r1.toString();
        assertTrue(str.contains("200"));
        assertTrue(str.contains("OK"));
    }


    /**
     * Test HttpServerInformation getters, setters, equals, hashCode, toString, getURI, getProtocol.
     */
    @Test
    public void httpServerInformationTest() {
        HttpServerInformation info1 = new HttpServerInformation();
        info1.setPort(8080);
        info1.setHostname("myhost");
        info1.setLocalIpAddress("192.168.1.1");

        // getters
        assertEquals(8080, info1.getPort());
        assertEquals("myhost", info1.getHostname());
        assertEquals("192.168.1.1", info1.getLocalIpAddress());
        assertEquals("http", info1.getProtocol());
        assertNull(info1.getSSLContext());

        // URI
        assertEquals("http://myhost:8080", info1.getURI().toString());

        // equals
        HttpServerInformation info2 = new HttpServerInformation();
        info2.setPort(8080);
        info2.setHostname("myhost");
        info2.setLocalIpAddress("192.168.1.1");

        assertEquals(info1, info2);
        assertEquals(info1.hashCode(), info2.hashCode());
        assertEquals(info1, info1);
        assertFalse(info1.equals(null));
        assertFalse(info1.equals("string"));

        // not equals
        HttpServerInformation info3 = new HttpServerInformation();
        info3.setHostname("other");
        info3.setLocalIpAddress("10.0.0.1");
        assertNotEquals(info1, info3);

        // toString
        String str = info1.toString();
        assertTrue(str.contains("myhost"));
        assertTrue(str.contains("192.168.1.1"));
    }


    /**
     * Test HttpServerInformation getProtocol returns "https" when SSLContext is set.
     *
     * @throws Exception In case of an error
     */
    @Test
    public void httpServerInformationHttpsProtocolTest() throws Exception {
        HttpServerInformation info = new HttpServerInformation();
        info.setPort(8443);
        info.setHostname("securehost");

        // Set an SSL context
        javax.net.ssl.SSLContext sslContext = SSLContextFactory.getInstance().createSslContext(
                SecurityManagerProviderFactory.getInstance().getSecurityManagerProvider("toolarium", "changit"));
        info.setSSLContext(sslContext);

        assertEquals("https", info.getProtocol());
        assertNotNull(info.getSSLContext());
        assertEquals("https://securehost:8443", info.getURI().toString());
    }
}
