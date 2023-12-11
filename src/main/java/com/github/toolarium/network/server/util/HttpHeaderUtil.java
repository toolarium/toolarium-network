/*
 * HttpHeaderUtil.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.server.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Http headers
 * 
 * @author patrick
 */
public final class HttpHeaderUtil {
    
    /** ALLOW */
    public static final String ALLOW = "Allow";
    
    /** AUTHORIZATION */
    public static final String AUTHORIZATION = "Authorization";
    
    /** CONTENT_RANGE */
    public static final String CONTENT_RANGE = "Content-Range";
    
    /** HOST */
    public static final String HOST = "Host";
    
    /** IF_MATCH */
    public static final String IF_MATCH = "If-Match";
    
    /** LOCATION */
    public static final String LOCATION = "Location";
    
    /** RANGE */
    public static final String RANGE = "Range";
    
    /** CONTENT_LENGTH */
    public static final String CONTENT_LENGTH = "Content-Length";
    
    /** CONTENT_LENGTH */
    public static final String WWW_AUTHENTICATE = "WWW-Authenticate";

    /** DATE */
    public static final String DATE = "Date";
    
    /** LAST_MODIFIED */
    public static final String LAST_MODIFIED = "Last-Modified";

    

    
    /**
     * Private class, the only instance of the singelton which will be created by accessing the holder class.
     *
     * @author patrick
     */
    private static class HOLDER {
        static final HttpHeaderUtil INSTANCE = new HttpHeaderUtil();
    }

    
    /**
     * Constructor
     */
    private HttpHeaderUtil() {
        // NOP
    }

    
    /**
     * Get the instance
     *
     * @return the instance
     */
    public static HttpHeaderUtil getInstance() {
        return HOLDER.INSTANCE;
    }
    
    
    /**
     * Read headers
     *
     * @param br the buffer
     * @return the headers
     * @throws IOException In case of an I/O error
     */
    public Map<String, String> readHeaders(BufferedReader br) throws IOException {
        if (br == null) {
            return null;
        }

        final Map<String, String> headers = new LinkedHashMap<>();
        String currentLine = br.readLine();
        while (currentLine != null && !currentLine.trim().isEmpty()) {
            currentLine = currentLine.trim();
            int idx = currentLine.indexOf(":");
            if (idx > 0) {
                headers.put(currentLine.substring(0, idx).trim(), currentLine.substring(idx + 1).trim());
            }
            
            currentLine = br.readLine();
        }
        
        return headers;
    }
}