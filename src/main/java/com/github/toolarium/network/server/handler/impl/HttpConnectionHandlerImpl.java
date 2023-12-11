/*
 * HttpConnectionHandlerImpl.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.server.handler.impl;

import com.github.toolarium.network.server.dto.HttpRequest;
import com.github.toolarium.network.server.dto.IHttpRequest;
import com.github.toolarium.network.server.dto.IHttpResponse;
import com.github.toolarium.network.server.dto.IHttpServerInformation;
import com.github.toolarium.network.server.logger.IHttpAccessLogger;
import com.github.toolarium.network.server.logger.IHttpServerLogger;
import com.github.toolarium.network.server.service.IHttpService;
import com.github.toolarium.network.server.util.HttpHeaderUtil;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;


/**
 * Implements a server http connection handler
 * 
 * @author patrick
 */
public class HttpConnectionHandlerImpl extends AbstractConnectionHandler {
    private Socket clientSocket;
    private IHttpService httpService;
    private IHttpServerInformation httpServerInformation;
    private IHttpServerLogger httpServerLogger;
    private IHttpAccessLogger httpAccessLogger;


    /**
     * @see com.github.toolarium.network.server.handler.IHttpConnectionHandler#init(java.net.Socket, com.github.toolarium.network.server.service.IHttpService, 
     *      com.github.toolarium.network.server.dto.IHttpServerInformation, com.github.toolarium.network.server.logger.IHttpServerLogger, com.github.toolarium.network.server.logger.IHttpAccessLogger)
     */
    @Override
    public void init(Socket socket, IHttpService httpService, IHttpServerInformation httpServerInformation, IHttpServerLogger httpServerLogger, IHttpAccessLogger httpAccessLogger) {
        this.clientSocket = socket;
        this.httpService = httpService;
        this.httpServerInformation = httpServerInformation;
        this.httpServerLogger = httpServerLogger;
        this.httpAccessLogger = httpAccessLogger;
    }

    
    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {
        if (clientSocket == null || httpService == null) {
            return;
        }

        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            IHttpRequest request = buildHttpRequest(reader);
            if (httpAccessLogger != null) {
                httpAccessLogger.requestReceived(httpServerInformation, request);
            }
            
            IHttpResponse response = httpService.processRequest(httpServerLogger, request); 
            if (response != null) {
                
                writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                final String statusLineAndHeaders = getStatusLineAndHeaders(response);
                if (statusLineAndHeaders != null && statusLineAndHeaders.length() > 0) {
                    writer.write(statusLineAndHeaders);                    
                }
                
                final String body = getBodyAsByteArray(response);
                if (body != null && body.length() > 0) {
                    writer.write(body);
                }
            }
            
            if (httpAccessLogger != null) {
                httpAccessLogger.responseSent(httpServerInformation, request, response);
            }
        } catch (IOException e) {
            Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);

        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    // NOP
                }
            }
        }
    }

    
    /**
     * Build the http request
     *
     * @param reader the reader
     * @return the http request
     * @throws IOException In case of an I/O error
     */
    protected IHttpRequest buildHttpRequest(BufferedReader reader) throws IOException {
        HttpRequest request = new HttpRequest();
        readInFirstLine(reader, request);
        
        if (reader.ready()) {
            request.setHeaders(HttpHeaderUtil.getInstance().readHeaders(reader));
        }
        
        if (reader.ready()) {
            if (request.containsHeader(HttpHeaderUtil.CONTENT_LENGTH)) {
                request.setBody(readInBody(reader, Integer.parseInt(request.getHeader(HttpHeaderUtil.CONTENT_LENGTH))));
            } else {
                StringBuilder content = new StringBuilder();
                String m;
                while ((m = reader.readLine()) != null) {
                    content.append(m);
                }
                
                request.setBody(content.toString());
            }
        }
        
        return request;
    }
}
