/*
 * IPortAnalyzer.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.scanner.analyze;

import com.github.toolarium.network.scanner.dto.IPortScanResult;


/**
 * Defines a port analyzer.
 *
 * @author patrick
 */
public interface IPortAnalyzer {
    
    /**
     * Analyze a port
     *
     * @param hostAddress the host address
     * @param port the port to analyze
     * @return the port analyze result
     */
    IPortScanResult analyzePort(String hostAddress, int port);
}
