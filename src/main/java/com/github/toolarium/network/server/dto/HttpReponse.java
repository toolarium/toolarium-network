/*
 * HttpReponse.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.server.dto;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;


/**
 * Implements the {@link IHttpResponse}.
 * 
 * @author patrick
 */
public class HttpReponse implements IHttpResponse, Serializable {
    private static final long serialVersionUID = -2448680476757024255L;
    private int status;
    private String version;
    private Map<String, String> headers;
    private String body;

    
    /**
     * Constructor for HttpReponse
     */
    public HttpReponse() {
        headers = new LinkedHashMap<>();
        status = 200;
        version = "HTTP/1.1";
    }

    
    /**
     * @see com.github.toolarium.network.server.dto.IHttpResponse#getStatus()
     */
    @Override
    public int getStatus() {
        return status;
    }

    
    /**
     * Sets the status
     *
     * @param status the status
     * @return the http response
     */
    public HttpReponse setStatus(int status) {
        this.status = status;
        return this;
    }


    /**
     * @see com.github.toolarium.network.server.dto.IHttpResponse#getVersion()
     */
    @Override
    public String getVersion() {
        return version;
    }

    
    /**
     * Sets the version
     *
     * @param version the version
     * @return the http response
     */
    public HttpReponse setVersion(String version) {
        this.version = version;
        return this;
    }

    
    /**
     * Add a header
     *
     * @param name the header name
     * @param value the header value
     * @return the http response
     */
    public HttpReponse addHeader(String name, String value) {
        headers.put(name, value);
        return this;
    }


    /**
     * @see com.github.toolarium.network.server.dto.IHttpResponse#getHeaders()
     */
    @Override
    public Map<String, String> getHeaders() {
        return new LinkedHashMap<>(headers);
    }

    
    /**
     * @see com.github.toolarium.network.server.dto.IHttpResponse#getBody()
     */
    @Override
    public String getBody() {
        return body;
    }

    
    /**
     * Sets the body
     *
     * @param body the body
     * @return the http response
     */
    public HttpReponse setBody(String body) {
        this.body = body;
        return this;
    }


    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(body, headers, status, version);
    }


    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        HttpReponse other = (HttpReponse) obj;
        return Objects.equals(body, other.body) && Objects.equals(headers, other.headers) && status == other.status
                && Objects.equals(version, other.version);
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "HttpReponse [status=" + status + ", version=" + version + ", headers=" + headers + ", body=" + body + "]";
    }
}
