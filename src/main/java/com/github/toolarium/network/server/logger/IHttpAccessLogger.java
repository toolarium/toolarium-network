/*
 * IHttpAccessLog.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.server.logger;

import com.github.toolarium.network.server.dto.IHttpRequest;
import com.github.toolarium.network.server.dto.IHttpResponse;
import com.github.toolarium.network.server.dto.IHttpServerInformation;


/**
 * Defines the http access log
 * 
 * @author patrick
 */
public interface IHttpAccessLogger {
    
    /**
     * Notification to start the http access logger 
     */
    void start();


    /**
     * Notification to stop the http access logger
     */
    void stop();


    /**
     * Request received notification
     *
     * @param serverInformation the server information
     * @param request the request
     */
    void requestReceived(IHttpServerInformation serverInformation, IHttpRequest request);


    /**
     * Response sent notification
     *
     * @param serverInformation the server information
     * @param request the request
     * @param response the response
     */
    void responseSent(IHttpServerInformation serverInformation, IHttpRequest request, IHttpResponse response);
    
}
