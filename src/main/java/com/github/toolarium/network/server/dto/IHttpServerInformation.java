/*
 * IHttpServerInformation.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.server.dto;

import java.net.URI;
import javax.net.ssl.SSLContext;


/**
 * Defines the http server information
 * 
 * @author patrick
 */
public interface IHttpServerInformation {

    /**
     * Get the port
     *
     * @return the port
     */
    int getPort();
    
    
    /**
     * Get the ssl context
     *
     * @return the ssl context or null
     */
    SSLContext getSSLContext();
    
    
    /**
     * Get the protocol
     *
     * @return the protocol
     */
    String getProtocol();

    
    /**
     * Get the local ip address
     *
     * @return the local ip address
     */
    String getLocalIpAddress();

    
    /**
     * Get the hostname
     *
     * @return the hostname
     */
    String getHostname();
    
    
    /**
     * Get the server URI
     *
     * @return the server uri
     */
    URI getURI();
    
}
