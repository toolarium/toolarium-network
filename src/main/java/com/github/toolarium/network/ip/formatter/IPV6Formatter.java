/*
 * CIDRFormatter.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.ip.formatter;

import com.github.toolarium.network.ip.IPUtil;
import java.util.LinkedList;


/**
 * IPv6 address formatter
 * 
 * @author patrick
 */
public class IPV6Formatter {
    private static final String IPV6_SEPARATOR = ":";


    /**
     * Format IPv6 address.
     * 
     * </p> 
     * Short ipv6 addresses:
     * <li>Rule 1: When there are continuous zeros (0s) in the IPv6 address notation, they are replaced with ::. This rule is also known as zero compression.
     * For example, original IPv6 = ef82:0000:0000:0000:0000:1a12:1234:1b12, compressed IPv6 = ef82::1a12:1234:1b12
     * <li>Rule 2: Leading zeros (0s) in the 16 bits field can be removed. But each block in which you do this, have at least one number remaining. If the field contains all zeros (0s), you have to leave one zero (0) remaining.
     * Removing leading zeros (0s) does not have any effect on the value. However, you cannot apply this rule to trailing zeros (0s). This rule is also known as leading zero compression.
     * For example, original IPv6 = 1234:0fd2:5621:0001:0089:0000:0000:4500, compressed IPv6 = 1234:fd2:5621:1:89:0:0:4500
     * <li>Rule 3: When zeros (0s) are present in discontinuous pattern in IPv6 address notation, then at only one junction, the zeros (0s) are replaced with ::.
     * For example, original IPv6 = 2001:1234:0000:0000:1b12:0000:0000:1a13, ompressed IPv6 = 2001:1234::1b12:0:0:1a13 or IPv6 = 2001:1234:0:0:1b12::1a13
     * @see <a href="https://iplocation.io/">iplocation.io/</a>
     * @param ipV6Address the address to short
     * @return The shorten ipv6 address
     */
    public String format(String ipV6Address) {
        if (!IPUtil.getInstance().isIPv6Address(ipV6Address)) {
            return ipV6Address;
        }
        
        LinkedList<int[]> junctionZeroMap = new LinkedList<>();
        return combineElements(parseAndSplit(ipV6Address.trim(), junctionZeroMap), 
                                             seachBiggestJunctionZeroRecord(junctionZeroMap));
    }


    /**
     * Parse and split ipv6 address
     * 
     * @param ipV6Address the address to parse and split
     * @param junctionZeroMap the junction zero map
     * @return the splitted adddress
     */
    protected String[] parseAndSplit(String ipV6Address, LinkedList<int[]> junctionZeroMap) {
        String[] ipv6Split;
        int idx = ipV6Address.indexOf("::");
        if (idx >= 0) {
            ipv6Split = new String[8];
            String[] headSplit = ipV6Address.substring(0, idx).split(IPV6_SEPARATOR);
            if (headSplit != null && headSplit.length > 0) {
                for (int i = 0; i < headSplit.length; i++) {
                    ipv6Split[i] = headSplit[i];
                }
            }
            String[] tailSplit = ipV6Address.substring(idx + 2).split(IPV6_SEPARATOR);
            int tailIdx = tailSplit.length - 1;
            if (tailSplit != null && tailSplit.length > 0) {
                for (int i = ipv6Split.length - 1; i >= 0 && tailIdx >= 0; i--) {
                    ipv6Split[i] = tailSplit[tailIdx--];
                }
            }
        } else {
            ipv6Split = ipV6Address.split(IPV6_SEPARATOR);
        }

        for (int i = 0; i < ipv6Split.length; i++) {
            String value;
            if (ipv6Split[i] == null) {
                value = "";
            } else {
                value = ipv6Split[i].trim();
            }
            
            if (value.isEmpty() || value.trim().equals("0") || value.trim().equals("0000")) {
                // rule 1
                ipv6Split[i] = "";
                if (!junctionZeroMap.isEmpty() && (junctionZeroMap.getLast()[0] + junctionZeroMap.getLast()[1]) == i) {
                    junctionZeroMap.getLast()[1] = junctionZeroMap.getLast()[1] + 1;
                } else {
                    junctionZeroMap.add(new int[] {i, 1});
                }
            } else if (value.startsWith("0")) {
                // rule 2
                boolean hasLeadingZeros = true;
                String s = value;
                ipv6Split[i] = "";
                for (int j = 0; j < s.length(); j++) {
                    if (!(hasLeadingZeros && s.charAt(j) == '0')) {
                        hasLeadingZeros = false;
                        ipv6Split[i] += s.charAt(j); 
                    }
                }
            }
        }
        return ipv6Split;
    }


    /**
     * Search the biggest junction zeros: 0000:1200:0000:0000:0000:0000:0000:0000 corresponds to 0:1200::

     * @param junctionZeroMap the junction map
     * @return the junction zero: first element corresponds to the index , second the length
     */
    protected int[] seachBiggestJunctionZeroRecord(LinkedList<int[]> junctionZeroMap) {
        int[] chooseBiggestJunction = null;
        int maxRange = -1;
        for (int[] z: junctionZeroMap) {
            if (z[1] > maxRange) {
                chooseBiggestJunction = z;
                maxRange = chooseBiggestJunction[1];
            }
        }
        return chooseBiggestJunction;
    }


    /**
     * Combine and format address
     * 
     * @param ipv6Split the address split
     * @param chooseBiggestJunction the junction zero record
     * @return the formatted address
     */
    protected String combineElements(String[] ipv6Split, int[] chooseBiggestJunction) {
        String result = "";
        int i = 0;
        while (i < ipv6Split.length) {
            if (i > 0 && !result.endsWith("::")) {
                result += IPV6_SEPARATOR;
            }

            if (ipv6Split[i].isBlank()) {
                if (chooseBiggestJunction[0] >= 0 && chooseBiggestJunction[1] > 1) {
                    int end = chooseBiggestJunction[0] + chooseBiggestJunction[1];
                    if (chooseBiggestJunction[0] <= i && i < end) {
                        result += IPV6_SEPARATOR;
                        i = end - 1;
                    } else {
                        result += "0";
                    }
                } else { 
                    result += "0";
                }
            } else {
                if (ipv6Split[i].startsWith("0")) {
                    int lastZero = 0;
                    while (lastZero < ipv6Split[i].length() && ipv6Split[i].charAt(lastZero) == '0') {
                        lastZero++;
                    }
                    
                    result += ipv6Split[i].substring(lastZero);
                } else {
                    result += ipv6Split[i];
                }
            }
            
            i++;
        }

        if (result.equals(IPV6_SEPARATOR)) {
            result += IPV6_SEPARATOR;
        }
        return result;
    }
}
