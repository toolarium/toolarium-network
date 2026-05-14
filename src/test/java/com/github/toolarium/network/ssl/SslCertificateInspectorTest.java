/*
 * SslCertificateInspectorTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.ssl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toolarium.network.server.HttpServerFactory;
import com.github.toolarium.network.server.IHttpServer;
import com.github.toolarium.network.server.service.EchoService;
import com.github.toolarium.network.ssl.dto.ISslCertificateInfo;
import com.github.toolarium.network.ssl.dto.SslCertificateInfo;
import com.github.toolarium.security.keystore.SecurityManagerProviderFactory;
import com.github.toolarium.security.ssl.SSLContextFactory;
import java.util.Arrays;
import java.util.Date;
import javax.net.ssl.SSLContext;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for SSL certificate inspector.
 *
 * @author patrick
 */
public class SslCertificateInspectorTest {
    private static final Logger LOG = LoggerFactory.getLogger(SslCertificateInspectorTest.class);

    /**
     * Test inspect local SSL server.
     *
     * @throws Exception In case of an error
     */
    @Test
    public void inspectLocalSslServerTest() throws Exception {
        int port = 9200;
        SSLContext sslContext = SSLContextFactory.getInstance().createSslContext(
                SecurityManagerProviderFactory.getInstance().getSecurityManagerProvider("toolarium", "changit"));

        IHttpServer server = HttpServerFactory.getInstance().getServerInstance();
        server.start(new EchoService(), port, sslContext);
        Thread.sleep(100L);

        try {
            ISslCertificateInfo result = SslCertificateInspectorFactory.getInstance().inspect("localhost", port);
            assertNotNull(result);
            assertEquals("localhost", result.getHost());
            assertEquals(port, result.getPort());
            assertTrue(result.isSuccess());
            assertNotNull(result.getSubjectDN());
            assertNotNull(result.getIssuerDN());
            assertNotNull(result.getSerialNumber());
            assertNotNull(result.getNotBefore());
            assertNotNull(result.getNotAfter());
            assertNotNull(result.getProtocol());
            assertNotNull(result.getCipherSuite());
            assertTrue(result.getChainLength() > 0);
            assertTrue(result.getDuration() >= 0);
            LOG.info("SSL inspect: " + result);
        } finally {
            server.stop();
        }
    }

    /**
     * Test inspect with null host.
     */
    @Test
    public void inspectNullHostTest() {
        ISslCertificateInfo result = SslCertificateInspectorFactory.getInstance().inspect(null, 443);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNotNull(result.getException());
    }

    /**
     * Test inspect with unreachable host.
     */
    @Test
    public void inspectUnreachableTest() {
        ISslCertificateInfo result = SslCertificateInspectorFactory.getInstance()
                .getInspector(1000).inspect("localhost", 19999);
        assertNotNull(result);
        assertFalse(result.isSuccess());
    }

    /**
     * Test inspect multiple with empty input.
     */
    @Test
    public void inspectMultipleEmptyTest() {
        ISslCertificateInspector inspector = SslCertificateInspectorFactory.getInstance().getInspector();
        assertTrue(inspector.inspect(443).isEmpty());
    }

    /**
     * Test SslCertificateInfo DTO equals, hashCode, toString.
     */
    @Test
    public void sslCertificateInfoDtoTest() {
        Date notBefore = new Date(System.currentTimeMillis() - 86400000L); // yesterday
        Date notAfter = new Date(System.currentTimeMillis() + 86400000L * 30); // 30 days from now

        SslCertificateInfo.CertIdentity certId = new SslCertificateInfo.CertIdentity(
                "CN=host1", "CN=CA", "abc123", notBefore, notAfter);
        SslCertificateInfo.TlsSession tlsSession = new SslCertificateInfo.TlsSession(
                "TLSv1.3", "TLS_AES_256", 2);
        SslCertificateInfo i1 = new SslCertificateInfo("host1", 443, certId,
                Arrays.asList("host1", "www.host1"), tlsSession, 50);
        SslCertificateInfo i2 = new SslCertificateInfo("host1", 443, certId,
                Arrays.asList("host1", "www.host1"), tlsSession, 50);
        SslCertificateInfo i3 = new SslCertificateInfo("host2", 443, 0, new Exception("fail"));

        assertEquals(i1, i2);
        assertEquals(i1.hashCode(), i2.hashCode());
        assertFalse(i1.equals(i3));
        assertFalse(i1.equals(null));
        assertFalse(i1.equals("string"));

        assertTrue(i1.isValid());
        assertTrue(i1.isSuccess());
        assertTrue(i1.getDaysUntilExpiry() > 0);
        assertEquals(2, i1.getSubjectAlternativeNames().size());
        assertNull(i1.getException());

        assertFalse(i3.isSuccess());
        assertFalse(i3.isValid());
        assertEquals(-1, i3.getDaysUntilExpiry());
        assertTrue(i3.getSubjectAlternativeNames().isEmpty());

        assertTrue(i1.toString().contains("CN=host1"));
        assertTrue(i1.toString().contains("TLSv1.3"));
        assertTrue(i3.toString().contains("FAILED"));
    }
}
