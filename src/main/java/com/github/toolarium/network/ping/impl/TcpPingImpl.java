/*
 * TcpPingImpl.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.ping.impl;

import com.github.toolarium.network.ping.IPing;
import com.github.toolarium.network.ping.dto.IPingResult;
import com.github.toolarium.network.ping.dto.PingResult;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implements TCP ping using NIO non-blocking channels and a selector for
 * parallel connection establishment to multiple hosts.
 *
 * @author patrick
 */
public class TcpPingImpl implements IPing {
    private static final Logger LOG = LoggerFactory.getLogger(TcpPingImpl.class);
    private final int timeout;


    /**
     * Constructor for TcpPingImpl
     *
     * @param timeout the connection timeout in milliseconds
     */
    public TcpPingImpl(int timeout) {
        this.timeout = timeout;
    }


    /**
     * @see com.github.toolarium.network.ping.IPing#ping(java.lang.String, int)
     */
    @Override
    public IPingResult ping(String host, int port) {
        List<IPingResult> results = ping(port, host);
        if (results.isEmpty()) {
            return new PingResult(host, port, false, -1, null);
        }
        return results.get(0);
    }


    /**
     * @see com.github.toolarium.network.ping.IPing#ping(int, java.lang.String[])
     */
    @Override
    public List<IPingResult> ping(int port, String... hosts) {
        if (hosts == null || hosts.length == 0) {
            return Collections.emptyList();
        }

        String[] targets = new String[hosts.length];
        for (int i = 0; i < hosts.length; i++) {
            targets[i] = hosts[i] + ":" + port;
        }
        return pingTargets(port, targets);
    }


    /**
     * @see com.github.toolarium.network.ping.IPing#pingTargets(int, java.lang.String[])
     */
    @Override
    public List<IPingResult> pingTargets(int defaultPort, String... targets) {
        if (targets == null || targets.length == 0) {
            return Collections.emptyList();
        }

        // Parse targets into entries
        List<PingEntry> entries = new ArrayList<>();
        for (String target : targets) {
            entries.add(parseTarget(target, defaultPort));
        }

        // Execute parallel ping
        return executePing(entries);
    }


    /**
     * Execute ping for all entries using NIO selector.
     *
     * @param entries the ping entries
     * @return the results
     */
    private List<IPingResult> executePing(List<PingEntry> entries) {
        List<IPingResult> results = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(entries.size());
        Selector selector = null;

        try {
            selector = Selector.open();

            // Initiate all connections
            for (PingEntry entry : entries) {
                initiateConnection(selector, entry, latch, results);
            }

            // Select loop until all done or timeout
            long deadline = System.currentTimeMillis() + timeout;
            while (latch.getCount() > 0) {
                long remaining = deadline - System.currentTimeMillis();
                if (remaining <= 0) {
                    break;
                }

                int ready = selector.select(remaining);
                if (ready > 0) {
                    processSelectedKeys(selector, latch, results);
                }
            }

            // Handle timed-out entries
            for (SelectionKey key : selector.keys()) {
                if (key.isValid() && key.attachment() instanceof PingEntry) {
                    PingEntry entry = (PingEntry) key.attachment();
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException e) {
                        // NOP
                    }
                    synchronized (results) {
                        results.add(new PingResult(entry.getHost(), entry.getPort(), false, -1, null));
                    }
                    latch.countDown();
                }
            }

        } catch (IOException e) {
            LOG.warn("Ping selector error: " + e.getMessage(), e);
        } finally {
            if (selector != null) {
                try {
                    selector.close();
                } catch (IOException e) {
                    // NOP
                }
            }
        }

