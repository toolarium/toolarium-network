/*
 * PortScanClientTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.scanner;

import com.github.toolarium.network.server.HttpServerFactory;
import com.github.toolarium.network.server.IHttpServer;
import com.github.toolarium.network.server.service.EchoService;
import java.io.IOException;
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
        
        server1.stop();
        server2.stop();
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
