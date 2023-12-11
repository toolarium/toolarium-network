/*
 * HttpService.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.server.service;

import com.github.toolarium.network.server.dto.IHttpRequest;
import com.github.toolarium.network.server.dto.IHttpResponse;
import com.github.toolarium.network.server.handler.IHttpConnectionHandler;
import com.github.toolarium.network.server.logger.IHttpServerLogger;


/**
 * Defines the http service
 *  
 * @author patrick
 */
public interface IHttpService {

    /**
     * Start the service
     * 
     * @param httpServerLogger the server logger
     * @param port the port
     * @return true to continue startup; otherwise stop startup
     */
    boolean start(IHttpServerLogger httpServerLogger,int port);

    
    /**
     * Stops the service
     * 
     * @param httpServerLogger the server logger
     * @param port the port
     */
    void stop(IHttpServerLogger httpServerLogger,int port);


    /**
     * Process a request
     * 
     * @param httpServerLogger the server logger
     * @param request the request
     * @return the response
     */
    IHttpResponse processRequest(IHttpServerLogger httpServerLogger, IHttpRequest request);


    /**
     * Get the http connection handler
     * 
     * @return the http connection handler
     */
    IHttpConnectionHandler getHttpConnectionHandler();

}
