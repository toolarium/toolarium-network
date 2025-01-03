/*
 * HttpConnectionHandlerImpl.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.server.handler.impl;

import com.github.toolarium.common.formatter.TimeDifferenceFormatter;
import com.github.toolarium.common.util.StringUtil;
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
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implements a server http connection handler
 * 
 * @author patrick
 */
public class HttpConnectionHandlerImpl extends AbstractConnectionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(HttpConnectionHandlerImpl.class);
    private static AtomicLong counter = new AtomicLong();
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
        long id = counter.incrementAndGet();
        if (clientSocket == null || httpService == null) {
            return;
        }

        BufferedReader reader = null;
        BufferedWriter writer = null;

        final long startTimestamp = System.currentTimeMillis();
        String logId = "";
        String logHeader = "";
        if (LOG.isDebugEnabled()) {
            logId = Long.toString(id);
            logHeader = " > #" + logId + " http ";
        }
        
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Handling server request #" + logId + ":");
            }
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            IHttpRequest request = readHttpRequest(logHeader, reader);
            if (httpAccessLogger != null) {
                httpAccessLogger.requestReceived(httpServerInformation, request);
            }
            
            IHttpResponse response = httpService.processRequest(httpServerLogger, request); 
            if (response != null) {
                writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                writeHttpResponse(logHeader, writer, response);
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
        
        if (LOG.isDebugEnabled()) {
            String duration = new TimeDifferenceFormatter(false, false).formatAsString(System.currentTimeMillis() - startTimestamp);
            LOG.debug(StringUtil.getInstance().width(logHeader + "process duration", 34, ' ', false) + logOutputWrapper(duration)); 
        }
    }

    
    
    /**
     * Read the http request
     *
     * @param logHeader the log header
     * @param reader the reader
     * @return the http request
     * @throws IOException In case of an I/O error
     */
    protected IHttpRequest readHttpRequest(String logHeader, BufferedReader reader) throws IOException {
        HttpRequest request = new HttpRequest();
        String firstLine = readInFirstLine(reader, request);
        
        if (LOG.isDebugEnabled()) {
            LOG.debug(StringUtil.getInstance().width(logHeader + "request", 34, ' ', false) + logOutputWrapper(firstLine)); 
        }

        if (reader.ready()) {
            request.setHeaders(HttpHeaderUtil.getInstance().readHeaders(reader));
            if (LOG.isDebugEnabled()) {
                LOG.debug(StringUtil.getInstance().width(logHeader + "request headers", 34, ' ', false) + logOutputWrapper("" + request.getHeaders())); 
            }
        
            if (request.containsHeader(HttpHeaderUtil.CONTENT_LENGTH)) {
                int length = Integer.parseInt(request.getHeader(HttpHeaderUtil.CONTENT_LENGTH));
                if (LOG.isDebugEnabled()) {
                    LOG.debug(StringUtil.getInstance().width(logHeader + "read length", 34, ' ', false) + logOutputWrapper("" + length)); 
                }
                String body = readInBody(reader, length);
                request.setBody(body);
                if (LOG.isDebugEnabled()) {
                    LOG.debug(StringUtil.getInstance().width(logHeader + "request body", 34, ' ', false) + logOutputWrapper(body)); 
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(StringUtil.getInstance().width(logHeader + "no length", 34, ' ', false)); 
                }
                
                if (reader.ready()) {
                    StringBuilder content = new StringBuilder();
                    String m;
                    while ((m = reader.readLine()) != null) {
                        content.append(m);
                    }
                    
                    request.setBody(content.toString());
                }
            }
        }
        
        return request;
    }


    /**
     * Write the response
     *
     * @param logHeader the log header
     * @param writer the writer
     * @param response the response
     * @throws IOException In case of an I/O error
     */
    protected void writeHttpResponse(String logHeader, BufferedWriter writer, IHttpResponse response) throws IOException {
        final String statusLine = getStatusLine(response);
        if (statusLine != null && statusLine.length() > 0) {
            writer.write(statusLine);                    
            if (LOG.isDebugEnabled()) {
                LOG.debug(StringUtil.getInstance().width(logHeader + "response", 34, ' ', false) + logOutputWrapper(statusLine.replace(System.lineSeparator(), ""))); 
            }
        }

        final String headers = getHeaders(response);
        if (headers != null && headers.length() > 0) {
            writer.write(headers);                    
            
            if (LOG.isDebugEnabled()) {
                LOG.debug(StringUtil.getInstance().width(logHeader + "response headers", 34, ' ', false) + logOutputWrapper(headers)); 
            }
        }

        writer.write(System.lineSeparator());                    

        final String body = getBodyAsByteArray(response);
        if (body != null && body.length() > 0) {
            writer.write(body);
            if (LOG.isDebugEnabled()) {
                LOG.debug(StringUtil.getInstance().width(logHeader + "response body", 34, ' ', false) + logOutputWrapper(body)); 
            }
        }
    }

    
    /**
     * Log output wrapper
     *
     * @param input the input
     * @return the wrapped output
     */
    protected String logOutputWrapper(String input) {
        return new StringBuilder().append('[').append(input).append(']').toString();
    }
}
