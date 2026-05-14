/*
 * ProxyDetector.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.proxy;

import com.github.toolarium.network.proxy.dto.IProxyInfo;
import com.github.toolarium.network.proxy.dto.ProxyInfo;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Detects system proxy settings using {@link java.net.ProxySelector}.
 *
 * @author patrick
 */
public final class ProxyDetector {
    private static final Logger LOG = LoggerFactory.getLogger(ProxyDetector.class);

    private static final class HOLDER {
        static final ProxyDetector INSTANCE = new ProxyDetector();
    }

    /**
     * Constructor.
     */
    private ProxyDetector() {
    }

    /**
     * Get the instance.
     *
     * @return the instance
     */
    public static ProxyDetector getInstance() {
        return HOLDER.INSTANCE;
    }


    /**
     * Detect proxies configured for a given URL.
     *
     * @param url the target URL (e.g. "http://example.com")
     * @return the list of detected proxies
     */
    public List<IProxyInfo> detectProxies(String url) {
        if (url == null || url.trim().isEmpty()) {
            return Collections.singletonList(new ProxyInfo("DIRECT", null, -1));
        }

        try {
            URI uri = URI.create(url.trim());
            ProxySelector selector = ProxySelector.getDefault();
            if (selector == null) {
                return Collections.singletonList(new ProxyInfo("DIRECT", null, -1));
            }

            List<Proxy> proxies = selector.select(uri);
            List<IProxyInfo> result = new ArrayList<>();

            for (Proxy proxy : proxies) {
                if (proxy.type() == Proxy.Type.DIRECT) {
                    result.add(new ProxyInfo("DIRECT", null, -1));
                } else {
                    InetSocketAddress addr = (InetSocketAddress) proxy.address();
                    String type;
                    if (proxy.type() == Proxy.Type.HTTP) {
                        type = "HTTP";
                    } else {
                        type = "SOCKS";
                    }
                    String host;
                    int port;
                    if (addr != null) {
                        host = addr.getHostString();
                        port = addr.getPort();
                    } else {
                        host = null;
                        port = -1;
                    }
                    result.add(new ProxyInfo(type, host, port));
                }
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Proxies for " + url + ": " + result);
            }
            return result;

        } catch (Exception e) {
            LOG.debug("Proxy detection failed for " + url + ": " + e.getMessage());
            return Collections.singletonList(new ProxyInfo("DIRECT", null, -1));
        }
    }


    /**
     * Detect proxies for HTTP traffic.
     *
     * @return the list of proxies for http://example.com
     */
    public List<IProxyInfo> detectHttpProxies() {
        return detectProxies("http://example.com");
    }


    /**
     * Detect proxies for HTTPS traffic.
     *
     * @return the list of proxies for https://example.com
     */
    public List<IProxyInfo> detectHttpsProxies() {
        return detectProxies("https://example.com");
    }


    /**
     * Check if a proxy is configured for the given URL.
     *
     * @param url the URL
     * @return true if a non-direct proxy is configured
     */
    public boolean hasProxy(String url) {
        List<IProxyInfo> proxies = detectProxies(url);
        for (IProxyInfo proxy : proxies) {
            if (!proxy.isDirect()) {
                return true;
            }
        }
        return false;
    }
}
