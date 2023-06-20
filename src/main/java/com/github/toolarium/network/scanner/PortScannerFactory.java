/*
 * PortScannerFactory.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.scanner;

import com.github.toolarium.network.scanner.dto.IPortScanResult;
import com.github.toolarium.network.scanner.impl.PortScannerImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Defines the port scan factory
 *
 * @author patrick
 */
public final class PortScannerFactory {
    
    /**
     * Private class, the only instance of the singleton which will be created by accessing the holder class.
     *
     * @author patrick
     */
    private static class HOLDER {
        static final PortScannerFactory INSTANCE = new PortScannerFactory();
    }

    
    /**
     * Constructor
     */
    private PortScannerFactory() {
        // NOP
    }

    
    /**
     * Get the instance
     *
     * @return the instance
     */
    public static PortScannerFactory getInstance() {
        return HOLDER.INSTANCE;
    }



    /**
     * Get port scanner instance
     *
     * @param timeout the timeout of a port scan
     * @return the port scanner
     */
    public IPortScanner getPortScanner(int timeout) {
        return new PortScannerImpl(1, timeout);
    }


    /**
     * Get port scanner instance
     *
     * @param numberOfThreads the number of threads to use for port scanning
     * @param timeout the timeout of a port scan
     * @return the port scanner
     */
    public IPortScanner getPortScanner(int numberOfThreads, int timeout) {
        return new PortScannerImpl(numberOfThreads, timeout);
    }


    /**
     * Scan open ports
     *
     * @param scanAddress the scan address or scan address range (CIDR notation)
     * @param startPort the start port
     * @param endPort the end port
     * @param numberOfThreads the number of threads
     * @param timeout the timeout
     * @return the result set where the key corresponds to the host and the value the port list
     */
    public Map<String, List<Integer>> scanOpenPorts(String scanAddress, int startPort, int endPort, int numberOfThreads, int timeout) {
        return scanPorts(scanAddress, startPort, endPort, numberOfThreads, timeout, Boolean.TRUE);
    }


    /**
     * Scan closed ports
     *
     * @param scanAddress the scan address or scan address range (CIDR notation)
     * @param startPort the start port
     * @param endPort the end port
     * @param numberOfThreads the number of threads
     * @param timeout the timeout
     * @return the result set where the key corresponds to the host and the value the port list
     */
    public Map<String, List<Integer>> scanClosedPorts(String scanAddress, int startPort, int endPort, int numberOfThreads, int timeout) {
        return scanPorts(scanAddress, startPort, endPort, numberOfThreads, timeout, Boolean.FALSE);
    }


    /**
     * Scan ports
     *
     * @param scanAddress the scan address or scan address range (CIDR notation)
     * @param startPort the start port
     * @param endPort the end port
     * @param numberOfThreads the number of threads
     * @param timeout the timeout
     * @param filterIsAvailable filter the output: true only available ports, false only not available ports, both: null
     * @return the result set where the key corresponds to the host and the value the port list
     */
    private Map<String, List<Integer>> scanPorts(String scanAddress, int startPort, int endPort, int numberOfThreads, int timeout, Boolean filterIsAvailable) {
        List<IPortScanResult> portScanResultList = getPortScanner(numberOfThreads, timeout).scan(scanAddress, startPort, endPort, filterIsAvailable);

        Map<String, List<Integer>> hostPortMap = new HashMap<String, List<Integer>>();
        for (IPortScanResult r : portScanResultList) {
            List<Integer> openPorts = hostPortMap.get(r.getHostAddress());
            if (openPorts == null) {
                openPorts = new ArrayList<Integer>();
                hostPortMap.put(r.getHostAddress(), openPorts);
            }

            openPorts.add(r.getPort());
        }

        return hostPortMap;
    }
}
