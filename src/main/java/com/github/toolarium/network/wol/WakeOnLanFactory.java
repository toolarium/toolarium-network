/*
 * WakeOnLanFactory.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.wol;

import com.github.toolarium.network.wol.dto.IWakeOnLanResult;
import com.github.toolarium.network.wol.impl.WakeOnLanImpl;


/**
 * Factory for creating Wake-on-LAN instances.
 *
 * @author patrick
 */
public final class WakeOnLanFactory {

    private static final class HOLDER {
        static final WakeOnLanFactory INSTANCE = new WakeOnLanFactory();
    }

    /**
     * Constructor.
     */
    private WakeOnLanFactory() {
    }

    /**
     * Get the instance.
     *
     * @return the instance
     */
    public static WakeOnLanFactory getInstance() {
        return HOLDER.INSTANCE;
    }

    /**
     * Get a Wake-on-LAN instance.
     *
     * @return the WOL instance
     */
    public IWakeOnLan getWakeOnLan() {
        return new WakeOnLanImpl();
    }

    /**
     * Convenience: send a magic packet to the given MAC address.
     *
     * @param macAddress the MAC address
     * @return the result
     */
    public IWakeOnLanResult wake(String macAddress) {
        return getWakeOnLan().wake(macAddress);
    }

    /**
     * Convenience: send a magic packet with a specific broadcast address.
     *
     * @param macAddress the MAC address
     * @param broadcastAddress the broadcast address
     * @return the result
     */
    public IWakeOnLanResult wake(String macAddress, String broadcastAddress) {
        return getWakeOnLan().wake(macAddress, broadcastAddress);
    }
}
