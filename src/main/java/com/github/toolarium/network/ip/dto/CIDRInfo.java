/*
 * CIDRInfo.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.ip.dto;

import java.io.Serializable;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


/**
 * Define the CIDR information
 * 
 * @author patrick
 */
public class CIDRInfo implements Serializable {
    private static final long serialVersionUID = 422885747646991475L;
    private final String address;
    private final int network;
    private final InetAddress networkAddress;
    private final int targetSize;
    private final InetAddress startAddress;
    private final BigInteger startIp;
    private final InetAddress endAddress;
    private final BigInteger endIp;

    
    /**
     * Constructor for CIDR
     * 
     * @param address the address, e.g. 192.168.0.1 
     * @param network the network , e.g. 24
     * @throws UnknownHostException In case the address can't be resolved
     */
    public CIDRInfo(String address, int network) throws UnknownHostException {
        this.address = address;
        this.network = network;
        this.networkAddress = InetAddress.getByName(address);
        if (networkAddress.getAddress().length == 4) {
            this.targetSize = 4;
        } else {
            this.targetSize = 16;
        }
        
        final ByteBuffer maskBuffer;
        if (networkAddress.getAddress().length == 4) {
            maskBuffer = ByteBuffer.allocate(targetSize).putInt(-1);
        } else {
            maskBuffer = ByteBuffer.allocate(targetSize).putLong(-1L).putLong(-1L);
        }

        BigInteger mask = (new BigInteger(1, maskBuffer.array())).not().shiftRight(network);
        ByteBuffer buffer = ByteBuffer.wrap(networkAddress.getAddress());
        BigInteger ipVal = new BigInteger(1, buffer.array());

        this.startIp = ipVal.and(mask);
        this.startAddress = InetAddress.getByAddress(toBytes(startIp.toByteArray(), targetSize)); 
        this.endIp = startIp.add(mask.not());
        this.endAddress = InetAddress.getByAddress(toBytes(endIp.toByteArray(), targetSize));
    }


    /**
     * Get the address, e.g. 192.168.0.1
     *
     * @return the address
     */
    public String getAddress() {
        return address;
    }
    
    
    /**
     * Get the network
     *
     * @return the network, e.g. 24
     */
    public int getNetwork() {
        return network;
    }

    
    /**
     * The start address
     *
     * @return the start address
     */
    public InetAddress getStartAddress() {
        return this.startAddress;
    }

    
    /**
     * The start address string
     *
     * @return the start address
     */
    public String getStartAddressString() {
        return this.startAddress.getHostAddress();
    }

    
    /**
     * The start ip
     *
     * @return the start ip
     */
    public BigInteger getStartIp() {
        return this.startIp;
    }

    
    /**
     * The end address
     *
     * @return the end address
     */
    public InetAddress getEndAddress() {
        return this.endAddress;
    }


    /**
     * The end address string (ipv4 corresponds this to the broadcast address)
     *
     * @return The end / broadcast address
     */
    public String getEndAddressString() {
        return this.endAddress.getHostAddress();
    }

    
    /**
     * The end ip
     *
     * @return the end ip
     */
    public BigInteger getEndIp() {
        return this.endIp;
    }
    

    /**
     * Resolves an address
     *
     * @param addr the address as BigInteger to resolve
     * @return the resolved address
     * @throws UnknownHostException In case the address can't be resolved
     */
    public InetAddress resolveAddress(BigInteger addr) throws UnknownHostException {
        return resolveAddress(addr.toByteArray());
    }

    
    /**
     * Resolves an address
     *
     * @param addr the address ad byte[] to resolve
     * @return the resolved address
     * @throws UnknownHostException In case the address can't be resolved
     */
    public InetAddress resolveAddress(byte[] addr) throws UnknownHostException {
        return InetAddress.getByAddress(toBytes(addr, getTargetSize()));
    }

    
    /**
     * Return the ip address, in byte array form, e.g. <code>01111111 00000000 00000000 00000001</code>
     *
     * @param address the address
     * @return the byte array form, e.g. <code>01111111 00000000 00000000 00000001</code>
     */
    public String toBinaryString(InetAddress address) {
        return toBinaryString(address.getAddress());
    }

    
    /**
     * Return the ip address, in byte array form, e.g. <code>01111111 00000000 00000000 00000001</code>
     *
     * @param address the address
     * @return the byte array form, e.g. <code>01111111 00000000 00000000 00000001</code>
     */
    public String toBinaryString(byte[] address) {
        String addressBits = "";
        for (byte octet : toBytes(address, targetSize)) {
            for (int i = 7; i >= 0; i--) {
                int bit = 1 << i;
                
                if ((octet & bit) == bit) {
                    addressBits += String.valueOf("1");
                } else {
                    addressBits += String.valueOf("0");
                }
            }
            addressBits += " ";
        }
        
        return "" + addressBits.substring(0, addressBits.length() - 1);
    }

    
    /**
     * Get the target size
     *
     * @return the target size
     */
    public int getTargetSize() {
        return targetSize;
    }


    /**
     * Check if a given address is in range
     *
     * @param ipAddress the address to check
     * @return true if it is in range.
     * @throws UnknownHostException In case if an invalid address
     */
    public boolean isInRange(String ipAddress) throws UnknownHostException {
        InetAddress address = InetAddress.getByName(ipAddress);
        BigInteger start = new BigInteger(1, this.startAddress.getAddress());
        BigInteger end = new BigInteger(1, this.endAddress.getAddress());
        BigInteger target = new BigInteger(1, address.getAddress());

        int st = start.compareTo(target);
        int te = target.compareTo(end);

        return (st < 0 || st == 0) && (te < 0 || te == 0);
    }
    
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return address + "/" + network;
    }


    /**
     * Prepare byte array
     *
     * @param input the input bytes
     * @param targetSize the target size
     * @return the prepared byte array
     */
    private byte[] toBytes(byte[] input, int targetSize) {
        int counter = 0;
        List<Byte> newArr = new ArrayList<Byte>();
        while (counter < targetSize && (input.length - 1 - counter >= 0)) {
            newArr.add(0, input[input.length - 1 - counter]);
            counter++;
        }

        int size = newArr.size();
        for (int i = 0; i < (targetSize - size); i++) {

            newArr.add(0, (byte) 0);
        }

        byte[] ret = new byte[newArr.size()];
        for (int i = 0; i < newArr.size(); i++) {
            ret[i] = newArr.get(i);
        }

        return ret;
    }
}
