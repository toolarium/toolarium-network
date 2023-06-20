/*
 * PortScanResultImpl.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.scanner.dto;

import java.io.Serializable;
import java.util.Objects;


/**
 * Implements the {@link IPortScanResult}.
 *
 * @author patrick
 */
public class PortScanResult implements IPortScanResult, Serializable {
    private static final long serialVersionUID = -2703078177414002124L;
    private final String hostAddress;
    private final int port;
    private final boolean isAvailable;
    private boolean isActive;
    private String protocol;
    private String application;


    /**
     * Constructor
     *
     * @param hostAddress the host address
     * @param port the port
     * @param isAvailable is it available
     */
    public PortScanResult(final String hostAddress, final int port, final boolean isAvailable) {
        this.hostAddress = hostAddress;
        this.port = port;
        this.isAvailable = isAvailable;
        this.isActive = isAvailable;
        this.protocol = null;
        this.application = null;
    }


    /**
     * Constructor
     *
     * @param portScanResult the port scan result
     */
    public PortScanResult(final IPortScanResult portScanResult) {
        this.hostAddress = portScanResult.getHostAddress();
        this.port = portScanResult.getPort();
        this.isAvailable = portScanResult.isAvailable();
        this.isActive = portScanResult.isActive();
        this.protocol = portScanResult.getProtocol();
        this.application = portScanResult.getApplication();
    }


    /**
     * @see com.github.toolarium.network.scanner.dto.IPortScanResult#getHostAddress()
     */
    @Override
    public String getHostAddress() {
        return hostAddress;
    }


    /**
     * @see com.github.toolarium.network.scanner.dto.IPortScanResult#getPort()
     */
    @Override
    public int getPort() {
        return port;
    }


    /**
     * @see com.github.toolarium.network.scanner.dto.IPortScanResult#isAvailable()
     */
    @Override
    public boolean isAvailable() {
        return isAvailable;
    }


    /**
     * @see com.github.toolarium.network.scanner.dto.IPortScanResult#isActive()
     */
    @Override
    public boolean isActive() {
        return isActive;
    }


    /**
     * Set is it active or not
     *
     * @param isActive true if it is active
     */
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }


    /**
     * @see com.github.toolarium.network.scanner.dto.IPortScanResult#getProtocol()
     */
    @Override
    public String getProtocol() {
        return protocol;
    }


    /**
     * Set the protocol
     *
     * @param protocol the protocol
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }


    /**
     * @see com.github.toolarium.network.scanner.dto.IPortScanResult#getApplication()
     */
    @Override
    public String getApplication() {
        return application;
    }


    /**
     * Sets the application
     *
     * @param application the application
     */
    public void setApplication(String application) {
        this.application = application;
    }


    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(application, hostAddress, isActive, isAvailable, port, protocol);
    }


    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
            
        PortScanResult other = (PortScanResult) obj;
        return Objects.equals(application, other.application) && Objects.equals(hostAddress, other.hostAddress)
                && isActive == other.isActive && isAvailable == other.isAvailable && port == other.port
                && Objects.equals(protocol, other.protocol);
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "PortScanResultImpl [hostAddress=" + hostAddress + ", port=" + port + ", isAvailable=" + isAvailable
                + ", isActive=" + isActive + ", protocol=" + protocol + ", application=" + application + "]";
    }
}
