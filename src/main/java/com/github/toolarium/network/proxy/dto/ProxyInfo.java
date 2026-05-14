/*
 * ProxyInfo.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.proxy.dto;

import java.io.Serializable;
import java.util.Objects;


/**
 * Implements the {@link IProxyInfo}.
 *
 * @author patrick
 */
public class ProxyInfo implements IProxyInfo, Serializable {
    private static final long serialVersionUID = 1L;
    private String type;
    private String host;
    private int port;


    /**
     * Constructor for ProxyInfo
     *
     * @param type the proxy type
     * @param host the host
     * @param port the port
     */
    public ProxyInfo(String type, String host, int port) {
        this.type = type;
        this.host = host;
        this.port = port;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public boolean isDirect() {
        return "DIRECT".equals(type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, host, port);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ProxyInfo other = (ProxyInfo) obj;
        return Objects.equals(type, other.type) && Objects.equals(host, other.host) && port == other.port;
    }

    @Override
    public String toString() {
        if (isDirect()) {
            return "ProxyInfo [DIRECT]";
        }
        return "ProxyInfo [" + type + " " + host + ":" + port + "]";
    }
}
