/*
 * ISslCertificateInfo.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.ssl.dto;

import java.util.Date;
import java.util.List;


/**
 * Defines information about an SSL/TLS certificate.
 *
 * @author patrick
 */
public interface ISslCertificateInfo {

    /**
     * Get the queried host.
     *
     * @return the host
     */
    String getHost();


    /**
     * Get the queried port.
     *
     * @return the port
     */
    int getPort();


    /**
     * Get the subject DN (Distinguished Name).
     *
     * @return the subject DN
     */
    String getSubjectDN();


    /**
     * Get the issuer DN.
     *
     * @return the issuer DN
     */
    String getIssuerDN();


    /**
     * Get the serial number.
     *
     * @return the serial number as hex string
     */
    String getSerialNumber();


    /**
     * Get the certificate validity start date.
     *
     * @return the not-before date
     */
    Date getNotBefore();


    /**
     * Get the certificate validity end date.
     *
     * @return the not-after date
     */
    Date getNotAfter();


    /**
     * Get the Subject Alternative Names (SANs).
     *
     * @return the list of SANs
     */
    List<String> getSubjectAlternativeNames();


    /**
     * Get the TLS protocol version used.
     *
     * @return the protocol (e.g. "TLSv1.3")
     */
    String getProtocol();


    /**
     * Get the cipher suite used.
     *
     * @return the cipher suite
     */
    String getCipherSuite();


    /**
     * Get the number of certificates in the chain.
     *
     * @return the chain length
     */
    int getChainLength();


    /**
     * Check if the certificate is currently valid (not expired).
     *
     * @return true if valid
     */
    boolean isValid();


    /**
     * Get the number of days until expiry.
     *
     * @return days until expiry (negative if expired)
     */
    long getDaysUntilExpiry();


    /**
     * Check if the query was successful.
     *
     * @return true if successful
     */
    boolean isSuccess();


    /**
     * Get the query duration in milliseconds.
     *
     * @return the duration
     */
    long getDuration();


    /**
     * Get the exception if the query failed.
     *
     * @return the exception or null
     */
    Exception getException();
}
