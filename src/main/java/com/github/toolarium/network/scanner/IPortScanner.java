/*
 * IPortScanner.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.scanner;

import com.github.toolarium.network.scanner.dto.IPortScanResult;
import com.github.toolarium.network.scanner.listener.IPortScanListener;
import java.util.List;


/**
 * Defines the interface of a port scanner
 *
 * @author patrick
 */
public interface IPortScanner {
    
    /** MIN_PORT */
    int MIN_PORT = 1;

    /** MAX_PORT */
    int MAX_PORT = 65535;


    /**
     * Scan a port range
     *
     * @param scanAddress the scan address
     * @param startPort the start port
     * @param endPort the end port
     * @param filterIsAvailable filter the output: true only available ports, false only not available ports, both: null
     * @param portScannerListeners a list of port scan listener
     * @return the port scan result list
     */
    List<IPortScanResult> scan(String scanAddress,
                               int startPort,
                               int endPort,
                               Boolean filterIsAvailable,
                               IPortScanListener... portScannerListeners);
}