/*
 * HttpRequestParser.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.server.handler.impl.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Http request parser
 *  
 * @author patrick
 */
public class HttpRequestParser {
    private static Map<String, String> decoderPairs = new HashMap<>();
    
    static {
        decoderPairs.put("%20", " ");
        decoderPairs.put("%3C", "<");
        decoderPairs.put("%2C", ",");
        decoderPairs.put("%3E", ">");
        decoderPairs.put("%3D", "=");
        decoderPairs.put("%3B", ";");
        decoderPairs.put("%2B", "+");
        decoderPairs.put("%26", "&");
        decoderPairs.put("%40", "@");
        decoderPairs.put("%23", "#");
        decoderPairs.put("%24", "\\$");
        decoderPairs.put("%5B", "[");
        decoderPairs.put("%5D", "]");
        decoderPairs.put("%3A", ":");
        decoderPairs.put("%22", "\"");
        decoderPairs.put("%3F", "\\?");
    }
    
    private String[] request;


    /**
     * Constructor for HttpRequestParser
     *
     * @param request the request
     */
    public HttpRequestParser(String request) {
        this.request = request.split(" ");
    }

    
    /**
     * Get the method
     *
     * @return the method
     */
    public String getMethod() {
        if (request.length == 0) {
            return "";
        }
        
        return request[0].trim();
    }

    
    /**
     * Get the path
     *
     * @return the path
     */
    public String getPath() {
        if (request.length <= 1) {
            return "";
        }
        
        return request[1].trim().split("\\?")[0];
    }

    
    /**
     * Get the version
     *
     * @return the version
     */
    public String getVersion() {
        if (request.length <= 2) {
            return "";
        }
        
        return request[2].trim();
    }

    
    /**
     * Get parameters
     *
     * @return the parameters
     */
    public Map<String, String> getParameters() {
        if (request.length <= 1) {
            return Collections.emptyMap();
        }

        Map<String, String> parameters = new LinkedHashMap<>();
        String pathWithParameters = request[1].trim();
        if (pathWithParameters != null && !pathWithParameters.isBlank()) {
            String[] splitPathFromParams = pathWithParameters.split("\\?");
            if (splitPathFromParams.length > 1) {
                String[] allParams = splitPathFromParams[1].split("&");
                for (String singleParam: allParams) {
                    String[] paramValuePair = singleParam.split("=");
                    String value = "";
                    if (paramValuePair.length > 1) {
                        value = paramValuePair[1];
                        for (Map.Entry<String, String> entry : decoderPairs.entrySet()) {
                            value = value.replaceAll(entry.getKey(), entry.getValue());
                        }
                    }
                    
                    parameters.put(paramValuePair[0], value);
                }
            }
        }
        
        return parameters;
    }
}
