/*
 * ISslCertificateInspector.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.ssl;

import com.github.toolarium.network.ssl.dto.ISslCertificateInfo;
import java.util.List;


/**
 * Defines the SSL/TLS certificate inspection interface.
 *
 * @author patrick
 */
public interface ISslCertificateInspector {

    /**
     * Inspect the SSL/TLS certificate of a remote server.
     *
     * @param host the hostname
     * @param port the HTTPS port
     * @return the certificate info
     */
    ISslCertificateInfo inspect(String host, int port);


    /**
     * Inspect the SSL/TLS certificate on the default HTTPS port (443).
     *
     * @param host the hostname
     * @return the certificate info
     */
    ISslCertificateInfo inspect(String host);


    /**
     * Inspect multiple hosts in parallel.
     *
     * @param port the port
     * @param hosts the hostnames
     * @return the list of certificate info
     */
    List<ISslCertificateInfo> inspect(int port, String... hosts);
}
