/*
 * IPortScanResult.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.scanner.dto;

/**
 * Defines the port scan result
 *
 * @author patrick
 */
public interface IPortScanResult {
    
    /**
     * Get the host address
     *
     * @return the host address
     */
    String getHostAddress();


    /**
     * Get the port
     *
     * @return the port
     */
    int getPort();


    /**
     * Define if the port is available
     *
     * @return true if the port is available
     */
    boolean isAvailable();


    /**
     * Define if the port is active
     *
     * @return true if the port is active
     */
    boolean isActive();


    /**
     * The guessed protocol
     *
     * @return the protocol which is used for this port or null in case of unknown
     */
    String getProtocol();


    /**
     * The guessed application behind the port
     *
     * @return the application which is behind the port or null in case of unknown
     */
    String getApplication();
}
