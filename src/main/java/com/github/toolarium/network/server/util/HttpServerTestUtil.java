/*
 * HttpServerTestUtil.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.server.util;

import com.github.toolarium.common.util.ExceptionWrapper;
import com.github.toolarium.common.util.ThreadUtil;
import com.github.toolarium.network.server.HttpServerFactory;
import com.github.toolarium.network.server.IHttpServer;
import com.github.toolarium.network.server.service.IHttpService;
import com.github.toolarium.security.keystore.ISecurityManagerProvider;
import com.github.toolarium.security.keystore.SecurityManagerProviderFactory;
import com.github.toolarium.security.ssl.SSLContextFactory;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.Locale;
import java.util.Optional;
import javax.net.ssl.SSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;


/**
 * Http server utility for test cases
 * 
 * @author patrick
 */
public final class HttpServerTestUtil {
    private static final Logger LOG = LoggerFactory.getLogger(HttpServerTestUtil.class);

    /**
     * Private class, the only instance of the singelton which will be created by accessing the holder class.
     *
     * @author patrick
     */
    private static class HOLDER {
        static final HttpServerTestUtil INSTANCE = new HttpServerTestUtil();
    }

    
    /**
     * Constructor
     */
    private HttpServerTestUtil() {
        // NOP
    }

    
    /**
     * Get the instance
     *
     * @return the instance
     */
    public static HttpServerTestUtil getInstance() {
        return HOLDER.INSTANCE;
    }

    
    /**
     * Execute a http or https call to a http-server
     * 
     * @param httpService the http service to prcoess the request
     * @param httpRequest the http request the request to send
     * @return the response
     * @throws GeneralSecurityException In case of a security issue
     * @throws IOException In case of an I/O error
     */
    public HttpResponse<String> runHttps(IHttpService httpService, HttpRequest httpRequest) throws GeneralSecurityException, IOException {
        SSLContext sslContext = null;

        URI uri = httpRequest.uri();
        try {
            uri = new URI(uri.getScheme().toLowerCase(Locale.US), "localhost" + ":" + uri.getPort(), uri.getPath(), uri.getQuery(), uri.getFragment());
        } catch (URISyntaxException e) {
            LOG.warn("Error occured: " + e.getMessage(), e);
        }
        final HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(uri, httpRequest);
        
        if ("https".equals(httpRequestWrapper.uri().getScheme())) {
            // create self signed certificate
            final ISecurityManagerProvider securityManagerProvider = SecurityManagerProviderFactory.getInstance().getSecurityManagerProvider("toolarium", "changit");
            
            // get ssl context
            sslContext = SSLContextFactory.getInstance().createSslContext(securityManagerProvider);
        }
        
        // start web server
        final IHttpServer httpServer = HttpServerFactory.getInstance().getServerInstance();
        httpServer.start(httpService, httpRequestWrapper.uri().getPort(), sslContext);

        HttpClient.Builder httpClientBuilder = HttpClient
                .newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10));

        if (sslContext != null) {
            httpClientBuilder.sslContext(sslContext);
        }
        
        return executeCall(httpServer, httpClientBuilder.build(), httpRequestWrapper);
    }


    /**
     * Execute a https call to a webserver
     * 
     * @param httpService the http service
     * @param httpRequest the http request
     * @return the response
     * @throws GeneralSecurityException In case of a security issue
     * @throws IOException In case of an I/O error
     */
    public HttpResponse<String> runHttp(IHttpService httpService, HttpRequest httpRequest) throws GeneralSecurityException, IOException {

        URI uri = httpRequest.uri();
        try {
            uri = new URI(uri.getScheme().toLowerCase(Locale.US), "localhost" + ":" + uri.getPort(), uri.getPath(), uri.getQuery(), uri.getFragment());
        } catch (URISyntaxException e) {
            LOG.warn("Error occured: " + e.getMessage(), e);
        }
        final HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(uri, httpRequest);

        // start web server
        final IHttpServer httpServer = HttpServerFactory.getInstance().getServerInstance();
        httpServer.start(httpService, httpRequestWrapper.uri().getPort());
        
        final HttpClient httpClient = HttpClient
                .newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        
        return executeCall(httpServer, httpClient, httpRequestWrapper);
    }


    /**
     * Execute a http call to a server
     * 
     * @param httpServer the http server
     * @param httpClient the http client
     * @param httpRequest the http request
     * @return the response
     * @throws GeneralSecurityException In case of a security issue
     * @throws IOException In case of an I/O error
     */
    private HttpResponse<String> executeCall(IHttpServer httpServer, HttpClient httpClient, HttpRequest httpRequest) throws GeneralSecurityException, IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Server hostname: " + httpServer.getHttpServerInformation().getHostname() + ":" + httpServer.getHttpServerInformation().getPort());
        }
        ThreadUtil.getInstance().sleep(100L);

        HttpResponse<String> response = null;
        try {
            response = httpClient.send(httpRequest, BodyHandlers.ofString());
        } catch (InterruptedException e) {
            ExceptionWrapper.getInstance().convertException(e, IOException.class, Level.WARN, "Interrupted http call: " + e.getMessage());
        } finally {
            httpServer.stop();
        }
        
        if (response != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Response: [" + response.body() + "]");
            }
        }
        
        return response;
    }


    /**
     * Wrap {@link HttpRequest} to take care of hostname and port.
     * 
     * @author patrick
     */
    class HttpRequestWrapper extends HttpRequest {
        private URI uri;
        private HttpRequest httpRequest;
        
        
        /**
         * Constructor for HttpRequestWrapper
         *
         * @param uri the uri
         * @param httpRequest the request to wrap
         */
        HttpRequestWrapper(URI uri, HttpRequest httpRequest) {
            this.uri = uri;
            this.httpRequest = httpRequest;
        }

        
        /**
         * Constructor for HttpRequestWrapper
         *
         * @param https true to use https otherwise false
         * @param port the port
         * @param path the path
         * @param query the query parameter
         * @param fragment the fragment
         */
        HttpRequestWrapper(boolean https, int port, String path, String query, String fragment) {
            
            StringBuilder builder = new StringBuilder();
            builder.append("http");
            
            if (https) {
                builder.append("s");
            }
            
            builder.append("://");
            builder.append("localhost");
            builder.append(":");
            
            if (port > 0) {
                builder.append(port);
            }
            
            if (path != null && !path.isBlank()) {
                if (!path.startsWith("/")) {
                    builder.append("/");
                }
                builder.append(path.trim());
            }

            if (query != null && !query.isBlank()) {
                if (!query.startsWith("?")) {
                    builder.append("?");
                }
                builder.append(query.trim());
            }

            if (fragment != null && !fragment.isBlank()) {
                if (!query.startsWith("#")) {
                    builder.append("#");
                }
                builder.append(fragment.trim());
            }

            uri = URI.create(builder.toString());
        }


        
        /**
         * @see java.net.http.HttpRequest#bodyPublisher()
         */
        @Override
        public Optional<BodyPublisher> bodyPublisher() {
            return httpRequest.bodyPublisher();
        }

        
        /**
         * @see java.net.http.HttpRequest#method()
         */
        @Override
        public String method() {
            return httpRequest.method();
        }

        
        /**
         * @see java.net.http.HttpRequest#timeout()
         */
        @Override
        public Optional<Duration> timeout() {
            return httpRequest.timeout();
        }
            

        /**
         * @see java.net.http.HttpRequest#expectContinue()
         */
        @Override
        public boolean expectContinue() {
            return httpRequest.expectContinue();
        }


        /**
         * @see java.net.http.HttpRequest#uri()
         */
        @Override
        public URI uri() {
            return uri;
        }

        
        /**
         * @see java.net.http.HttpRequest#version()
         */
        @Override
        public Optional<HttpClient.Version> version() {
            return httpRequest.version();
        }

        
        /**
         * @see java.net.http.HttpRequest#headers()
         */
        @Override
        public HttpHeaders headers() {
            return httpRequest.headers();
        }
    }
}
