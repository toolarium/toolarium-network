/*
 * CIRDRUtil.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.ip;

import com.github.toolarium.network.ip.dto.CIDRInfo;
import com.github.toolarium.network.ip.formatter.IPV6Formatter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;


/**
 * CIDR utility
 * 
 * @author patrick
 */
public final class CIDRUtil {

    /** Regular expression: ipv4 address range pattern */
    public static final  String IPV4_RANGE_EXPRESSION = "^([0-9]{1,3}\\.){3}[0-9]{1,3}" + "/([0-9]|[1-2][0-9]|3[0-2])$";

    /** Regular expression: ipv6 address range pattern */
    public static final  String IPV6_RANGE_EXPRESSION = "^s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}"
                                                        + "(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}"
                                                        + "(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}"
                                                        + "(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}"
                                                        + "(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}"
                                                        + "(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}"
                                                        + "(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3}))|:))|"
                                                        + "(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3}))|:)))(%.+)?s*"
                                                        + "/([0-9]|[1-9][0-9]|1[0-1][0-9]|12[0-8])$";

    private Pattern ipv4RangeExpression;
    private Pattern ipv6RangeExpression;


    /**
     * Private class, the only instance of the singleton which will be created by accessing the holder class.
     *
     * @author patrick
     */
    private static class HOLDER {
        static final CIDRUtil INSTANCE = new CIDRUtil();
    }

    
    /**
     * Constructor
     */
    private CIDRUtil() {
        ipv4RangeExpression = Pattern.compile(IPV4_RANGE_EXPRESSION);
        ipv6RangeExpression = Pattern.compile(IPV6_RANGE_EXPRESSION);
    }


    /**
     * Get the instance
     *
     * @return the instance
     */
    public static CIDRUtil getInstance() {
        return HOLDER.INSTANCE;
    }

    
    /**
     * Validate if the given address is an instance of ipv4 or ipv6 address
     *
     * @param address the address
     * @return true if it is valid otherwise false
     */
    public boolean isValidAddressRange(String address) {
        if (!isValidRange(address)) {
            return false;
        }

        // try to resolve address
        InetAddress ipAddress = IPUtil.getInstance().parse(address);
        if (ipAddress != null) {
            String a = ipAddress.getHostAddress();
            if (a != null && (isIPv4Range(a) || isIPv6Range(a))) {
                return true;
            }
        }

        return false;
    }

    
    /**
     * Check if the address string is a valid IP address (cidr notation)
     *
     * @param ipRange the ip range
     * @return true if it is
     */
    public boolean isValidRange(String ipRange) {
        if (ipRange == null || ipRange.trim().length() == 0) {
            return false;
        }

        final String a = ipRange.trim();
        return isIPv4Range(a) || isIPv6Range(a);
    }


    /**
     * Check if the address string is instance of IPv4 range (cidr notation)
     *
     * @param ipRange the ip range
     * @return true if it is
     */
    public boolean isIPv4Range(String ipRange) {
        if (ipRange == null || ipRange.trim().length() == 0) {
            return false;
        }

        return ipv4RangeExpression.matcher(ipRange).matches();
    }


