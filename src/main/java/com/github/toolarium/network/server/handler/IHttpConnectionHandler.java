/*
 * IHttpConnectionHandler.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.server.handler;

import com.github.toolarium.network.server.dto.IHttpServerInformation;
import com.github.toolarium.network.server.logger.IHttpAccessLogger;
import com.github.toolarium.network.server.logger.IHttpServerLogger;
import com.github.toolarium.network.server.service.IHttpService;
import java.net.Socket;


/**
 * Defines the http connection handler
 * 
 * @author patrick
 */
public interface IHttpConnectionHandler extends Runnable {
    
    /**
     * Initialise the connection handler
     *
     * @param socket the socket
     * @param httpService the http service
     * @param httpServerInformation the http server information
     * @param httpServerLogger the http server logger
     * @param httpAccessLogger the http access logger
     */
    void init(Socket socket, 
              IHttpService httpService, 
              IHttpServerInformation httpServerInformation,
              IHttpServerLogger httpServerLogger,
              IHttpAccessLogger httpAccessLogger);
    
}
