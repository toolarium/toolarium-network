/*
 * IPV6FormatterTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.ip.formatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;


/**
 * Test IPV6 formatting
 *  
 * @author patrick
 */
public class IPV6FormatterTest {
    
    /**
     * Test format ipvv6 address
     */
    @Test
    public void formatIPV6Base() {
        assertEqualsIPFormat("::1", "0000:0000:0000:0000:0000:0000:0000:0001");
        assertEqualsIPFormat("::", "0000:0000:0000:0000:0000:0000:0000:0000");
        assertEqualsIPFormat("::", "::");
        assertEqualsIPFormat("::1", "::1");
        
        assertEqualsIPFormat("0:1200::", "0000:1200:0000:0000:0000:0000:0000:0000");
        assertEqualsIPFormat("::1200:0:0:0:8351", "0000:0000:0000:1200:0000:0000:0000:8351");
        assertEqualsIPFormat("0:125f:0:94dd:e53f:0:61a9:0", "0000:125f:0000:94dd:e53f:0000:61a9:0000");
        assertEqualsIPFormat("0:5f:0:94dd:0:cfe7:0:8351", "0000:005f:0000:94dd:0000:cfe7:0000:8351");
    }

    
    /**
     * Test format ipvv6 address
     */
    @Test
    public void formatIPV6() {
        assertEqualsIPFormat("7469:125f:8eb6:94dd:e53f:cfe7:61a9:8351", "7469:125f:8eb6:94dd:e53f:cfe7:61a9:8351");
        assertEqualsIPFormat("7469:125f::e53f:cfe7:0:0", "7469:125f:0000:0000:e53f:cfe7:0000:0000");
        assertEqualsIPFormat("7469:125f::f:c000:0:0", "7469:125f:0000:0000:000f:c000:0000:0000");
        assertEqualsIPFormat("7469:125f::f:c000:0:0", "7469:125f:0000:0000:000f:c000:0000:0000");
        assertEqualsIPFormat("7469:0:0:94dd::8351", "7469:0000:0000:94dd:0000:0000:0000:8351");
        assertEqualsIPFormat("469:125f:8eb6:94dd:0:cfe7:61a9:8351", "0469:125f:8eb6:94dd:0000:cfe7:61a9:8351");
        assertEqualsIPFormat("69:125f:8eb6:94dd:0:cfe7:61a9:8351", "0069:125f:8eb6:94dd:0000:cfe7:61a9:8351");
        assertEqualsIPFormat("9:125f:8eb6:94dd:0:cfe7:61a9:8351", "0009:125f:8eb6:94dd:0000:cfe7:61a9:8351");
        assertEqualsIPFormat("::8eb6:94dd:e53f:7:6009:8350", "0000:0000:8eb6:94dd:e53f:0007:6009:8350");
        assertEqualsIPFormat("::8eb6:94dd:e53f:7:6009:8300", "0000:0000:8eb6:94dd:e53f:0007:6009:8300");
        assertEqualsIPFormat("::8eb6:94dd:e53f:7:6009:8000", "0000:0000:8eb6:94dd:e53f:0007:6009:8000");
        assertEqualsIPFormat("7469::e53f:0:0:8300", "7469:0000:0000:0000:e53f:0000:0000:8300");
        assertEqualsIPFormat("7009:100f:8eb6:94dd:e000:cfe7:6009:8351", "7009:100f:8eb6:94dd:e000:cfe7:6009:8351");
        assertEqualsIPFormat("7469:100f:8006:900d:e53f:cfe7:61a9:8351", "7469:100f:8006:900d:e53f:cfe7:61a9:8351");
        assertEqualsIPFormat("7000:1200:8e00:94dd:e53f:cfe7:0:1", "7000:1200:8e00:94dd:e53f:cfe7:0000:0001");
        assertEqualsIPFormat("0:0:0:94dd::", "0000:0000:0000:94dd:0000:0000:0000:0000");
        assertEqualsIPFormat("0:1200::", "0000:1200:0000:0000:0000:0000:0000:0000");
        assertEqualsIPFormat("::1200:0:0:0:8351", "0000:0000:0000:1200:0000:0000:0000:8351");
        assertEqualsIPFormat("0:125f:0:94dd:e53f:0:61a9:0", "0000:125f:0000:94dd:e53f:0000:61a9:0000");
        assertEqualsIPFormat("7469:0:8eb6:0:e53f:0:61a9:0", "7469:0000:8eb6:0000:e53f:0000:61a9:0000");
        assertEqualsIPFormat("0:125f:0:94dd:0:cfe7:0:8351", "0000:125f:0000:94dd:0000:cfe7:0000:8351");
        assertEqualsIPFormat("0:25f:0:94dd:0:cfe7:0:8351", "0000:025f:0000:94dd:0000:cfe7:0000:8351");
        assertEqualsIPFormat("0:5f:0:94dd:0:cfe7:0:8351", "0000:005f:0000:94dd:0000:cfe7:0000:8351");
        assertEqualsIPFormat("0:f:0:94dd:0:cfe7:0:8351", "0000:000f:0000:94dd:0000:cfe7:0000:8351");

        assertEqualsIPFormat("2001:db8::2:1", "2001:db8:0:0:0:0:2:1");
        assertEqualsIPFormat("2001:db8:0:1:1:1:1:1", "2001:db8:0:1:1:1:1:1");
        assertEqualsIPFormat("2001:0:0:1::1", "2001:0:0:1:0:0:0:1");
        assertEqualsIPFormat("2001:db8::1:0:0:1", "2001:db8:0:0:1:0:0:1");
        
        assertEqualsIPFormat("2001:db8::1:0:0:1", "2001:db8::1:0:0:1");
        assertEqualsIPFormat("0:0:0:94dd::", "0:0:0:94dd::");
        assertEqualsIPFormat("::1200:0:0:0:8351", "::1200:0:0:0:8351");
                      
    }

    
    /**
     * Asert ipv6 formatter
     *
     * @param expectedValue the expected value
     * @param testValue the test value
     */
    protected void assertEqualsIPFormat(String expectedValue, String testValue) {
        String formattedValue = new IPV6Formatter().format(testValue);
        assertEquals(expectedValue, formattedValue);
        assertEquals(expectedValue, new IPV6Formatter().format(formattedValue));
    }
}
