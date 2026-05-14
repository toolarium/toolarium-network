/*
 * SslCertificateInspectorFactory.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.ssl;

import com.github.toolarium.network.ssl.dto.ISslCertificateInfo;
import com.github.toolarium.network.ssl.impl.SslCertificateInspectorImpl;


/**
 * Factory for creating SSL certificate inspector instances.
 *
 * @author patrick
 */
public final class SslCertificateInspectorFactory {
    /**
     * Default timeout: 5 seconds.
     */
    static final int DEFAULT_TIMEOUT = 5000;

    private static final class HOLDER {
        static final SslCertificateInspectorFactory INSTANCE = new SslCertificateInspectorFactory();
    }

    /**
     * Constructor.
     */
    private SslCertificateInspectorFactory() {
    }

    /**
     * Get the instance.
     *
     * @return the instance
     */
    public static SslCertificateInspectorFactory getInstance() {
        return HOLDER.INSTANCE;
    }

    /**
     * Get an SSL certificate inspector with the specified timeout.
     *
     * @param timeout the timeout in milliseconds
     * @return the inspector instance
     */
    public ISslCertificateInspector getInspector(int timeout) {
        return new SslCertificateInspectorImpl(timeout);
    }

    /**
     * Get an SSL certificate inspector with the default timeout (5 seconds).
     *
     * @return the inspector instance
     */
    public ISslCertificateInspector getInspector() {
        return new SslCertificateInspectorImpl(DEFAULT_TIMEOUT);
    }

    /**
     * Convenience: inspect a host on the given port.
     *
     * @param host the hostname
     * @param port the port
     * @return the certificate info
     */
    public ISslCertificateInfo inspect(String host, int port) {
        return getInspector().inspect(host, port);
    }

    /**
     * Convenience: inspect a host on the default HTTPS port (443).
     *
     * @param host the hostname
     * @return the certificate info
     */
    public ISslCertificateInfo inspect(String host) {
        return getInspector().inspect(host);
    }
}
