/*
 * PingService.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.server.service;

import com.github.toolarium.network.server.dto.HttpReponse;
import com.github.toolarium.network.server.dto.IHttpRequest;
import com.github.toolarium.network.server.dto.IHttpResponse;
import com.github.toolarium.network.server.logger.IHttpServerLogger;
import com.github.toolarium.network.server.util.HttpHeaderUtil;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implements a simple ping service
 * 
 * @author patrick
 */
public class PingService extends AbstractHttpService {
    // https://docs.oracle.com/javase/8/docs/technotes/guides/io/example/Ping.java
    // https://www.livetolearn.in/site/programming/java/ping-server-and-client-java
    private static final Logger LOG = LoggerFactory.getLogger(PingService.class);
    
    
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
    

    
    /**
     * Ping client
     * 
     * @author patrick
     */
    public static class Client {

        /**
         * Ping 
         * 
         * @param host the host
         * @param port the port to ping
         * @return the ping in milliseconds
         */
        public long ping(String host, int port) {
            long result = 0;
            Socket socket = null;
            BufferedReader reader = null;
            BufferedWriter writer = null;

            try {
                socket = new Socket(host, port);
                String str = "TEST";
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                final long t1 = System.currentTimeMillis();
                writer.write(str);
                writer.newLine();
                writer.flush();
                reader.readLine();
                result = (System.currentTimeMillis() - t1);
                LOG.debug("Ping " + socket.getInetAddress() + ", reply from " + socket.getInetAddress() + " -> " + result + "ms");
            } catch (IOException e) {
                LOG.warn("Could not ping: " + e.getMessage(), e);
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        // NOP
                    }
                }
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // NOP
                    }
                }
            }
            
            return result;
        }        
    }
}
