/*
 * IPortScanListener.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.scanner.listener;

import com.github.toolarium.network.scanner.dto.IPortScanResult;


/**
 * Defines the port scanner listener.
 *
 * @author patrick
 */
public interface IPortScanListener {
    
    /**
     * Call back method after a port was visited
     *
     * @param portScanResult the port scan result
     */
    void visitedPort(IPortScanResult portScanResult);
}
