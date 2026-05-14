/*
 * WakeOnLanResult.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.wol.dto;

import java.io.Serializable;
import java.util.Objects;


/**
 * Implements the {@link IWakeOnLanResult}.
 *
 * @author patrick
 */
public class WakeOnLanResult implements IWakeOnLanResult, Serializable {
    private static final long serialVersionUID = 1L;
    private String macAddress;
    private String broadcastAddress;
    private int port;
    private boolean success;
    private Exception exception;


    /**
     * Constructor for WakeOnLanResult
     *
     * @param macAddress the MAC address
     * @param broadcastAddress the broadcast address
     * @param port the port
     * @param success true if sent
     * @param exception the exception or null
     */
    public WakeOnLanResult(String macAddress, String broadcastAddress, int port, boolean success, Exception exception) {
        this.macAddress = macAddress;
        this.broadcastAddress = broadcastAddress;
        this.port = port;
        this.success = success;
        this.exception = exception;
    }

    @Override
    public String getMacAddress() {
        return macAddress;
    }

    @Override
    public String getBroadcastAddress() {
        return broadcastAddress;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public Exception getException() {
        return exception;
    }

    @Override
    public int hashCode() {
        return Objects.hash(macAddress, broadcastAddress, port, success);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        WakeOnLanResult other = (WakeOnLanResult) obj;
        return Objects.equals(macAddress, other.macAddress) && Objects.equals(broadcastAddress, other.broadcastAddress)
                && port == other.port && success == other.success;
    }

    @Override
    public String toString() {
        if (success) {
            return "WakeOnLanResult [" + macAddress + " via " + broadcastAddress + ":" + port + " sent]";
        }
        StringBuilder sb = new StringBuilder("WakeOnLanResult [");
        sb.append(macAddress).append(" FAILED");
        if (exception != null) {
            sb.append(", error=").append(exception.getMessage());
        }
        sb.append("]");
        return sb.toString();
    }
}
