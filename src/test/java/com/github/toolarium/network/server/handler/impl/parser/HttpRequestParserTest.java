/*
 * HttpRequestParserTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.server.handler.impl.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;


/**
 * Tests for HttpRequestParser — method, path, version, and query parameter parsing.
 *
 * @author patrick
 */
public class HttpRequestParserTest {

    /**
     * Test basic GET request line parsing.
     */
    @Test
    public void basicGetTest() {
        HttpRequestParser parser = new HttpRequestParser("GET /index.html HTTP/1.1");
        assertEquals("GET", parser.getMethod());
        assertEquals("/index.html", parser.getPath());
        assertEquals("HTTP/1.1", parser.getVersion());
        assertTrue(parser.getParameters().isEmpty());
    }


    /**
     * Test POST request line parsing.
     */
    @Test
    public void postRequestTest() {
        HttpRequestParser parser = new HttpRequestParser("POST /api/data HTTP/2");
        assertEquals("POST", parser.getMethod());
        assertEquals("/api/data", parser.getPath());
        assertEquals("HTTP/2", parser.getVersion());
    }


    /**
     * Test query parameter parsing.
     */
    @Test
    public void queryParameterTest() {
        HttpRequestParser parser = new HttpRequestParser("GET /search?q=hello&page=2 HTTP/1.1");
        assertEquals("GET", parser.getMethod());
        assertEquals("/search", parser.getPath());
        assertEquals("HTTP/1.1", parser.getVersion());

        Map<String, String> params = parser.getParameters();
        assertEquals(2, params.size());
        assertEquals("hello", params.get("q"));
        assertEquals("2", params.get("page"));
    }


    /**
     * Test URL-encoded parameter values.
     */
    @Test
    public void urlEncodedParameterTest() {
        HttpRequestParser parser = new HttpRequestParser("GET /search?name=hello%20world&email=test%40example.com HTTP/1.1");
        Map<String, String> params = parser.getParameters();

        assertEquals("hello world", params.get("name"));
        assertEquals("test@example.com", params.get("email"));
    }


    /**
     * Test multiple URL-encoded special characters.
     */
    @Test
    public void urlEncodedSpecialCharsTest() {
        HttpRequestParser parser = new HttpRequestParser("GET /test?val=a%3Db%26c%3Dd HTTP/1.1");
        Map<String, String> params = parser.getParameters();

        // %3D = '=', %26 = '&'
        assertEquals("a=b&c=d", params.get("val"));
    }


    /**
     * Test parameter with empty value.
     */
    @Test
    public void emptyParameterValueTest() {
        HttpRequestParser parser = new HttpRequestParser("GET /test?key= HTTP/1.1");
        Map<String, String> params = parser.getParameters();

        assertEquals(1, params.size());
        assertEquals("", params.get("key"));
    }


    /**
     * Test parameter without value (no equals sign).
     */
    @Test
    public void parameterWithoutValueTest() {
        HttpRequestParser parser = new HttpRequestParser("GET /test?flag HTTP/1.1");
        Map<String, String> params = parser.getParameters();

        assertEquals(1, params.size());
        assertEquals("", params.get("flag"));
    }


    /**
     * Test empty request line.
     */
    @Test
    public void emptyRequestTest() {
        HttpRequestParser parser = new HttpRequestParser("");
        assertEquals("", parser.getMethod());
        assertEquals("", parser.getPath());
        assertEquals("", parser.getVersion());
        assertTrue(parser.getParameters().isEmpty());
    }


    /**
     * Test request with only method (no path or version).
     */
    @Test
    public void methodOnlyTest() {
        HttpRequestParser parser = new HttpRequestParser("GET");
        assertEquals("GET", parser.getMethod());
        assertEquals("", parser.getPath());
        assertEquals("", parser.getVersion());
    }


    /**
     * Test path with multiple encoded characters.
     */
    @Test
    public void multipleEncodedCharsTest() {
        HttpRequestParser parser = new HttpRequestParser("GET /test?arr=%5Ba%2Cb%5D&hash=%23tag HTTP/1.1");
        Map<String, String> params = parser.getParameters();

        // %5B = '[', %2C = ',', %5D = ']', %23 = '#'
        assertEquals("[a,b]", params.get("arr"));
        assertEquals("#tag", params.get("hash"));
    }


    /**
     * Test colon-encoded and quote-encoded values.
     */
    @Test
    public void colonAndQuoteEncodedTest() {
        HttpRequestParser parser = new HttpRequestParser("GET /test?url=http%3A//host&q=%22hello%22 HTTP/1.1");
        Map<String, String> params = parser.getParameters();

        // %3A = ':', %22 = '"'
        assertEquals("http://host", params.get("url"));
        assertEquals("\"hello\"", params.get("q"));
    }
}
