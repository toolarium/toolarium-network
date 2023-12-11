/*
 * IHttpServer.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.server;

import com.github.toolarium.network.server.dto.IHttpServerInformation;
import com.github.toolarium.network.server.logger.IHttpAccessLogger;
import com.github.toolarium.network.server.logger.IHttpServerLogger;
import com.github.toolarium.network.server.service.IHttpService;
import java.io.IOException;
import javax.net.ssl.SSLContext;


/**
 * Defines the http server
 *  
 * @author patrick
 */
public interface IHttpServer {
    
    /**
     * Initialise http server
     *
     * @param httpServerLogger the server logger
     * @param httpAccessLogger the access logger
     */
    void init(IHttpServerLogger httpServerLogger, IHttpAccessLogger httpAccessLogger);

    
    /**
     * Start the server 
     *
     * @param port the port
     * @param inputHttpService the http service
     * @throws IOException In case of an I/O error
     */
    void start(IHttpService inputHttpService, int port) throws IOException;

    
    /**
     * Start the server 
     *
     * @param port the port
     * @param sslContext the ssl context
     * @param inputHttpService the http service
     * @throws IOException In case of an I/O error
     */
    void start(IHttpService inputHttpService, int port, SSLContext sslContext) throws IOException;


    /**
     * Stop the server
     * 
     * @throws IOException In case of an I/O error
     */
    void stop() throws IOException;
    

    /**
     * Get the http server information
     *
     * @return the http server information
     */
    IHttpServerInformation getHttpServerInformation();
    
}
