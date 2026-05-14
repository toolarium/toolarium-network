/*
 * WhoisTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.whois;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toolarium.network.whois.dto.IWhoisResult;
import com.github.toolarium.network.whois.dto.WhoisResult;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for WHOIS functionality.
 *
 * @author patrick
 */
public class WhoisTest {
    private static final Logger LOG = LoggerFactory.getLogger(WhoisTest.class);

    /**
     * Test null query returns failure.
     */
    @Test
    public void nullQueryTest() {
        IWhoisResult result = WhoisFactory.getInstance().query(null);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNotNull(result.getException());
    }

    /**
     * Test empty query returns failure.
     */
    @Test
    public void emptyQueryTest() {
        IWhoisResult result = WhoisFactory.getInstance().query("  ");
        assertNotNull(result);
        assertFalse(result.isSuccess());
    }

    /**
     * Test custom timeout configuration.
     */
    @Test
    public void customTimeoutTest() {
        IWhois whois = WhoisFactory.getInstance().getWhois(3000);
        assertNotNull(whois);
    }

    /**
     * Test custom WHOIS server query.
     */
    @Test
    public void customServerTest() {
        IWhois whois = WhoisFactory.getInstance().getWhois(3000);
        // Query against a specific server — just verify it doesn't crash
        IWhoisResult result = whois.query("localhost", "localhost");
        assertNotNull(result);
        // Will likely fail since localhost isn't a WHOIS server, but should handle gracefully
        assertNotNull(result.getQuery());
    }

    /**
     * Test WhoisResult DTO equals, hashCode, toString.
     */
    @Test
    public void whoisResultDtoTest() {
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("Domain", "example.com");

        WhoisResult r1 = new WhoisResult("example.com", "whois.iana.org", "raw", fields, true, 100, null);
        WhoisResult r2 = new WhoisResult("example.com", "whois.iana.org", "raw", fields, true, 100, null);
        WhoisResult r3 = new WhoisResult("other.com", "whois.iana.org", null, Collections.emptyMap(), false, 0, new Exception("fail"));

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertFalse(r1.equals(r3));
        assertFalse(r1.equals(null));
        assertFalse(r1.equals("string"));

        assertEquals("example.com", r1.getQuery());
        assertEquals("whois.iana.org", r1.getWhoisServer());
        assertEquals("raw", r1.getRawResponse());
        assertEquals(1, r1.getFields().size());
        assertTrue(r1.isSuccess());
        assertEquals(100, r1.getDuration());
        assertNull(r1.getException());

        assertTrue(r1.toString().contains("example.com"));
        assertTrue(r1.toString().contains("100ms"));
        assertTrue(r3.toString().contains("FAILED"));
    }
}
