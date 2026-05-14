/*
 * AbstractHttpService.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.server.service;

import com.github.toolarium.network.server.dto.HttpReponse;
import com.github.toolarium.network.server.dto.IHttpRequest;
import com.github.toolarium.network.server.handler.IHttpConnectionHandler;
import com.github.toolarium.network.server.handler.impl.HttpConnectionHandlerImpl;
import com.github.toolarium.network.server.logger.IHttpServerLogger;
import com.github.toolarium.network.server.util.HttpHeaderUtil;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


/**
 * Abstract base http service class
 * 
 * @author patrick
 */
public abstract class AbstractHttpService implements IHttpService {
    // Thread-safe formatter for RFC 1123 date strings -- "Sun, 06 Nov 1994 08:49:37 GMT"
    private static final DateTimeFormatter RFC_1123_FORMAT = DateTimeFormatter
            .ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);

    
    /**
     * @see com.github.toolarium.network.server.service.IHttpService#start(com.github.toolarium.network.server.logger.IHttpServerLogger, int)
     */
    @Override
    public boolean start(IHttpServerLogger httpServerLogger, int port) {
        return true;
    }


    /**
     * @see com.github.toolarium.network.server.service.IHttpService#stop(com.github.toolarium.network.server.logger.IHttpServerLogger, int)
     */
    @Override
    public void stop(IHttpServerLogger httpServerLogger, int port) {
    }
    
    
    /**
     * @see com.github.toolarium.network.server.service.IHttpService#getHttpConnectionHandler()
     */
    @Override
    public IHttpConnectionHandler getHttpConnectionHandler() {
        return new HttpConnectionHandlerImpl();
    }

    
    /**
     * Prepare response
     *
     * @param request the request
     * @return the response
     */
    protected HttpReponse prepareResponse(IHttpRequest request) {
        HttpReponse response = new HttpReponse();
        response.setVersion(request.getVersion());
        final String dateStr = getRFC1123Timestamp();
        response.addHeader(HttpHeaderUtil.DATE, dateStr);
        response.addHeader(HttpHeaderUtil.LAST_MODIFIED, dateStr);
        return response;
    }
    
    
    /**
     * Get the current time stamp in RFC 1123 format
     *
     * @return the current time stamp in RFC 1123 format
     */
    protected String getRFC1123Timestamp() {
        return RFC_1123_FORMAT.format(ZonedDateTime.now(ZoneOffset.UTC));
    }
}
