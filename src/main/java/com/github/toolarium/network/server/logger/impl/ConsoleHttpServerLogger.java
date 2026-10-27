/*
 * HttpServerLoggerImpl.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.server.logger.impl;

import com.github.toolarium.network.server.logger.IHttpServerLogger;
import java.io.PrintStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


/**
 * Implements the {@link IHttpServerLogger}.
 *  
 * @author patrick
 */
public class ConsoleHttpServerLogger implements IHttpServerLogger {
    private PrintStream out = System.out;
    private PrintStream err = System.err;
    private final DateTimeFormatter logFormat;


    /**
     * Constructor for ConsoleHttpServerLogger
     */
    public ConsoleHttpServerLogger() {
        logFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
    }

    
    /**
     * @see com.github.toolarium.network.server.logger.IHttpServerLogger#logInfo(java.lang.String)
     */
    @Override
    public void logInfo(String info) {
        out.println(formatLog(info));
    }

    
    /**
     * @see com.github.toolarium.network.server.logger.IHttpServerLogger#logWarn(java.lang.String, java.lang.Exception)
     */
    @Override
    public void logWarn(String info, Exception e) {
        out.println(formatLog(info));
        
        if (e != null) {
            e.printStackTrace(); // CHECKSTYLE IGNORE THIS LINE
        }
    }

    
    /**
     * @see com.github.toolarium.network.server.logger.IHttpServerLogger#logError(java.lang.String, java.lang.Exception)
     */
    @Override
    public void logError(String info, Exception e) {
        err.println(formatLog(info));
        
        if (e != null) {
            e.printStackTrace(); // CHECKSTYLE IGNORE THIS LINE
        }
    }
    

    /**
     * Format log
     *
     * @param info the info
     * @return the preapread log
     */
    protected String formatLog(String info) {
        StringBuilder b = new StringBuilder();
        b.append(logFormat.format(ZonedDateTime.now()));
        b.append(" | ");
        b.append(info);
        return b.toString();   
    }
}
