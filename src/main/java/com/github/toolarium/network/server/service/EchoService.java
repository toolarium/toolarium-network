/*
 * EchoService.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.server.service;

import com.github.toolarium.network.server.dto.HttpReponse;
import com.github.toolarium.network.server.dto.IHttpRequest;
import com.github.toolarium.network.server.dto.IHttpResponse;
import com.github.toolarium.network.server.logger.IHttpServerLogger;
import com.github.toolarium.network.server.util.HttpHeaderUtil;


/**
 * Implements a simple echo servie
 *  
 * @author patrick
 */
public class EchoService extends AbstractHttpService {
    // https://www.herongyang.com/JDK/SSL-Socket-Server-Example-SslReverseEchoer.html
    // https://github.com/undertow-io/undertow/blob/e8473ec35c420b782e072723d1e6338548def842/examples/src/main/java/io/undertow/examples/http2/Http2Server.java#L76

    /**
     * @see com.github.toolarium.network.server.service.IHttpService#processRequest(com.github.toolarium.network.server.logger.IHttpServerLogger, com.github.toolarium.network.server.dto.IHttpRequest)
     */
    @Override
    public IHttpResponse processRequest(IHttpServerLogger httpServerLogger, IHttpRequest request) {
        HttpReponse response = prepareResponse(request);
        response.setBody(request.getBody());
        if (response.getBody() == null) {
            response.setBody("");
        }

        if (response.getBody() != null) {
            response.addHeader(HttpHeaderUtil.CONTENT_LENGTH, "" + response.getBody().length());
        }
        
        return response;
    }
}
