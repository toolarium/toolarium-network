/*
 * HttpServerInformationImpl.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.server.dto;

import java.io.Serializable;
import java.net.URI;
import java.util.Objects;
import javax.net.ssl.SSLContext;


/**
 * Implements the {@link IHttpServerInformation}.
 * 
 * @author patrick
 */
public class HttpServerInformation implements IHttpServerInformation, Serializable {
    private static final long serialVersionUID = -9210837272048199788L;
    private SSLContext sslContext;
    private int port;
    private String localIpAddress;
    private String hostname;


    /**
     * @see com.github.toolarium.network.server.dto.IHttpServerInformation#getPort()
     */
    @Override
    public int getPort() {
        return port;
    }

        
    /**
     * Set the local port
     *
     * @param port the local port
     * @return the http server information
     */
    public HttpServerInformation setPort(int port) {
        this.port = port;
        return this;
    }

    
    /**
     * @see com.github.toolarium.network.server.dto.IHttpServerInformation#getSSLContext()
     */
    @Override
    public SSLContext getSSLContext() {
        return sslContext;
    }

    
    /**
     * Set the SSL context
     *
     * @param sslContext the SSL context
     * @return the http server information
     */
    public HttpServerInformation setSSLContext(SSLContext sslContext) {
        this.sslContext = sslContext;
        return this;
    }


    /**
     * @see com.github.toolarium.network.server.dto.IHttpServerInformation#getProtocol()
     */
    @Override
    public String getProtocol() {
        String protocol = "http";
        if (sslContext != null) {
            protocol = "https";
        }
        
        return protocol;
    }

    
    /**
     * @see com.github.toolarium.network.server.dto.IHttpServerInformation#getLocalIpAddress()
     */
    @Override
    public String getLocalIpAddress() {
        return localIpAddress;
    }


    /**
     * Set the local ip address
     *
     * @param localIpAddress the local ip address
     * @return the http server information
     */
    public HttpServerInformation setLocalIpAddress(String localIpAddress) {
        this.localIpAddress = localIpAddress;
        return this;
    }
    
    
    /**
     * @see com.github.toolarium.network.server.dto.IHttpServerInformation#getHostname()
     */
    @Override
    public String getHostname() {
        return hostname;
    }


    /**
     * Set the hostname
     *
     * @param hostname the hostname
     * @return the http server information
     */
    public HttpServerInformation setHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }


    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(hostname, localIpAddress);
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
        
        HttpServerInformation other = (HttpServerInformation) obj;
        return Objects.equals(hostname, other.hostname) && Objects.equals(localIpAddress, other.localIpAddress);
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "HttpServerInformation [localIpAddress=" + localIpAddress + ", hostname=" + hostname + "]";
    }


    /**
     * @see com.github.toolarium.network.server.dto.IHttpServerInformation#getURI()
     */
    @Override
    public URI getURI() {
        return URI.create(getProtocol() + "://" + getHostname() + ":" + getPort());
    }
}
