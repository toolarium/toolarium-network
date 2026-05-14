/*
 * IProxyInfo.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.proxy.dto;


/**
 * Defines information about a detected proxy.
 *
 * @author patrick
 */
public interface IProxyInfo {

    /**
     * Get the proxy type (DIRECT, HTTP, SOCKS).
     *
     * @return the proxy type
     */
    String getType();


    /**
     * Get the proxy host.
     *
     * @return the host or null if DIRECT
     */
    String getHost();


    /**
     * Get the proxy port.
     *
     * @return the port or -1 if DIRECT
     */
    int getPort();


    /**
     * Check if this is a direct connection (no proxy).
     *
     * @return true if no proxy
     */
    boolean isDirect();
}
