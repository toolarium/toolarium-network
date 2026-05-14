/*
 * DigTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.dig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toolarium.network.dig.dto.DigResult;
import com.github.toolarium.network.dig.dto.DnsRecord;
import com.github.toolarium.network.dig.dto.DnsRecordType;
import com.github.toolarium.network.dig.dto.IDigResult;
import com.github.toolarium.network.dig.dto.IDnsRecord;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for DNS dig functionality.
 *
 * @author patrick
 */
public class DigTest {
    private static final Logger LOG = LoggerFactory.getLogger(DigTest.class);
    private static final String LOCALHOST = "localhost";
    private static final String IP_10_0_0_1 = "10.0.0.1";


    /**
     * Test A record lookup for localhost.
     */
    @Test
    public void localhostARecordTest() {
        IDigResult result = DigFactory.getInstance().dig(LOCALHOST, DnsRecordType.A);
        assertNotNull(result);
        assertEquals(LOCALHOST, result.getQuery());
        assertEquals(DnsRecordType.A, result.getRecordType());
        // localhost may or may not have an A record depending on the system DNS config
        assertTrue(result.getDuration() >= 0);
        LOG.info("Localhost A record: " + result);
    }


    /**
     * Test dig with multiple record types.
     */
    @Test
    public void multipleRecordTypesTest() {
        IDig dig = DigFactory.getInstance().getDig(3000);
        List<IDigResult> results = dig.dig(LOCALHOST, DnsRecordType.A, DnsRecordType.AAAA);

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(DnsRecordType.A, results.get(0).getRecordType());
        assertEquals(DnsRecordType.AAAA, results.get(1).getRecordType());
        LOG.info("Multi record types: " + results);
    }


    /**
     * Test digAll queries all common record types.
     */
    @Test
    public void digAllTest() {
        IDig dig = DigFactory.getInstance().getDig(3000);
        List<IDigResult> results = dig.digAll(LOCALHOST);

        assertNotNull(results);
        // digAll queries A, AAAA, MX, CNAME, TXT, NS = 6 types
        assertEquals(6, results.size());
        LOG.info("Dig all: " + results.size() + " results");
        for (IDigResult r : results) {
            LOG.info("  " + r);
        }
    }


    /**
     * Test dig with null hostname.
     */
    @Test
    public void nullHostnameTest() {
        IDigResult result = DigFactory.getInstance().dig(null, DnsRecordType.A);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNotNull(result.getException());
    }


    /**
     * Test dig with empty hostname.
     */
    @Test
    public void emptyHostnameTest() {
        IDigResult result = DigFactory.getInstance().dig("  ", DnsRecordType.A);
        assertNotNull(result);
        assertFalse(result.isSuccess());
    }


    /**
     * Test dig with null record type.
     */
    @Test
    public void nullRecordTypeTest() {
        IDigResult result = DigFactory.getInstance().dig(LOCALHOST, (DnsRecordType) null);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNotNull(result.getException());
    }


    /**
     * Test dig with empty record types array.
     */
    @Test
    public void emptyRecordTypesArrayTest() {
        IDig dig = DigFactory.getInstance().getDig();
        List<IDigResult> results = dig.dig(LOCALHOST);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }


    /**
     * Test dig with unknown host.
     */
    @Test
    public void unknownHostTest() {
        IDigResult result = DigFactory.getInstance().dig("this.host.does.not.exist.invalid", DnsRecordType.A);
        assertNotNull(result);
        // May return success with empty records or failure depending on DNS config
        LOG.info("Unknown host dig: " + result);
    }


    /**
     * Test dig with custom DNS server.
     */
    @Test
    public void customDnsServerTest() {
        // Use Google's public DNS
        IDig dig = DigFactory.getInstance().getDig(3000, "8.8.8.8");
        IDigResult result = dig.dig("localhost", DnsRecordType.A);
        assertNotNull(result);
        LOG.info("Custom DNS server: " + result);
    }


