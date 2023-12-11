/*
 * IHttpServerLogger.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.server.logger;

/**
 * Defines the http server logger
 *  
 * @author patrick
 */
public interface IHttpServerLogger {
    
    /**
     * Log an information
     *
     * @param info the information to log
     */
    void logInfo(String info);

    
    /**
     * Log a warn information
     *
     * @param info the warn information to log
     * @param e the exception or null
     */
    void logWarn(String info, Exception e);


    /**
     * Log a error information
     *
     * @param info the error information to log
     * @param e the exception or null
     */
    void logError(String info, Exception e);

}
