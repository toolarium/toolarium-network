/*
 * HttpServerUtilTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.server.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import org.junit.jupiter.api.Test;


/**
 * Tests for HttpStatusUtil and HttpHeaderUtil.
 *
 * @author patrick
 */
public class HttpServerUtilTest {

    // ---- HttpStatusUtil ----

    /**
     * Test all known status codes.
     */
    @Test
    public void statusCodeTest() {
        assertEquals("OK", HttpStatusUtil.getInstance().getStatusText(200));
        assertEquals("CREATED", HttpStatusUtil.getInstance().getStatusText(201));
        assertEquals("NO CONTENT", HttpStatusUtil.getInstance().getStatusText(204));
        assertEquals("MOVED", HttpStatusUtil.getInstance().getStatusText(300));
        assertEquals("BAD REQUEST", HttpStatusUtil.getInstance().getStatusText(400));
        assertEquals("UNAUTHORIZED", HttpStatusUtil.getInstance().getStatusText(401));
        assertEquals("FORBIDDEN", HttpStatusUtil.getInstance().getStatusText(403));
        assertEquals("NOT FOUND", HttpStatusUtil.getInstance().getStatusText(404));
        assertEquals("Method Not Allowed", HttpStatusUtil.getInstance().getStatusText(405));
        assertEquals("TIMEOUT", HttpStatusUtil.getInstance().getStatusText(408));
        assertEquals("CONFLICT", HttpStatusUtil.getInstance().getStatusText(409));
        assertEquals("TOO LARGE", HttpStatusUtil.getInstance().getStatusText(413));
        assertEquals("INTERNAL SERVER ERROR", HttpStatusUtil.getInstance().getStatusText(500));
        assertEquals("SERVICE UNAVAILABLE", HttpStatusUtil.getInstance().getStatusText(503));
    }


    /**
     * Test unknown status code returns default "OK".
     */
    @Test
    public void unknownStatusCodeTest() {
        assertEquals("OK", HttpStatusUtil.getInstance().getStatusText(999));
        assertEquals("OK", HttpStatusUtil.getInstance().getStatusText(0));
        assertEquals("OK", HttpStatusUtil.getInstance().getStatusText(-1));
        assertEquals("OK", HttpStatusUtil.getInstance().getStatusText(418)); // I'm a teapot — not in the map
    }


    // ---- HttpHeaderUtil ----

    /**
     * Test reading standard headers.
     *
     * @throws IOException In case of an error
     */
    @Test
    public void readHeadersTest() throws IOException {
        String raw = "Host: localhost\r\n"
                + "Content-Type: text/html\r\n"
                + "Content-Length: 42\r\n"
                + "\r\n";

        BufferedReader reader = new BufferedReader(new StringReader(raw));
        Map<String, String> headers = HttpHeaderUtil.getInstance().readHeaders(reader);

        assertNotNull(headers);
        assertEquals(3, headers.size());
        assertEquals("localhost", headers.get("Host"));
        assertEquals("text/html", headers.get("Content-Type"));
        assertEquals("42", headers.get("Content-Length"));
    }


    /**
     * Test reading empty headers (just blank line).
     *
     * @throws IOException In case of an error
     */
    @Test
    public void readEmptyHeadersTest() throws IOException {
        String raw = "\r\n";
        BufferedReader reader = new BufferedReader(new StringReader(raw));
        Map<String, String> headers = HttpHeaderUtil.getInstance().readHeaders(reader);

        assertNotNull(headers);
        assertTrue(headers.isEmpty());
    }


    /**
     * Test reading headers with null reader.
     *
     * @throws IOException In case of an error
     */
    @Test
    public void readHeadersNullReaderTest() throws IOException {
        Map<String, String> headers = HttpHeaderUtil.getInstance().readHeaders(null);
        assertNull(headers);
    }


    /**
     * Test reading headers with colon in value (e.g. URL value).
     *
     * @throws IOException In case of an error
     */
    @Test
    public void readHeadersWithColonInValueTest() throws IOException {
        String raw = "Location: http://example.com:8080/path\r\n"
                + "\r\n";

        BufferedReader reader = new BufferedReader(new StringReader(raw));
        Map<String, String> headers = HttpHeaderUtil.getInstance().readHeaders(reader);

        assertNotNull(headers);
        assertEquals(1, headers.size());
        // Value should contain everything after first colon
        assertEquals("http://example.com:8080/path", headers.get("Location"));
    }


    /**
     * Test header constant values exist.
     */
    @Test
    public void headerConstantsTest() {
        assertEquals("Allow", HttpHeaderUtil.ALLOW);
        assertEquals("Authorization", HttpHeaderUtil.AUTHORIZATION);
        assertEquals("Content-Range", HttpHeaderUtil.CONTENT_RANGE);
        assertEquals("Host", HttpHeaderUtil.HOST);
        assertEquals("If-Match", HttpHeaderUtil.IF_MATCH);
        assertEquals("Location", HttpHeaderUtil.LOCATION);
        assertEquals("Range", HttpHeaderUtil.RANGE);
        assertEquals("Content-Length", HttpHeaderUtil.CONTENT_LENGTH);
        assertEquals("WWW-Authenticate", HttpHeaderUtil.WWW_AUTHENTICATE);
        assertEquals("Date", HttpHeaderUtil.DATE);
        assertEquals("Last-Modified", HttpHeaderUtil.LAST_MODIFIED);
    }
}