    /**
     * Check if the address string is instance of IPv6 range (cidr notation)
     *
     * @param ipRange the ip range
     * @return true if it is
     */
    public boolean isIPv6Range(String ipRange) {
        if (ipRange == null || ipRange.trim().length() == 0) {
            return false;
        }

        return ipv6RangeExpression.matcher(ipRange).matches();
    }

    
    /**
     * Check if the address is an ip address or an ip address range. IPv4 and IPv6 are supported.
     *
     * @param ipRange the ip range
     * @param address the address to check
     * @return true if the address name is belong to given range
     */
    public boolean isInRange(String ipRange, String address) {
        // check address
        if (!IPUtil.getInstance().isValidAddress(address)) {
            //log.debug("Invalid address [" + address + "]!");
            return false;
        }

        InetAddress inetAdress = IPUtil.getInstance().parse(address);
        if (inetAdress == null) {
            //log.debug("Could not resolve address [" + address + "]!");
            return false;
        }

        String remoteAddress = inetAdress.getHostAddress();
        if (remoteAddress == null || !IPUtil.getInstance().isValidAddress(remoteAddress)) {
            //log.debug("Invalid remote address [" + address + "]!");
            return false;
        }

        if (!remoteAddress.equals(address)) {
            //log.debug("Resolved address [" + address + "] as [" + remoteAddress + "].");
        }

        String ipRangeAddress = ipRange.trim();
        if (ipRange.equals(address) || ipRange.equals(remoteAddress)) {
            if (ipRange.equals(address)) {
                //log.debug("Address [" + address + "] and address range [" + addressRange + "] are equals.");
            } else if (ipRange.equals(remoteAddress)) {
                //log.debug("Remote address [" + remoteAddress + "] and address range [" + addressRange + "] are equals.");
            }

            return true;
        }

        if (!isValidRange(ipRangeAddress)) {
            // it could be a hostname
            inetAdress = IPUtil.getInstance().parse(ipRangeAddress);
            if (inetAdress == null) {
                //log.debug("Invalid address range [" + addressRangeAddress + "]!");
                return false;
            }

            ipRangeAddress = inetAdress.getHostAddress();
            if (ipRangeAddress == null || !IPUtil.getInstance().isValidAddress(ipRangeAddress)) {
                //log.debug("Invalid range address [" + addressRangeAddress + "]!");
                return false;
            }

            //log.debug("Address [" + remoteAddress + "] and address range [" + addressRangeAddress + "] are equals.");
            return ipRangeAddress.equals(remoteAddress);
        }

        boolean result = false;
        try {
            CIDRInfo cidr = parse(ipRangeAddress);
            result = cidr.isInRange(remoteAddress) || cidr.getEndAddressString().equals(remoteAddress) || cidr.getStartAddressString().equals(remoteAddress);
        } catch (Exception e) {
            //log.error("Error occured while check address [" + remoteAddress + "] in range [" + addressRangeAddress + "]: " + e.getMessage(), e);
        }

        //log.debug("Check remote address [" + remoteAddress + "] in address range [" + addressRangeAddress + "]: " + result);
        return result;
    }

    
    /**
     * Get all addresses of a subnet
     *
     * @param addressRange the address range
     * @return all addresses of the subnet
     * @throws UnknownHostException In case of invalid address
     */
    public List<String> getAllAddresses(String addressRange) throws UnknownHostException {
        if (IPUtil.getInstance().isValidAddress(addressRange)) {
            return Arrays.asList(addressRange);
        }

        if (isValidRange(addressRange)) {
            final CIDRInfo cidrInfo = parse(addressRange);
            final List<String> addrList = new ArrayList<>();
            if (cidrInfo.getStartIp().compareTo(cidrInfo.getEndIp()) <= 0) {
                for (BigInteger d = cidrInfo.getStartIp(); d.compareTo(cidrInfo.getEndIp()) <= 0; d = d.add(BigInteger.ONE)) {
                    try {
                        final InetAddress addr = cidrInfo.resolveAddress(d);
                        final String ipv6Addr = shortIpv6Address(addr.getHostAddress()); 
                        addrList.add(ipv6Addr);
                        //if (LOG.isDebugEnabled()) {
                        //    LOG.debug(" - " + addr.getHostAddress() + " -> " + ipv6Addr + " -> "+ cidrInfo.toBinaryString(addr));
                        //}
                    } catch (UnknownHostException e) {
                        Logger logger = Logger.getLogger(CIDRUtil.class.getName());
                        logger.fine("Could not resolve address: " + d);
                    }
                    
                }
            }

            return addrList;
        }

        return Collections.emptyList(); 
    }

    
    /**
     * Parse CIDR expression
     *
     * @param cidrExpression the CIDR expression
     * @return the processed CIDR
     * @throws UnknownHostException In case of invalid address
     */
    public CIDRInfo parse(String cidrExpression) throws UnknownHostException {
        if (cidrExpression == null || cidrExpression.trim().isEmpty() || !cidrExpression.contains("/")) {
            throw new UnknownHostException("Invalid CIDR format: [" + cidrExpression + "]!");
        }

        final String cidr = cidrExpression.trim();
        int index = cidr.indexOf("/");
        String address = cidr.substring(0, index).trim();
        String networkStr = cidr.substring(index + 1).trim();
        
        final int network;
        try {
            network = Integer.parseInt(networkStr);
        } catch (NumberFormatException ex) {
            throw new UnknownHostException("Invalid network: [" + networkStr + "]!");
        }
        
        return new CIDRInfo(address, network);
    }

    
    /**
     * Short ipv6 addresses:
     *
     * @param ipV6Address the address to short
     * @return The shorten ipv6 address
     */
    private String shortIpv6Address(String ipV6Address) {
        if (!IPUtil.getInstance().isIPv6Address(ipV6Address)) {
            return ipV6Address;
        }

        return new IPV6Formatter().format(ipV6Address);
    }
}
