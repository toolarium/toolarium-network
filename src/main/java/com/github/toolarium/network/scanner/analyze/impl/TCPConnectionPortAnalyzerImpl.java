/*
 * TCPConnectionPortAnalyzerImpl.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.scanner.analyze.impl;

import com.github.toolarium.network.scanner.analyze.IPortAnalyzer;
import com.github.toolarium.network.scanner.dto.IPortScanResult;
import com.github.toolarium.network.scanner.dto.PortScanResult;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;


/**
 * Implements a tcp connection port analyzer.
 *
 * @author patrick
 */
public class TCPConnectionPortAnalyzerImpl implements IPortAnalyzer {
    private int timeout;


    /**
     * Constructor
     *
     * @param timeout the timeout
     */
    public TCPConnectionPortAnalyzerImpl(int timeout) {
        this.timeout = timeout;
    }


    /**
     * @see com.github.toolarium.network.scanner.analyze.IPortAnalyzer#analyzePort(java.lang.String, int)
     */
    @Override
    public IPortScanResult analyzePort(String scanAddress, int port) {
        Socket socket = new Socket();

        try {
            socket.connect(new InetSocketAddress(scanAddress, port), timeout);
            return new PortScanResult(scanAddress, port, true);
        } catch (Exception ex) {
            // nop
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                // NOP
            } finally {
                // NOP
            }
        }

        return new PortScanResult(scanAddress, port, false);
    }
}
