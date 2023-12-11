/*
 * HttpRequest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.server.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Objects;


/**
 * Implements the {@link IHttpRequest}.
 *  
 * @author patrick
 */
public class HttpRequest implements IHttpRequest, Serializable {
    private static final long serialVersionUID = 465856949478530369L;
    private Date requestTimestamp;
    private String version;
    private String path;
    private String method;
    private Map<String, String> headers;
    private Map<String, String> parameters;
    private String baseLocation;
    private String body;

    
    /**
     * Constructor for HttpRequest
     *
     */
    public HttpRequest() {
        requestTimestamp = new Date();
    }

    
    /**
     * @see com.github.toolarium.network.server.dto.IHttpRequest#getRequestTimestamp()
     */
    @Override
    public Date getRequestTimestamp() {
        return requestTimestamp;
    }
    
    
    /**
     * Sets the request time stamp
     *
     * @param requestTimestamp the request time stamp
     * @return the http request
     */
    public HttpRequest setRequestTimestamp(Date requestTimestamp) {
        this.requestTimestamp = requestTimestamp;
        return this;
    }

    
    /**
     * @see com.github.toolarium.network.server.dto.IHttpRequest#getVersion()
     */
    @Override
    public String getVersion() {
        return version;
    }
    
    
    /**
     * Sets the version
     *
     * @param version the version
     * @return the http request
     */
    public HttpRequest setVersion(String version) {
        this.version = version;
        return this;
    }

    
    /**
     * @see com.github.toolarium.network.server.dto.IHttpRequest#getPath()
     */
    @Override
    public String getPath() {
        return path;
    }
    
    
    /**
     * Sets the path
     *
     * @param path the path
     * @return the http request
     */
    public HttpRequest setPath(String path) {
        this.path = path;
        return this;
    }


    /**
     * @see com.github.toolarium.network.server.dto.IHttpRequest#getMethod()
     */
    @Override
    public String getMethod() {
        return method;
    }
    
    
    /**
     * Sets the method
     *
     * @param method the method
     * @return the http request
     */
    public HttpRequest setMethod(String method) {
        this.method = method;
        return this;
    }


    /**
     * @see com.github.toolarium.network.server.dto.IHttpRequest#getHeader(java.lang.String)
     */
    @Override
    public String getHeader(String headerName) {
        return headers.get(headerName);
    }


    /**
     * @see com.github.toolarium.network.server.dto.IHttpRequest#containsHeader(java.lang.String)
     */
    @Override
    public  boolean containsHeader(String headerName) {
        return headers.containsKey(headerName);
    }

    
    /**
     * @see com.github.toolarium.network.server.dto.IHttpRequest#getHeader(java.lang.String)
     */
    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    
    /**
     * Sets the headers
     *
     * @param headers the headers
     * @return the http request
     */
    public HttpRequest setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }


    /**
     * @see com.github.toolarium.network.server.dto.IHttpRequest#getParameter(java.lang.String)
     */
    @Override
    public String getParameter(String parameterName) {
        return parameters.get(parameterName);
    }


    /**
     * @see com.github.toolarium.network.server.dto.IHttpRequest#containsParameter(java.lang.String)
     */
    @Override
    public boolean containsParameter(String parameterName) {
        return parameters.containsKey(parameterName);
    }


    /**
     * @see com.github.toolarium.network.server.dto.IHttpRequest#getParameters()
     */
    @Override
    public Map<String, String> getParameters() {
        return parameters;
    }

    
    /**
     * Sets the parameters
     *
     * @param parameters the parameters
     * @return the http request
     */
    public HttpRequest setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
        return this;
    }


    /**
     * @see com.github.toolarium.network.server.dto.IHttpRequest#getBaseLocation()
     */
    @Override
    public String getBaseLocation() {
        return baseLocation;
    }

    
    /**
     * Sets the base location
     *
     * @param baseLocation the base location
     * @return the http request
     */
    public HttpRequest setBaseLocation(String baseLocation) {
        this.baseLocation = baseLocation;
        return this;
    }


    /**
     * @see com.github.toolarium.network.server.dto.IHttpRequest#getBody()
     */
    @Override
    public String getBody() {
        return body;
    }

    
    /**
     * Sets the body
     *
     * @param body the body
     * @return the http request
     */
    public HttpRequest setBody(String body) {
        this.body = body;
        return this;
    }


    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(baseLocation, body, headers, method, parameters, path, requestTimestamp, version);
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
        
        HttpRequest other = (HttpRequest) obj;
        return Objects.equals(baseLocation, other.baseLocation) && Objects.equals(body, other.body)
                && Objects.equals(headers, other.headers) && Objects.equals(method, other.method)
                && Objects.equals(parameters, other.parameters) && Objects.equals(path, other.path)
                && Objects.equals(requestTimestamp, other.requestTimestamp) && Objects.equals(version, other.version);
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "HttpRequest [requestTimestamp=" + requestTimestamp + ", version=" + version + ", path=" + path
                + ", method=" + method + ", headers=" + headers + ", parameters=" + parameters + ", baseLocation="
                + baseLocation + ", body=" + body + "]";
    }
}
