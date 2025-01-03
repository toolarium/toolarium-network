/*
 * AbstractConnectionHandler.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.server.handler.impl;

import com.github.toolarium.network.server.dto.HttpReponse;
import com.github.toolarium.network.server.dto.HttpRequest;
import com.github.toolarium.network.server.dto.IHttpRequest;
import com.github.toolarium.network.server.dto.IHttpResponse;
import com.github.toolarium.network.server.handler.IHttpConnectionHandler;
import com.github.toolarium.network.server.handler.impl.parser.HttpRequestParser;
import com.github.toolarium.network.server.util.HttpStatusUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;


/**
 * Implements the abstract base class for {@link IHttpConnectionHandler}.
 * 
 * @author patrick
 */
public abstract class AbstractConnectionHandler implements IHttpConnectionHandler {
    private static final String CRLF = System.lineSeparator();

    
    /**
     * Read the first line
     *
     * @param br the reader
     * @param request the request
     * @return the first line
     * @throws IOException In case of an I/O error
     */
    protected String readInFirstLine(BufferedReader br, HttpRequest request) throws IOException {
        String input = "";
        
        if (br == null) {
            return input;
        }
        
        input = br.readLine();
        if (input == null) {
            return input;
        }

        final HttpRequestParser requestParser = new HttpRequestParser(input.trim());
        request.setMethod(requestParser.getMethod());
        request.setVersion(requestParser.getVersion());
        request.setPath(requestParser.getPath());
        request.setParameters(requestParser.getParameters());
        return input;
    }

    
    /**
     * Read in the body
     *
     * @param reader the reader
     * @param contentLength the content length
     * @return the body
     * @throws IOException In case of an I/O error
     */
    protected String readInBody(BufferedReader reader, int contentLength) throws IOException {
        if (reader == null || contentLength <= 0) {
            return "";
        }
        
        final char[] bodyInChars = new char[contentLength];
        reader.read(bodyInChars);
        return new String(bodyInChars);
    }

    
    /**
     * create a http response
     * @param request the reuqest
     * @return the response object
     */
    protected IHttpResponse createHttpResponse(IHttpRequest request) {
        return new HttpReponse();
    }

    
    /**
     * Get the status line
     *
     * @param response the http response
     * @return the status line 
     */
    protected String getStatusLine(IHttpResponse response) {
        if (response == null) {
            return null;
        }
        
        StringBuilder result = new StringBuilder();
        result.append(response.getVersion()).append(" ")
                    .append(response.getStatus())
                    .append(" ")
                    .append(HttpStatusUtil.getInstance().getStatusText(response.getStatus()))
                    .append(CRLF);
        
        return result.toString();
    }

    
    /**
     * Get the headers
     *
     * @param response the http response
     * @return the headers
     */
    protected String getHeaders(IHttpResponse response) {
        if (response == null) {
            return null;
        }
        
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
            result.append(header.getKey())
                    .append(": ")
                    .append(header.getValue())
                    .append(CRLF);
        }

        return result.toString();
    }

    
    /**
     * Get the body as byte array
     *
     * @param response the http response
     * @return the body as byte array
     */
    protected String getBodyAsByteArray(IHttpResponse response) {
        if (response == null || response.getBody() == null) {
            return null;
        }
        
        return response.getBody();
    }
}
