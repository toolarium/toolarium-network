/*
 * PortScannerClient.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.scanner;

import java.util.List;
import java.util.Map;


/**
 * Implements a port scanner client which can be used on command line.
 *
 * @author patrick
 */
public class PortScannerClient {
    private static final int DEFAULT_TIMEOUT = 50;
    private static final int DEFAULT_NUMBER_OF_THREADS = 300;
    private String scanAddress;
    private int startPort;
    private int endPort;
    private int numberOfThreads;
    private int timeout;
    //private VerboseLevel verboseLevel;

    
    /**
     * Constructor
     */
    public PortScannerClient() {
        scanAddress = "127.0.0.1";
        startPort = 1;
        endPort = IPortScanner.MAX_PORT;
        numberOfThreads = DEFAULT_NUMBER_OF_THREADS;
        timeout = DEFAULT_TIMEOUT;
    }

    
    /**
     * The main class
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        PortScannerClient portScanner = new PortScannerClient();

        for (int i = 0; i < args.length; i++) {
            if (getArgumentValue(args[i], "-v", "--version") != null) {
                logToConsole("");
                return;
            }
            if (getArgumentValue(args[i], "-h", "--help") != null) {
                logToConsole("Usage: portscanner [-hv] [-a=address] [-se=eport] [-sp=sport]");
                logToConsole("                   [-t=numberOfThreads] [-to=timeout] [--verbose=verboseLevel]");
                logToConsole("Small port scanner.");
                logToConsole("  -a, --addresss=address                 The address, by default 127.0.0.1.");
                logToConsole("  -h, --help                             Display this help message");
                logToConsole("  -se, --endPort=eport                   The end port, by default 65535.");
                logToConsole("  -sp, --startPort=sport                 The start port, by default 1.");
                logToConsole("  -t, --numberOfThreads=numberOfThreads  The number of threads, by default 300.");
                logToConsole("  -to, --timeout=timeout                 The timeout, by default 50.");
                logToConsole("  -v, --version                          Display version info");
                //logToConsole("  --verbose=verboseLevel                 Specify the verbose level: (NONE, INFO, ACCESS, ACCESS_CONSOLE, VERBOSE), by default INFO.");
                return;
            }
            
            if (getArgumentValue(args[i], "-a", "--address") != null) {
                portScanner.setScanAddress(getArgumentValue(args[i], "-a", "--address"));
            }
            if (getArgumentValue(args[i], "-sp", "--startPort") != null) {
                portScanner.setStartPort(getArgumentValue(args[i], "-sp", "--startPort"));
            }
            if (getArgumentValue(args[i], "-ep", "--endPort") != null) {
                portScanner.setEndPort(getArgumentValue(args[i], "-ep", "--endPort"));
            }
            if (getArgumentValue(args[i], "-t", "--numberOfThreads") != null) {
                portScanner.setNumberOfThreads(getArgumentValue(args[i], "-t", "--numberOfThreads"));
            }
            if (getArgumentValue(args[i], "-to", "--timeout") != null) {
                portScanner.setTimeout(getArgumentValue(args[i], "-t", "--timeout"));
            }
            if (getArgumentValue(args[i], null, "--verbose") != null) {
                portScanner.setVerboseLevel(getArgumentValue(args[i], null, "--verbose"));
            }
        }
        
        portScanner.run();
    }
    
    
    /**
     * Run
     */
    public void run() {
        try {
            logToConsole("Analyze open ports on " + scanAddress + "...");
            Map<String, List<Integer>> openPortMap = PortScannerFactory.getInstance().scanOpenPorts(scanAddress, startPort, endPort, numberOfThreads, timeout);
            if (openPortMap != null) {
                for (String addr : openPortMap.keySet()) {
                    logToConsole("Open ports on " + addr + ": " + openPortMap.get(addr));
                }
            } else {
                logToConsole("No open ports found on [" + scanAddress + "].");
            }
        } catch (Throwable e) {
            logToConsole("Could not execute, error: " + e.getMessage());
        }
    }

    
    

    /**
     * Get argument value
     *
     * @param argument the argument
     * @param shortArgument the short argument name
     * @param longArgument the long argument name
     * @return the value
     */
    private static String getArgumentValue(String argument, String shortArgument, String longArgument) {
        if (argument != null && shortArgument != null && argument.startsWith(shortArgument)) {
            if (argument.indexOf('=') > 0) {
                return argument.split("=")[1];
            } else {
                return "";
            }
        }

        if (argument != null && longArgument != null && argument.startsWith(longArgument)) {
            if (argument.indexOf('=') > 0) {
                return argument.split("=")[1];
            } else {
                return "";
            }
        }

        return null;
    }

    
    /**
     * Log to console
     *
     * @param msg the message
     */
    private static void logToConsole(String msg) {
        System.out.println(msg); // CHECKSTYLE IGNORE THIS LINE
    }

    
    /**
     * Set the scan address
     * 
     * @param scanAddress the scan address
     */
    private void setScanAddress(String scanAddress) {
        this.scanAddress = scanAddress.trim();
    }

    
    /**
     * Set the start port
     * 
     * @param startPort the start port
     */
    private void setStartPort(String startPort) {
        this.startPort = parseNumber(startPort.trim(), this.startPort);
    }


    /**
     * Set the end port
     * 
     * @param endPort the end port
     */
    private void setEndPort(String endPort) {
        this.endPort = parseNumber(endPort, this.endPort);
    }

    
    /**
     * Set the number of threads 
     * 
     * @param numberOfThreads the number of threads
     */
    private void setNumberOfThreads(String numberOfThreads) {
        this.numberOfThreads = parseNumber(numberOfThreads, this.numberOfThreads);
    }


    /**
     * Set the timeout
     * 
     * @param timeout the timeout
     */
    private void setTimeout(String timeout) {
        this.timeout = parseNumber(timeout, this.timeout);
    }

    
    /**
     * Set the verbose level
     * 
     * @param verboseLevel the verbose level
     */
    private void setVerboseLevel(String verboseLevel) {
        //this.verboseLevel = VerboseLevel.valueOf(verboseLevel.trim());
    }


    /**
     * Parse a number
     *
     * @param num the number
     * @param defaultValue the default value
     * @return the converted value
     */
    private int parseNumber(String num, int defaultValue) {
        try {
            return Integer.parseInt(num);
        } catch (NumberFormatException e) {
            logToConsole("Invalid argument value [" + num + "] can't converted into an integer: " + e.getMessage() + "!");
        }
        
        return defaultValue;
    }
}
