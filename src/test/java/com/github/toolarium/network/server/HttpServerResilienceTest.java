/*
 * HttpServerResilienceTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toolarium.network.server.impl.HttpServerImpl;
import com.github.toolarium.network.server.service.EchoService;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for HTTP server resilience and security fixes:
 * - Oversized Content-Length rejection
 * - Malformed Content-Length handling
 * - Stop/start cycle
 * - Custom configuration (pool size, socket timeout)
 *
 * @author patrick
 */
public class HttpServerResilienceTest {
    private static final Logger LOG = LoggerFactory.getLogger(HttpServerResilienceTest.class);


    /**
     * Test that a request with Content-Length exceeding maxBodySize is rejected.
     * The server should close the connection (IOException on read) rather than allocating memory.
     *
     * @throws Exception In case of an error
     */
    @Test
    public void oversizedContentLengthTest() throws Exception {
        int port = 8100;

        HttpServerImpl server = (HttpServerImpl) HttpServerFactory.getInstance().getServerInstance();
        server.start(new EchoService(), port);
        Thread.sleep(100L);

        try {
            // Send a raw HTTP request with a Content-Length larger than the 10 MB default limit
            try (Socket socket = new Socket("localhost", port)) {
                socket.setSoTimeout(5000);
                OutputStream out = socket.getOutputStream();
                String rawRequest = "POST / HTTP/1.1\r\n"
                        + "Host: localhost\r\n"
                        + "Content-Length: 20971520\r\n"  // 20 MB > 10 MB limit
                        + "\r\n"
                        + "small body";
                out.write(rawRequest.getBytes(StandardCharsets.UTF_8));
                out.flush();

                // The server should reject and close the connection — we expect either
                // no response or a closed stream (IOException / empty read).
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line = reader.readLine();
                // Server may close immediately (null) or we may get no valid response
                LOG.info("Oversized body response: " + line);
                // The key assertion: server did NOT crash with OOM — it handled it gracefully.
                // If line is null, the server closed the connection (correct behavior).
                // If line is non-null, the server sent an error response (also acceptable).
                assertTrue(line == null || !line.contains("200 OK"),
                        "Server should not return 200 OK for oversized body");
            }
        } finally {
            server.stop();
        }
    }


    /**
     * Test that a request with malformed (non-numeric) Content-Length is handled gracefully.
     *
     * @throws Exception In case of an error
     */
    @Test
    public void malformedContentLengthTest() throws Exception {
        int port = 8101;

        HttpServerImpl server = (HttpServerImpl) HttpServerFactory.getInstance().getServerInstance();
        server.start(new EchoService(), port);
        Thread.sleep(100L);

        try {
            try (Socket socket = new Socket("localhost", port)) {
                socket.setSoTimeout(5000);
                OutputStream out = socket.getOutputStream();
                String rawRequest = "POST / HTTP/1.1\r\n"
                        + "Host: localhost\r\n"
                        + "Content-Length: notanumber\r\n"
                        + "\r\n"
                        + "body";
                out.write(rawRequest.getBytes(StandardCharsets.UTF_8));
                out.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line = reader.readLine();
                LOG.info("Malformed Content-Length response: " + line);
                // Server should not crash — connection closed or error returned
                assertTrue(line == null || !line.contains("200 OK"),
                        "Server should not return 200 OK for malformed Content-Length");
            }
        } finally {
            server.stop();
        }
    }


    /**
     * Test that a request with negative Content-Length is handled gracefully.
     *
     * @throws Exception In case of an error
     */
    @Test
    public void negativeContentLengthTest() throws Exception {
        int port = 8102;

        HttpServerImpl server = (HttpServerImpl) HttpServerFactory.getInstance().getServerInstance();
        server.start(new EchoService(), port);
        Thread.sleep(100L);

        try {
            try (Socket socket = new Socket("localhost", port)) {
                socket.setSoTimeout(5000);
                OutputStream out = socket.getOutputStream();
                String rawRequest = "POST / HTTP/1.1\r\n"
                        + "Host: localhost\r\n"
                        + "Content-Length: -1\r\n"
                        + "\r\n"
                        + "body";
                out.write(rawRequest.getBytes(StandardCharsets.UTF_8));
                out.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line = reader.readLine();
                LOG.info("Negative Content-Length response: " + line);
                assertTrue(line == null || !line.contains("200 OK"),
                        "Server should not return 200 OK for negative Content-Length");
            }
        } finally {
            server.stop();
        }
    }


