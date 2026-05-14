/*
 * HttpServer.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.server.impl;

import com.github.toolarium.network.server.IHttpServer;
import com.github.toolarium.network.server.dto.HttpServerInformation;
import com.github.toolarium.network.server.dto.IHttpServerInformation;
import com.github.toolarium.network.server.handler.IHttpConnectionHandler;
import com.github.toolarium.network.server.logger.IHttpAccessLogger;
import com.github.toolarium.network.server.logger.IHttpServerLogger;
import com.github.toolarium.network.server.logger.impl.ConsoleHttpAccessLogger;
import com.github.toolarium.network.server.logger.impl.ConsoleHttpServerLogger;
import com.github.toolarium.network.server.service.IHttpService;
import com.github.toolarium.network.util.NetworkUtil;
import com.github.toolarium.security.ssl.util.SSLUtil;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Defines a http server
 * 
 * <p>NIO: see samples 
 * http://www.java2s.com/example/java/network/echo-server-via-serversocketchannel.html
 * http://www.java2s.com/example/java/network/echo-client-via-socketchannel.html
 * </p>
 * @author patrick
 */
public class HttpServerImpl implements IHttpServer {
    static final int DEFAULTPORT = 8080;
    static final int DEFAULT_SOCKET_TIMEOUT = 30_000;
    static final int DEFAULT_WORKER_POOL_SIZE = 100;
    private static final Logger LOG = LoggerFactory.getLogger(HttpServerImpl.class);
    private ServerSocket serverSocket;
    private ExecutorService mainExecutor;
    private ExecutorService executor;
    private volatile boolean run;
    private int socketTimeout;
    private int workerPoolSize;
    private IHttpService httpService;
    private HttpServerInformation httpServerInformation;
    private IHttpServerLogger httpServerLogger;
    private IHttpAccessLogger httpAccessLogger;

    
    /**
     * Constructor for HttpServer
     */
    public HttpServerImpl() {
        this.serverSocket = null;
        this.mainExecutor = null;
        this.executor = null;
        this.run = false;
        this.socketTimeout = DEFAULT_SOCKET_TIMEOUT;
        this.workerPoolSize = DEFAULT_WORKER_POOL_SIZE;
        this.httpService = null;
        
        httpServerInformation = new HttpServerInformation();
        httpServerInformation.setPort(DEFAULTPORT);
        httpServerInformation.setHostname(NetworkUtil.getInstance().getHostname());
        httpServerInformation.setLocalIpAddress(NetworkUtil.getInstance().getHostIPAddress());
        
        this.httpServerLogger = new ConsoleHttpServerLogger();
        this.httpAccessLogger = new ConsoleHttpAccessLogger();
    }

    
    /**
     * @see com.github.toolarium.network.server.IHttpServer#init(com.github.toolarium.network.server.logger.IHttpServerLogger, com.github.toolarium.network.server.logger.IHttpAccessLogger)
     */
    @Override
    public void init(IHttpServerLogger httpServerLogger, IHttpAccessLogger httpAccessLogger) {
        this.httpServerLogger = httpServerLogger;
        this.httpAccessLogger = httpAccessLogger;
    }


    /**
     * Set the socket read timeout in milliseconds. Connections that do not send
     * data within this period are closed. Must be called before {@link #start}.
     *
     * @param socketTimeout the timeout in milliseconds (0 = infinite)
     */
    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }


    /**
     * Set the worker thread pool size. Must be called before {@link #start}.
     *
     * @param workerPoolSize the number of worker threads
     */
    public void setWorkerPoolSize(int workerPoolSize) {
        this.workerPoolSize = workerPoolSize;
    }


    /**
     * @see com.github.toolarium.network.server.IHttpServer#start(com.github.toolarium.network.server.service.IHttpService, int)
     */
    @Override
    public void start(IHttpService inputHttpService, int port) throws IOException {
        start(inputHttpService, port, null);
    }


    /**
     * @see com.github.toolarium.network.server.IHttpServer#start(com.github.toolarium.network.server.service.IHttpService, int, javax.net.ssl.SSLContext)
     */
    @Override
    public void start(IHttpService inputHttpService, int port, SSLContext sslContext) throws IOException {
        if (inputHttpService == null) {
            run = false;
            return;
        }

        httpServerInformation.setPort(port);
        httpServerInformation.setSSLContext(sslContext);

        this.mainExecutor = Executors.newFixedThreadPool(1);
        this.executor = new ThreadPoolExecutor(
                workerPoolSize, workerPoolSize,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(workerPoolSize * 2),
                new ThreadPoolExecutor.CallerRunsPolicy());

        if (sslContext != null) {
            SSLServerSocketFactory ssf = sslContext.getServerSocketFactory();
            SSLServerSocket s = (SSLServerSocket) ssf.createServerSocket(httpServerInformation.getPort());
            serverSocket = s;
        } else {
            serverSocket = new ServerSocket(httpServerInformation.getPort());
        }

        if (httpServerLogger != null) {
            httpServerLogger.logInfo("Start " + httpServerInformation.getProtocol() + " server on port " + httpServerInformation.getPort());
        }

        httpService = inputHttpService;
        run = this.httpService.start(httpServerLogger, httpServerInformation.getPort());

        if (httpAccessLogger != null) {
            httpAccessLogger.start();
        }

        mainExecutor.execute(new Runnable() {
            /**
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
                while (run) {
                    try {
                        final Socket socket = serverSocket.accept();
                        socket.setSoTimeout(socketTimeout);

                        if (socket instanceof SSLSocket && LOG.isDebugEnabled()) {
                            SSLSocket c = (SSLSocket)socket;
                            SSLUtil.getInstance().processSocketInfo(LOG::debug, c);
                        }

                        if (socket != null) {
                            final IHttpConnectionHandler connectionHandler = httpService.getHttpConnectionHandler();
                            connectionHandler.init(socket, httpService, httpServerInformation, httpServerLogger, httpAccessLogger);
                            executor.execute(connectionHandler);
                        }
                    } catch (Exception e) {
                        if (serverSocket != null && !serverSocket.isClosed()) {
                            if (httpServerLogger != null) {
                                httpServerLogger.logWarn("Exception occured: " + e.getMessage(), e);
                            }
                        }
                    }
                }
            }
        });
    }
    

    /**
     * @see com.github.toolarium.network.server.IHttpServer#stop()
     */
    @Override
    public void stop() throws IOException {
        if (httpServerLogger != null) {
            httpServerLogger.logInfo("Stop http server on port " + httpServerInformation.getPort());
        }
        
        if (httpService != null) {
            httpService.stop(httpServerLogger, httpServerInformation.getPort());
            run = false;
            mainExecutor.shutdown();
            executor.shutdown();
            
            try {
                serverSocket.close();
            } catch (Exception e) {
                // NOP
            }
            serverSocket = null;
            
            if (httpAccessLogger != null) {
                httpAccessLogger.stop();
            }
        }
    }
    
    
    /**
     * @see com.github.toolarium.network.server.IHttpServer#getHttpServerInformation()
     */
    @Override
    public IHttpServerInformation getHttpServerInformation() {
        return httpServerInformation;
    }
}
