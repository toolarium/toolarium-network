/*
 * SslCertificateInspectorImpl.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.ssl.impl;

import com.github.toolarium.network.ssl.ISslCertificateInspector;
import com.github.toolarium.network.ssl.dto.ISslCertificateInfo;
import com.github.toolarium.network.ssl.dto.SslCertificateInfo;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implements SSL certificate inspection by connecting to the remote host
 * and examining the peer certificate chain.
 *
 * @author patrick
 */
public class SslCertificateInspectorImpl implements ISslCertificateInspector {
    private static final Logger LOG = LoggerFactory.getLogger(SslCertificateInspectorImpl.class);
    private static final int DEFAULT_PORT = 443;
    private final int timeout;


    /**
     * Constructor for SslCertificateInspectorImpl
     *
     * @param timeout the connection timeout in milliseconds
     */
    public SslCertificateInspectorImpl(int timeout) {
        this.timeout = timeout;
    }


    /**
     * @see com.github.toolarium.network.ssl.ISslCertificateInspector#inspect(java.lang.String, int)
     */
    @Override
    public ISslCertificateInfo inspect(String host, int port) {
        if (host == null || host.trim().isEmpty()) {
            return new SslCertificateInfo(host, port, 0, new IllegalArgumentException("Host is null or empty"));
        }

        String h = host.trim();
        long start = System.currentTimeMillis();

        SSLSocket socket = null;
        try {
            SSLSocketFactory factory = createTrustAllFactory();
            socket = (SSLSocket) factory.createSocket();
            socket.setSoTimeout(timeout);
            socket.connect(new java.net.InetSocketAddress(h, port), timeout);
            socket.startHandshake();

            SSLSession session = socket.getSession();
            Certificate[] certs = session.getPeerCertificates();

            if (certs.length == 0 || !(certs[0] instanceof X509Certificate)) {
                long duration = System.currentTimeMillis() - start;
                return new SslCertificateInfo(h, port, duration, new Exception("No X509 certificate found"));
            }

            X509Certificate cert = (X509Certificate) certs[0];
            List<String> sans = extractSANs(cert);
            long duration = System.currentTimeMillis() - start;

            SslCertificateInfo.CertIdentity certIdentity = new SslCertificateInfo.CertIdentity(
                    cert.getSubjectX500Principal().getName(),
                    cert.getIssuerX500Principal().getName(),
                    cert.getSerialNumber().toString(16),
                    cert.getNotBefore(),
                    cert.getNotAfter());
            SslCertificateInfo.TlsSession tlsSession = new SslCertificateInfo.TlsSession(
                    session.getProtocol(),
                    session.getCipherSuite(),
                    certs.length);
            SslCertificateInfo result = new SslCertificateInfo(h, port, certIdentity, sans, tlsSession, duration);

            if (LOG.isDebugEnabled()) {
                LOG.debug("SSL inspect: " + result);
            }
            return result;

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            LOG.debug("SSL inspect failed for " + h + ":" + port + ": " + e.getMessage());
            return new SslCertificateInfo(h, port, duration, e);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (Exception e) {
                    // NOP
                }
            }
        }
    }


    /**
     * @see com.github.toolarium.network.ssl.ISslCertificateInspector#inspect(java.lang.String)
     */
    @Override
    public ISslCertificateInfo inspect(String host) {
        return inspect(host, DEFAULT_PORT);
    }


    /**
     * @see com.github.toolarium.network.ssl.ISslCertificateInspector#inspect(int, java.lang.String[])
     */
    @Override
    public List<ISslCertificateInfo> inspect(int port, String... hosts) {
        if (hosts == null || hosts.length == 0) {
            return Collections.emptyList();
        }
        List<ISslCertificateInfo> results = new ArrayList<>();
        for (String host : hosts) {
            results.add(inspect(host, port));
        }
        return results;
    }


    /**
     * Extract Subject Alternative Names from an X509 certificate.
     *
     * @param cert the certificate
     * @return the list of SANs
     */
    private List<String> extractSANs(X509Certificate cert) {
        List<String> sans = new ArrayList<>();
        try {
            Collection<List<?>> sanCollection = cert.getSubjectAlternativeNames();
            if (sanCollection != null) {
                for (List<?> san : sanCollection) {
                    if (san.size() >= 2) {
                        Object value = san.get(1);
                        if (value instanceof String) {
                            sans.add((String) value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.debug("Could not extract SANs: " + e.getMessage());
        }
        return sans;
    }


    /**
     * Create an SSLSocketFactory that trusts all certificates (for inspection purposes).
     *
     * @return the factory
     * @throws Exception In case of an error
     */
    private SSLSocketFactory createTrustAllFactory() throws Exception {
        TrustManager[] trustAll = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    // trust all for inspection
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    // trust all for inspection
                }
            }
        };

        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, trustAll, new java.security.SecureRandom());
        return ctx.getSocketFactory();
    }
}