        // Wait briefly for latch (should already be zero)
        try {
            latch.await(100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return results;
    }


    /**
     * Initiate a non-blocking connection for a ping entry.
     *
     * @param selector the selector
     * @param entry the ping entry
     * @param latch the countdown latch
     * @param results the results list
     */
    private void initiateConnection(Selector selector, PingEntry entry, CountDownLatch latch, List<IPingResult> results) {
        SocketChannel channel = null;
        try {
            InetAddress addr = InetAddress.getByName(entry.getHost());
            final InetSocketAddress socketAddress = new InetSocketAddress(addr, entry.getPort());

            channel = SocketChannel.open();
            channel.configureBlocking(false);

            entry.setConnectStart(System.currentTimeMillis());
            boolean connected = channel.connect(socketAddress);

            if (connected) {
                // Immediate connection (localhost)
                long duration = System.currentTimeMillis() - entry.getConnectStart();
                channel.close();
                synchronized (results) {
                    results.add(new PingResult(entry.getHost(), entry.getPort(), true, duration, null));
                }
                latch.countDown();
            } else {
                // Register for connect completion
                channel.register(selector, SelectionKey.OP_CONNECT, entry);
            }
        } catch (IOException e) {
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException ex) {
                    // NOP
                }
            }
            synchronized (results) {
                results.add(new PingResult(entry.getHost(), entry.getPort(), false, -1, e));
            }
            latch.countDown();
            LOG.debug("Could not connect to " + entry.getHost() + ":" + entry.getPort() + ": " + e.getMessage());
        }
    }


    /**
     * Process selected keys (connection completions).
     *
     * @param selector the selector
     * @param latch the countdown latch
     * @param results the results list
     */
    private void processSelectedKeys(Selector selector, CountDownLatch latch, List<IPingResult> results) {
        Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
        while (iter.hasNext()) {
            SelectionKey key = iter.next();
            iter.remove();

            if (!(key.attachment() instanceof PingEntry)) {
                continue;
            }

            PingEntry entry = (PingEntry) key.attachment();
            SocketChannel channel = (SocketChannel) key.channel();

            try {
                if (channel.finishConnect()) {
                    long duration = System.currentTimeMillis() - entry.getConnectStart();
                    key.cancel();
                    channel.close();
                    synchronized (results) {
                        results.add(new PingResult(entry.getHost(), entry.getPort(), true, duration, null));
                    }
                    latch.countDown();
                }
            } catch (IOException e) {
                key.cancel();
                try {
                    channel.close();
                } catch (IOException ex) {
                    // NOP
                }
                synchronized (results) {
                    results.add(new PingResult(entry.getHost(), entry.getPort(), false, -1, e));
                }
                latch.countDown();
                LOG.debug("Connection failed to " + entry.getHost() + ":" + entry.getPort() + ": " + e.getMessage());
            }
        }
    }


    /**
     * Parse a target string into host and port.
     * Supports: "host", "host:port", "[ipv6]:port"
     *
     * @param target the target string
     * @param defaultPort the default port
     * @return the ping entry
     */
    static PingEntry parseTarget(String target, int defaultPort) {
        String trimmed = target.trim();

        // IPv6 bracket notation: [::1]:port
        if (trimmed.startsWith("[")) {
            int closeBracket = trimmed.indexOf(']');
            if (closeBracket > 0) {
                String host = trimmed.substring(1, closeBracket);
                int port = defaultPort;
                if (closeBracket + 1 < trimmed.length() && trimmed.charAt(closeBracket + 1) == ':') {
                    try {
                        port = Integer.parseInt(trimmed.substring(closeBracket + 2).trim());
                    } catch (NumberFormatException e) {
                        // use default
                    }
                }
                return new PingEntry(host, port);
            }
        }

        // Simple host:port (only if exactly one colon — avoids IPv6 ambiguity)
        int colonIdx = trimmed.indexOf(':');
        if (colonIdx > 0 && colonIdx == trimmed.lastIndexOf(':')) {
            String host = trimmed.substring(0, colonIdx).trim();
            try {
                int port = Integer.parseInt(trimmed.substring(colonIdx + 1).trim());
                return new PingEntry(host, port);
            } catch (NumberFormatException e) {
                // treat entire string as host
            }
        }

        return new PingEntry(trimmed, defaultPort);
    }


    /**
     * Internal representation of a ping target.
     */
    static class PingEntry {
        private final String host;
        private final int port;
        private long connectStart;

        /**
         * Constructor.
         *
         * @param host the host
         * @param port the port
         */
        PingEntry(String host, int port) {
            this.host = host;
            this.port = port;
        }

        /**
         * Get the host.
         *
         * @return the host
         */
        String getHost() {
            return host;
        }

        /**
         * Get the port.
         *
         * @return the port
         */
        int getPort() {
            return port;
        }

        /**
         * Get the connect start time.
         *
         * @return the connect start time
         */
        long getConnectStart() {
            return connectStart;
        }

        /**
         * Set the connect start time.
         *
         * @param connectStart the connect start time
         */
        void setConnectStart(long connectStart) {
            this.connectStart = connectStart;
        }
    }
}
