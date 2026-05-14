/*
 * SslCertificateInfo.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.ssl.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * Implements the {@link ISslCertificateInfo}.
 *
 * @author patrick
 */
public class SslCertificateInfo implements ISslCertificateInfo, Serializable {
    private static final long serialVersionUID = 1L;
    private String host;
    private int port;
    private String subjectDN;
    private String issuerDN;
    private String serialNumber;
    private Date notBefore;
    private Date notAfter;
    private List<String> subjectAlternativeNames;
    private String protocol;
    private String cipherSuite;
    private int chainLength;
    private boolean success;
    private long duration;
    private Exception exception;


    /**
     * Constructor for successful result.
     *
     * @param host the host
     * @param port the port
     * @param certIdentity the certificate identity information
     * @param sans the subject alternative names
     * @param tlsSession the TLS session information
     * @param duration the query duration
     */
    public SslCertificateInfo(String host, int port, CertIdentity certIdentity, List<String> sans, TlsSession tlsSession, long duration) {
        this.host = host;
        this.port = port;
        this.subjectDN = certIdentity.subjectDN;
        this.issuerDN = certIdentity.issuerDN;
        this.serialNumber = certIdentity.serialNumber;
        this.notBefore = certIdentity.notBefore;
        this.notAfter = certIdentity.notAfter;
        if (sans != null) {
            this.subjectAlternativeNames = new ArrayList<>(sans);
        } else {
            this.subjectAlternativeNames = Collections.emptyList();
        }
        this.protocol = tlsSession.protocol;
        this.cipherSuite = tlsSession.cipherSuite;
        this.chainLength = tlsSession.chainLength;
        this.success = true;
        this.duration = duration;
        this.exception = null;
    }


    /**
     * Constructor for failed result.
     *
     * @param host the host
     * @param port the port
     * @param duration the duration
     * @param exception the exception
     */
    public SslCertificateInfo(String host, int port, long duration, Exception exception) {
        this.host = host;
        this.port = port;
        this.success = false;
        this.duration = duration;
        this.exception = exception;
        this.subjectAlternativeNames = Collections.emptyList();
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String getSubjectDN() {
        return subjectDN;
    }

    @Override
    public String getIssuerDN() {
        return issuerDN;
    }

    @Override
    public String getSerialNumber() {
        return serialNumber;
    }

    @Override
    public Date getNotBefore() {
        return notBefore;
    }

    @Override
    public Date getNotAfter() {
        return notAfter;
    }

    @Override
    public List<String> getSubjectAlternativeNames() {
        return Collections.unmodifiableList(subjectAlternativeNames);
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    @Override
    public String getCipherSuite() {
        return cipherSuite;
    }

    @Override
    public int getChainLength() {
        return chainLength;
    }

    @Override
    public boolean isValid() {
        if (notBefore == null || notAfter == null) {
            return false;
        }
        Date now = new Date();
        return now.after(notBefore) && now.before(notAfter);
    }

    @Override
    public long getDaysUntilExpiry() {
        if (notAfter == null) {
            return -1;
        }
        long diff = notAfter.getTime() - System.currentTimeMillis();
        return TimeUnit.MILLISECONDS.toDays(diff);
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public Exception getException() {
        return exception;
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, subjectDN, serialNumber, success);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SslCertificateInfo other = (SslCertificateInfo) obj;
        return Objects.equals(host, other.host) && port == other.port
                && Objects.equals(subjectDN, other.subjectDN)
                && Objects.equals(serialNumber, other.serialNumber) && success == other.success;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("SslCertificateInfo [").append(host).append(":").append(port);
        if (success) {
            sb.append(", subject=").append(subjectDN);
            sb.append(", issuer=").append(issuerDN);
            sb.append(", valid=").append(isValid());
            sb.append(", expires in ").append(getDaysUntilExpiry()).append(" days");
            sb.append(", protocol=").append(protocol);
            sb.append(" in ").append(duration).append("ms");
        } else {
            sb.append(" FAILED");
            if (exception != null) {
                sb.append(", error=").append(exception.getMessage());
            }
        }
        return sb.append("]").toString();
    }


    /**
     * Groups certificate identity fields.
     */
    public static class CertIdentity {
        private final String subjectDN;
        private final String issuerDN;
        private final String serialNumber;
        private final Date notBefore;
        private final Date notAfter;

        /**
         * Constructor.
         *
         * @param subjectDN the subject DN
         * @param issuerDN the issuer DN
         * @param serialNumber the serial number
         * @param notBefore the not-before date
         * @param notAfter the not-after date
         */
        public CertIdentity(String subjectDN, String issuerDN, String serialNumber,
                             Date notBefore, Date notAfter) {
            this.subjectDN = subjectDN;
            this.issuerDN = issuerDN;
            this.serialNumber = serialNumber;
            this.notBefore = notBefore;
            this.notAfter = notAfter;
        }
    }


    /**
     * Groups TLS session fields.
     */
    public static class TlsSession {
        private final String protocol;
        private final String cipherSuite;
        private final int chainLength;

        /**
         * Constructor.
         *
         * @param protocol the TLS protocol
         * @param cipherSuite the cipher suite
         * @param chainLength the chain length
         */
        public TlsSession(String protocol, String cipherSuite, int chainLength) {
            this.protocol = protocol;
            this.cipherSuite = cipherSuite;
            this.chainLength = chainLength;
        }
    }
}
