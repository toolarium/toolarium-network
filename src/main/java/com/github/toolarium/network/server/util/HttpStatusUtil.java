/*
 * HttpStatusUtil.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.server.util;


/**
 * Defines the http status
 * 
 * @author patrick
 */
public final class HttpStatusUtil {

    /**
     * Private class, the only instance of the singelton which will be created by accessing the holder class.
     *
     * @author patrick
     */
    private static class HOLDER {
        static final HttpStatusUtil INSTANCE = new HttpStatusUtil();
    }

    
    /**
     * Constructor
     */
    private HttpStatusUtil() {
        // NOP
    }

    
    /**
     * Get the instance
     *
     * @return the instance
     */
    public static HttpStatusUtil getInstance() {
        return HOLDER.INSTANCE;
    }

    
    /**
     * Get the status text
     *
     * @param statusCode the status code
     * @return the status as string
     */
    public String getStatusText(int statusCode) {
        switch (statusCode) {
            case 200: return "OK"; // [GET/PUT/PATCH] The request could successfully processed.
            case 201: return "CREATED"; // [POST] The resource has been successfully created.
            case 204: return "NO CONTENT"; // [DELETE] The server successfully processed the request and is not returning any content.
            case 300: return "MOVED"; // [GET/DELETE/POST/PUT/PATCH] The resource has moved.
            case 400: return "BAD REQUEST"; // [POST/PUT/PATCH] The request could not be understood by the server due to malformed syntax. Something like Domain validation errors, missing data, invalid input, etc.
            case 401: return "UNAUTHORIZED"; // [GET/PUT/PATCH/DELETE] The caller is not authorized.
            case 403: return "FORBIDDEN"; // [GET/PUT/PATCH/DELETE] The caller has no permission.
            case 404: return "NOT FOUND"; // [GET/PUT/PATCH/DELETE] The server has not found anything matching the Request-URI.
            case 405: return "Method Not Allowed";
            case 408: return "TIMEOUT"; // [GET/PUT/PATCH/DELETE] The request timeout. 
            case 409: return "CONFLICT"; // [GET/PUT/PATCH/DELETE] Indicates that the request could not be processed because of conflict. 
            case 413: return "TOO LARGE"; // [GET/PUT/PATCH/DELETE]  Too Large: The payload request entity is too large. 
            case 500: return "INTERNAL SERVER ERROR"; // [*] The server encountered an internal error.
            case 503: return "SERVICE UNAVAILABLE"; // [*] The service is temporarily unavailable.
            default: return "OK";
        }
    }
}
