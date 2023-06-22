/*
 * PortScannerImpl.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.scanner.impl;


import com.github.toolarium.network.ip.CIDRUtil;
import com.github.toolarium.network.ip.IPUtil;
import com.github.toolarium.network.scanner.IPortScanner;
import com.github.toolarium.network.scanner.analyze.IPortAnalyzer;
import com.github.toolarium.network.scanner.analyze.impl.TCPConnectionPortAnalyzerImpl;
import com.github.toolarium.network.scanner.dto.IPortScanResult;
import com.github.toolarium.network.scanner.listener.IPortScanListener;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;


/**
 * Implements the {@link IPortScanner}.
 *
 * @author patrick
 */
public class PortScannerImpl implements IPortScanner {
    private static final Logger LOG = Logger.getLogger(PortScannerImpl.class.getName());
    private int numberOfThreads;
    private int timeout;


    /**
     * Constructor
     */
    public PortScannerImpl() {
        this(20, 200);
    }


    /**
     * Constructor
     *
     * @param numberOfThreads the number of threads
     * @param timeout the scan timeout
     */
    public PortScannerImpl(int numberOfThreads, int timeout) {
        this.numberOfThreads = numberOfThreads;
        this.timeout = timeout;
    }


    /**
     * @see com.github.toolarium.network.scanner.IPortScanner#scan(java.lang.String, int, int, java.lang.Boolean, com.github.toolarium.network.scanner.listener.IPortScanListener[])
     */
    @Override
    public List<IPortScanResult> scan(String scanAddress, int inputStartPort, int inputEndPort, Boolean filterIsAvailable, IPortScanListener... portScannerListeners) {
        // validate input
        final int startPort = prepareIntegerValue(inputStartPort, MIN_PORT, inputStartPort);
        final int endPort = prepareIntegerValue(inputEndPort, startPort, MAX_PORT);
        final List<String> hostList = prepareScanAddressList(scanAddress);

        final List<IPortScanResult> result = new ArrayList<IPortScanResult>();
        if (hostList == null || hostList.isEmpty()) {
            LOG.fine("Could not resolve address: " + scanAddress);
            LOG.info("No ports to scan on [" + scanAddress + "].");
            return result;
        }

        // prepare scan threads
        final List<IPortScanListener> portScanListenerList = preparePortScanListenerList(portScannerListeners);
        final ExecutorService es = Executors.newFixedThreadPool(numberOfThreads);
        final List<Future<IPortScanResult>> futures = new ArrayList<Future<IPortScanResult>>();
        for (String hostAddress : hostList) {
            LOG.info("Scan ports on [" + hostAddress + "] from range " + startPort + " - " + endPort + " (threads: " + numberOfThreads + ", timeout:" + timeout + ")...");
            for (int port = startPort; port <= endPort; port++) {
                futures.add(prepareNetworkAddressScanThread(es, hostAddress, port, timeout, portScanListenerList));
            }
        }

        es.shutdown();

        LOG.fine("Wait until ended...");
        for (final Future<IPortScanResult> f : futures) {
            prepareResultSet(filterIsAvailable, result, f);
        }

        return result;
    }


    /**
     * Prepare the network address scan thread
     *
     * @param es the executer service
     * @param scanAddress the scan address
     * @param port the port
     * @param t the timeout
     * @param portScannerListenerList the port scanner listener list
     * @return the result
     */
    protected Future<IPortScanResult> prepareNetworkAddressScanThread(final ExecutorService es,
                                                                      final String scanAddress,
                                                                      final int port,
                                                                      final int t,
                                                                      final List<IPortScanListener> portScannerListenerList) {
        return es.submit(new Callable<IPortScanResult>() {
            @Override
            public IPortScanResult call() {
                IPortAnalyzer networkAddressScanner = new TCPConnectionPortAnalyzerImpl(t);
                IPortScanResult portScanResult = networkAddressScanner.analyzePort(scanAddress, port);

                if (portScannerListenerList != null) {
                    for (IPortScanListener listener : portScannerListenerList) {
                        listener.visitedPort(portScanResult);
                    }
                }

                return portScanResult;
            }
        });
    }


    /**
     * Prepare the result set
     *
     * @param filterIsAvailable true if the filter is available
     * @param result the result set
     * @param futurePortScanResult the input port scan result
     */
    protected void prepareResultSet(Boolean filterIsAvailable, final List<IPortScanResult> result,
            final Future<IPortScanResult> futurePortScanResult) {
        if (futurePortScanResult == null) {
            return;
        }

        try {
            final IPortScanResult r = futurePortScanResult.get();
            if (r != null) {
                if (filterIsAvailable == null) {
                    result.add(r);
                } else if (filterIsAvailable.booleanValue() && r.isAvailable()) {
                    result.add(r);
                } else if (!filterIsAvailable.booleanValue() && !r.isAvailable()) {
                    result.add(r);
                }
            }
        } catch (Exception e) {
            // LOG.debug("Could not Error occurred: " + e.getMessage(), e);
        }
    }


    /**
     * Prepare input int value
     *
     * @param inputValue the input value
     * @param minValue the min value
     * @param maxValue the max value
     * @return the value
     */
    protected int prepareIntegerValue(int inputValue, int minValue, int maxValue) {
        int result = minValue;
        if (inputValue > 0 && inputValue > minValue) {
            result = inputValue;
        }

        if (result > maxValue) {
            result = maxValue;
        }

        return result;
    }

    /**
     * Prepare the scan addresses
     *
     * @param scanAddress the scan addresse(s)
     * @return the scan host address list
     */
    protected List<String> prepareScanAddressList(String scanAddress) {
        String inputHostAddresss = "127.0.0.1";
        if (scanAddress == null || scanAddress.trim().isEmpty()) {
            return Arrays.asList(inputHostAddresss);
        } else if (CIDRUtil.getInstance().isValidRange(scanAddress.trim())) {
            try {
                return CIDRUtil.getInstance().getAllAddresses(scanAddress.trim());
            } catch (UnknownHostException e) {
                // NOP
            }
        }

        String[] scanAddressSplit = scanAddress.trim().split(" ");
        if (scanAddress.indexOf(',') > 0) {
            scanAddressSplit = scanAddress.split(",");
        } else if (scanAddress.indexOf(' ') > 0) {
            scanAddressSplit = scanAddress.split(" ");
        }

        List<String> addressList = new ArrayList<String>();
        for (int i = 0; i < scanAddressSplit.length; i++) {
            String addr = scanAddressSplit[i].trim();
            if (IPUtil.getInstance().isValidAddress(addr)) {
                addressList.add(addr);
            }
        }

        return addressList;
    }

    /**
     * Prepare the port scan listener list
     *
     * @param portScannerListeners the port scan listener list
     * @return the port scan listener list
     */
    protected List<IPortScanListener> preparePortScanListenerList(IPortScanListener... portScannerListeners) {
        List<IPortScanListener> portScanListenerList = null;
        if (portScannerListeners != null) {
            portScanListenerList = Arrays.asList(portScannerListeners);
        }
        return portScanListenerList;
    }
}
