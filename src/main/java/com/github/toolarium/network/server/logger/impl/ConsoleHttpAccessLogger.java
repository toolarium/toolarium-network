/*
 * HttpAccessLoggerImpl.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.server.logger.impl;

import com.github.toolarium.network.server.dto.IHttpRequest;
import com.github.toolarium.network.server.dto.IHttpResponse;
import com.github.toolarium.network.server.dto.IHttpServerInformation;
import com.github.toolarium.network.server.logger.IHttpAccessLogger;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Implements the http access logger
 * 
 * @author patrick
 */
public class ConsoleHttpAccessLogger implements IHttpAccessLogger {
    private static final String SPACE = " ";
    private static final String DASH = "-";
    private PrintStream out = System.out;
    private DateFormat logFormat;


    /**
     * Constructor for ConsoleHttpAccessLogger
     */
    public ConsoleHttpAccessLogger() {
        logFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z");
    }

    
    /**
     * @see com.github.toolarium.network.server.logger.IHttpAccessLogger#start()
     */
    public void start() {
    }


    /**
     * @see com.github.toolarium.network.server.logger.IHttpAccessLogger#stop()
     */
    public void stop() {
    }

    
    /**
     * @see com.github.toolarium.network.server.logger.IHttpAccessLogger#requestReceived(com.github.toolarium.network.server.dto.IHttpServerInformation, com.github.toolarium.network.server.dto.IHttpRequest)
     */
    @Override
    public void requestReceived(IHttpServerInformation serverInformation, IHttpRequest request) {
    }

    
    /**
     * @see com.github.toolarium.network.server.logger.IHttpAccessLogger#responseSent(com.github.toolarium.network.server.dto.IHttpServerInformation, com.github.toolarium.network.server.dto.IHttpRequest, 
     *      com.github.toolarium.network.server.dto.IHttpResponse)
     */
    @Override
    public void responseSent(IHttpServerInformation serverInformation, IHttpRequest request, IHttpResponse response) {
        StringBuilder b = new StringBuilder();
        b.append(serverInformation.getLocalIpAddress()); // serverInformation.getHostname();
        b.append(SPACE);
        b.append(DASH).append(SPACE).append(DASH);
        b.append(SPACE);
        b.append("[").append(logFormat.format(new Date())).append("]"); // serverInformation.getHostname();
        b.append(SPACE);
        b.append("\"").append(handleNullValue(request.getMethod())).append(SPACE).append(handleNullValue(request.getPath())).append(SPACE).append(handleNullValue(request.getVersion())).append("\"");
        b.append(SPACE);
        b.append(response.getStatus());
        b.append(SPACE);

        int length = 0;
        if (response.getBody() != null) {
            length = response.getBody().length();
        }
        b.append(length);
        b.append(SPACE);

        long responseTime = System.currentTimeMillis() - request.getRequestTimestamp().getTime();
        b.append(responseTime * 1000);
        out.println(b.toString());
        
        //Map<String, String> getHeaders();
        //Map<String, String> getParameters();
        //String getBaseLocation();
        //String getBody();
    }


    /**
     * Handle null values
     * 
     * @param value the value
     * @return the prepared value
     */
    private String handleNullValue(String value) {
        if (value == null || value.isBlank()) {
            return "-";
        }
        return value;
    }
}

