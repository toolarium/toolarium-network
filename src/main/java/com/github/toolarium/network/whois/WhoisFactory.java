/*
 * WhoisFactory.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.whois;

import com.github.toolarium.network.whois.dto.IWhoisResult;
import com.github.toolarium.network.whois.impl.WhoisImpl;


/**
 * Factory for creating WHOIS lookup instances.
 *
 * @author patrick
 */
public final class WhoisFactory {
    /**
     * Default timeout: 10 seconds.
     */
    static final int DEFAULT_TIMEOUT = 10000;

    private static final class HOLDER {
        static final WhoisFactory INSTANCE = new WhoisFactory();
    }

    /**
     * Constructor.
     */
    private WhoisFactory() {
    }

    /**
     * Get the instance.
     *
     * @return the instance
     */
    public static WhoisFactory getInstance() {
        return HOLDER.INSTANCE;
    }

    /**
     * Get a WHOIS instance with the specified timeout.
     *
     * @param timeout the timeout in milliseconds
     * @return the whois instance
     */
    public IWhois getWhois(int timeout) {
        return new WhoisImpl(timeout);
    }

    /**
     * Get a WHOIS instance with the default timeout (10 seconds).
     *
     * @return the whois instance
     */
    public IWhois getWhois() {
        return new WhoisImpl(DEFAULT_TIMEOUT);
    }

    /**
     * Convenience: query a domain or IP with default settings.
     *
     * @param query the domain or IP address
     * @return the whois result
     */
    public IWhoisResult query(String query) {
        return getWhois().query(query);
    }
}
