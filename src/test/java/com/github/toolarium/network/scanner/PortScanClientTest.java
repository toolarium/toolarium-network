/*
 * PortScanClientTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.scanner;

import org.junit.jupiter.api.Test;


/**
 * 
 * @author patrick
 */
public class PortScanClientTest {
    
    /**
     * Test
     */
    @Test 
    public void test() {
        PortScannerClient.main(new String[] {"--startPort=10", "--endPort=12"});
    }
    
}