    /**
     * Test DnsRecord equals, hashCode, toString.
     */
    @Test
    public void dnsRecordEqualsHashCodeTest() {
        DnsRecord r1 = new DnsRecord(DnsRecordType.A, IP_10_0_0_1);
        DnsRecord r2 = new DnsRecord(DnsRecordType.A, IP_10_0_0_1);
        final DnsRecord r3 = new DnsRecord(DnsRecordType.A, "10.0.0.2");
        final DnsRecord r4 = new DnsRecord(DnsRecordType.AAAA, IP_10_0_0_1);
        final DnsRecord r5 = new DnsRecord(DnsRecordType.MX, "mail.example.com", 10);

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

        // getters
        assertEquals(DnsRecordType.A, r1.getType());
        assertEquals(IP_10_0_0_1, r1.getValue());
        assertEquals(-1, r1.getPriority());
        assertEquals(10, r5.getPriority());

        // toString without priority
        String str1 = r1.toString();
        assertTrue(str1.contains("A"));
        assertTrue(str1.contains(IP_10_0_0_1));
        assertFalse(str1.contains("-1")); // priority not shown when -1

        // toString with priority
        String str5 = r5.toString();
        assertTrue(str5.contains("MX"));
        assertTrue(str5.contains("10"));
        assertTrue(str5.contains("mail.example.com"));
    }


    /**
     * Test DigResult equals, hashCode, toString.
     */
    @Test
    public void digResultEqualsHashCodeTest() {
        List<IDnsRecord> records = Arrays.asList(
                (IDnsRecord) new DnsRecord(DnsRecordType.A, IP_10_0_0_1));

        DigResult r1 = new DigResult("host1", DnsRecordType.A, records, true, 5, null);
        DigResult r2 = new DigResult("host1", DnsRecordType.A, records, true, 5, null);
        final DigResult r3 = new DigResult("host2", DnsRecordType.A, records, true, 5, null);
        final DigResult r4 = new DigResult("host1", DnsRecordType.A, Collections.emptyList(), false, 0, new Exception("fail"));

        // equals
        assertEquals(r1, r2);
        assertEquals(r1, r1);
        assertFalse(r1.equals(null));
        assertFalse(r1.equals("string"));
        assertFalse(r1.equals(r3));
        assertFalse(r1.equals(r4));

        // hashCode
        assertEquals(r1.hashCode(), r2.hashCode());

        // getters
        assertEquals("host1", r1.getQuery());
        assertEquals(DnsRecordType.A, r1.getRecordType());
        assertEquals(1, r1.getRecords().size());
        assertTrue(r1.isSuccess());
        assertEquals(5, r1.getDuration());
        assertNull(r1.getException());
        assertNotNull(r4.getException());

        // toString success
        String str1 = r1.toString();
        assertTrue(str1.contains("host1"));
        assertTrue(str1.contains("5ms"));

        // toString failure
        String str4 = r4.toString();
        assertTrue(str4.contains("FAILED"));
        assertTrue(str4.contains("fail"));
    }


    /**
     * Test DnsRecordType enum values.
     */
    @Test
    public void dnsRecordTypeEnumTest() {
        DnsRecordType[] types = DnsRecordType.values();
        assertEquals(9, types.length);
        assertEquals(DnsRecordType.A, DnsRecordType.valueOf("A"));
        assertEquals(DnsRecordType.AAAA, DnsRecordType.valueOf("AAAA"));
        assertEquals(DnsRecordType.MX, DnsRecordType.valueOf("MX"));
        assertEquals(DnsRecordType.CNAME, DnsRecordType.valueOf("CNAME"));
        assertEquals(DnsRecordType.TXT, DnsRecordType.valueOf("TXT"));
        assertEquals(DnsRecordType.NS, DnsRecordType.valueOf("NS"));
        assertEquals(DnsRecordType.SOA, DnsRecordType.valueOf("SOA"));
        assertEquals(DnsRecordType.PTR, DnsRecordType.valueOf("PTR"));
        assertEquals(DnsRecordType.SRV, DnsRecordType.valueOf("SRV"));
    }
}
