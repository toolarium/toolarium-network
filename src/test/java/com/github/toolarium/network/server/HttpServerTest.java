/*
 * HttpServerTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.github.toolarium.network.server.service.EchoService;
import com.github.toolarium.network.server.service.PingService;
import com.github.toolarium.network.server.util.HttpServerTestUtil;
import com.github.toolarium.security.keystore.ISecurityManagerProvider;
import com.github.toolarium.security.keystore.SecurityManagerProviderFactory;
import com.github.toolarium.security.ssl.SSLContextFactory;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import javax.net.ssl.SSLContext;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Httop server tests
 * 
 * @author patrick
 */
public class HttpServerTest {
    private static final String COLON = ":";
    private static final Logger LOG = LoggerFactory.getLogger(HttpServerTest.class);

    
    /**
     * Echo test
     *
     * @throws Exception In case of an exception
     */
    @Test
    public void echoTest() throws Exception {
        int port = 8081;
        
        IHttpServer server = HttpServerFactory.getInstance().getServerInstance();
        server.start(new EchoService(), port);
        Thread.sleep(50L);
        
        //String content = "my test";
        LOG.debug("Server hostname: " + server.getHttpServerInformation().getHostname() + ":" + server.getHttpServerInformation().getPort());
        HttpRequest request = HttpRequest
                .newBuilder(URI.create("http://localhost" + COLON + port + "/echo")) //  + server.getHttpServerInformation().getHostname()
                .GET()
                //.POST(HttpRequest.BodyPublishers.ofString(content))
                .build();
        
        HttpResponse<String> response = HttpClient
                .newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build()
                .send(request, BodyHandlers.ofString());
        
        LOG.info("Response: " + response.body());
        assertEquals("echo", response.body());
        //assertEquals(content, response.body());
        server.stop();
    }

    
    /**
     * Echo test
     *
     * @throws Exception In case of an exception
     */
    @Test
    public void echoSSLTest() throws Exception {
        int port = 8082;
        
        // create self signed certificate
        ISecurityManagerProvider securityManagerProvider = SecurityManagerProviderFactory.getInstance().getSecurityManagerProvider("toolarium", "changit");
        assertNotNull(securityManagerProvider);

        // get ssl context from factory
        SSLContext sslContext = SSLContextFactory.getInstance().createSslContext(securityManagerProvider);

        IHttpServer server = HttpServerFactory.getInstance().getServerInstance();
        server.start(new EchoService(), port, sslContext);
        Thread.sleep(100L);
        
        String content = "my test";
        LOG.debug("Server hostname: " + server.getHttpServerInformation().getHostname() + ":" + server.getHttpServerInformation().getPort());
        HttpRequest request = HttpRequest
                .newBuilder(URI.create("https://localhost" + COLON + port)) //  + server.getHttpServerInformation().getHostname()
                //.GET()
                .POST(HttpRequest.BodyPublishers.ofString(content))
                .build();
        
        HttpResponse<String> response = HttpClient
                .newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .sslContext(sslContext)
                .build()
                .send(request, BodyHandlers.ofString());
        
        LOG.info("Response: " + response.body());
        assertEquals(content, response.body());
        server.stop();
    }

    
    /**
     * Ping test
     *
     * @throws Exception In case of an exception
     */
    @Test
    public void pingTest() throws Exception {
        int port = 8083;
        
        // create self signed certificate
        IHttpServer server = HttpServerFactory.getInstance().getServerInstance();
        server.start(new PingService(), port);
        Thread.sleep(100L);
        
        LOG.debug("Server hostname: " + server.getHttpServerInformation().getHostname() + ":" + server.getHttpServerInformation().getPort());
        
        String host = "localhost";
        long result = new PingService.Client().ping(host, port);
        LOG.info("Ping " + host + ", reply in " + result + "ms");

        HttpClient client = HttpClient
                .newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpRequest request = HttpRequest
                .newBuilder(URI.create("http://localhost" + COLON + port)) //  + server.getHttpServerInformation().getHostname()
                .GET()
                .build();
        
        final long t1 = System.currentTimeMillis();
        client.send(request, BodyHandlers.ofString());
        final long t2 = System.currentTimeMillis();
        LOG.info("Ping " + host + ", reply in " + (t2 - t1) + "ms");
        server.stop();
    }   

    
    /**
     * Test Echo server
     *
     * @throws Exception In case of an error
     */
    @Test
    public void echoSSLGetTest() throws Exception {
        int port = 8084;
        HttpRequest request = HttpRequest
                .newBuilder(URI.create("https://localhost" + COLON + port + "/echo"))
                .GET()
                .build();
        
        HttpResponse<String> response = HttpServerTestUtil.getInstance().runHttps(new EchoService(), request);
        assertEquals(200, response.statusCode());
        assertEquals("echo", response.body());
    }

    
    /**
     * Test Echo server
     *
     * @throws Exception In case of an error
     */
    @Test
    public void echoSSLGetWithBodyTest() throws Exception {
        int port = 8085;
        String content = "my test";
        HttpRequest request = HttpRequest
                .newBuilder(URI.create("https://localhost" + COLON + port + "/echo"))
                .method("GET", HttpRequest.BodyPublishers.ofString(content))
                .build();
        
        HttpResponse<String> response = HttpServerTestUtil.getInstance().runHttps(new EchoService(), request);
        assertEquals(200, response.statusCode());
        assertEquals("echo", response.body());
    }

    
    /**
     * Test Echo server
     *
     * @throws Exception In case of an error
     */
    @Test
    public void echoSSLPostTest() throws Exception {
        int port = 8086;
        String content = "{\"action\":\"hello\"}";
        HttpRequest request = HttpRequest
                .newBuilder(URI.create("https://localhost" + COLON + port))
                .POST(HttpRequest.BodyPublishers.ofString(content))
                .build();
        
        HttpResponse<String> response = HttpServerTestUtil.getInstance().runHttps(new EchoService(), request);
        assertEquals(200, response.statusCode());
        assertEquals(content, response.body());
    }
}
