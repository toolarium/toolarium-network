/*
 * IWhois.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.whois;

import com.github.toolarium.network.whois.dto.IWhoisResult;


/**
 * Defines the WHOIS lookup interface for querying domain/IP registration info.
 *
 * @author patrick
 */
public interface IWhois {

    /**
     * Perform a WHOIS query for a domain or IP address.
     *
     * @param query the domain or IP address
     * @return the whois result
     */
    IWhoisResult query(String query);


    /**
     * Perform a WHOIS query against a specific WHOIS server.
     *
     * @param query the domain or IP address
     * @param whoisServer the WHOIS server hostname
     * @return the whois result
     */
    IWhoisResult query(String query, String whoisServer);
}
