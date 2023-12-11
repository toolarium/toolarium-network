/*
 * HttpServerFactory.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.server;


import com.github.toolarium.network.server.impl.HttpServerImpl;


/**
 * Defines the http server factory
 *  
 * @author patrick
 */
public final class HttpServerFactory {

    /**
     * Private class, the only instance of the singelton which will be created by accessing the holder class.
     *
     * @author patrick
     */
    private static class HOLDER {
        static final HttpServerFactory INSTANCE = new HttpServerFactory();
    }

    
    /**
     * Constructor
     */
    private HttpServerFactory() {
        // NOP
    }

    
    /**
     * Get the instance
     *
     * @return the instance
     */
    public static HttpServerFactory getInstance() {
        return HOLDER.INSTANCE;
    }

    
    /**
     * Get a http server instance
     *
     * @return the http server instance
     */
    public IHttpServer getServerInstance() {
        return new HttpServerImpl();
    }
}