    /**
     * Test that the server can be stopped and restarted on the same port without errors.
     *
     * @throws Exception In case of an error
     */
    @Test
    public void stopStartCycleTest() throws Exception {
        int port = 8103;
        HttpServerImpl server = (HttpServerImpl) HttpServerFactory.getInstance().getServerInstance();

        // First cycle
        server.start(new EchoService(), port);
        Thread.sleep(100L);

        HttpResponse<String> response1 = sendGetRequest(port, "/hello");
        assertNotNull(response1);
        assertEquals(200, response1.statusCode());
        assertEquals("hello", response1.body());

        server.stop();
        Thread.sleep(100L);

        // Second cycle — same instance, same port
        server.start(new EchoService(), port);
        Thread.sleep(100L);

        HttpResponse<String> response2 = sendGetRequest(port, "/world");
        assertNotNull(response2);
        assertEquals(200, response2.statusCode());
        assertEquals("world", response2.body());

        server.stop();
    }


    /**
     * Test that custom worker pool size and socket timeout are accepted and the server operates correctly.
     *
     * @throws Exception In case of an error
     */
    @Test
    public void customConfigurationTest() throws Exception {
        int port = 8104;

        HttpServerImpl server = (HttpServerImpl) HttpServerFactory.getInstance().getServerInstance();
        server.setWorkerPoolSize(5);
        server.setSocketTimeout(10_000);
        server.start(new EchoService(), port);
        Thread.sleep(100L);

        try {
            // Send multiple requests to verify the server works with a small pool
            for (int i = 0; i < 10; i++) {
                HttpResponse<String> response = sendGetRequest(port, "/test" + i);
                assertNotNull(response);
                assertEquals(200, response.statusCode());
                assertEquals("test" + i, response.body());
            }
        } finally {
            server.stop();
        }
    }


    /**
     * Test that the RFC 1123 date header is present and correctly formatted.
     *
     * @throws Exception In case of an error
     */
    @Test
    public void dateHeaderFormatTest() throws Exception {
        int port = 8105;

        HttpServerImpl server = (HttpServerImpl) HttpServerFactory.getInstance().getServerInstance();
        server.start(new EchoService(), port);
        Thread.sleep(100L);

        try {
            try (Socket socket = new Socket("localhost", port)) {
                socket.setSoTimeout(5000);
                OutputStream out = socket.getOutputStream();
                String rawRequest = "GET /test HTTP/1.1\r\n"
                        + "Host: localhost\r\n"
                        + "\r\n";
                out.write(rawRequest.getBytes(StandardCharsets.UTF_8));
                out.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                StringBuilder fullResponse = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    fullResponse.append(line).append("\n");
                }

                String responseStr = fullResponse.toString();
                LOG.info("Date header response:\n" + responseStr);

                // Verify Date header is present and has correct 4-digit year format
                assertTrue(responseStr.contains("Date:"), "Response should contain Date header");
                // Should contain a 4-digit year (2025, 2026, etc.), not a 5-digit year
                assertTrue(responseStr.matches("(?s).*Date:.*20\\d{2}.*"),
                        "Date header should contain a 4-digit year");
            }
        } finally {
            server.stop();
        }
    }


    /**
     * Send a simple GET request using HttpClient.
     *
     * @param port the port
     * @param path the path
     * @return the response
     * @throws Exception In case of an error
     */
    private HttpResponse<String> sendGetRequest(int port, String path) throws Exception {
        HttpRequest request = HttpRequest
                .newBuilder(URI.create("http://localhost:" + port + path))
                .GET()
                .build();

        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build()
                .send(request, BodyHandlers.ofString());
    }
}
