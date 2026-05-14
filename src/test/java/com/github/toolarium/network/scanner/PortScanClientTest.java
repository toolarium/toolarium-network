/*
 * PortScanClientTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.scanner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toolarium.network.server.HttpServerFactory;
import com.github.toolarium.network.server.IHttpServer;
import com.github.toolarium.network.server.service.EchoService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import org.slf4j.bridge.SLF4JBridgeHandler;


/**
 * 
 * @author patrick
 */
public class PortScanClientTest {
    
    /**
     * Test
     * 
     * @throws InterruptedException In case of an interrupt 
     * @throws IOException In case of an /O error 
     */
    @Test 
    public void test() throws InterruptedException, IOException {
        redirectJavaLoggingToSlf4j();
        
        IHttpServer server1 = HttpServerFactory.getInstance().getServerInstance();
        server1.start(new EchoService(), 8010);
        IHttpServer server2 = HttpServerFactory.getInstance().getServerInstance();
        server2.start(new EchoService(), 8031);
        Thread.sleep(50L);

        PortScannerClient.main(new String[] {"--startPort=8000", "--endPort=8080"});
        Thread.sleep(50L);

        // Verify open ports are found via the factory API
        Map<String, List<Integer>> openPorts = PortScannerFactory.getInstance().scanOpenPorts("127.0.0.1", 8000, 8080, 20, 200);
        assertNotNull(openPorts);
        assertFalse(openPorts.isEmpty());
        List<Integer> ports = openPorts.get("127.0.0.1");
        assertNotNull(ports);
        assertTrue(ports.contains(8010), "Port 8010 should be open");
        assertTrue(ports.contains(8031), "Port 8031 should be open");

        server1.stop();
        server2.stop();
    }

    
    /**
     * Test scanClosedPorts — verify that closed ports are detected.
     *
     * @throws InterruptedException In case of an interrupt
     * @throws IOException In case of an I/O error
     */
    @Test
    public void scanClosedPortsTest() throws InterruptedException, IOException {
        redirectJavaLoggingToSlf4j();

        // Start a server on port 8040 only
        IHttpServer server = HttpServerFactory.getInstance().getServerInstance();
        server.start(new EchoService(), 8040);
        Thread.sleep(50L);

        try {
            // Scan range 8039-8041: port 8040 is open, 8039 and 8041 should be closed
            Map<String, List<Integer>> closedPorts = PortScannerFactory.getInstance().scanClosedPorts("127.0.0.1", 8039, 8041, 5, 200);
            assertNotNull(closedPorts);
            List<Integer> ports = closedPorts.get("127.0.0.1");
            assertNotNull(ports);
            assertTrue(ports.contains(8039), "Port 8039 should be closed");
            assertTrue(ports.contains(8041), "Port 8041 should be closed");
            assertFalse(ports.contains(8040), "Port 8040 should NOT be in closed list");
        } finally {
            server.stop();
        }
    }


    /**
     * Redirect java logging
     */
    private void redirectJavaLoggingToSlf4j() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        Logger.getLogger("").setLevel(Level.FINEST); // Root logger, for example.
    }
}
