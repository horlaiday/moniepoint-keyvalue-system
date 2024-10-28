package com.example.moniepoint.keyvalue.system.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class KeyValueLogger {

    private String endpoint;
    private Long benchmark;
    private String key = null;
    private String value = null;
    private String message = null;

    private static final Logger logger = LoggerFactory.getLogger(KeyValueLogger.class);

    public KeyValueLogger(String endpoint) {
        this.endpoint = endpoint;
        this.benchmark = System.currentTimeMillis();
    }

    public KeyValueLogger key(String key) {
        this.key = key;
        return this;
    }

    public KeyValueLogger value(String value) {
        this.value = value;
        return this;
    }

    public KeyValueLogger message(String message) {
        this.message = message;
        return this;
    }

    public String logDetails() {
        String logDetails = "CLASS : " + this.endpoint + " | METHOD : "
                + Thread.currentThread().getStackTrace()[3].getMethodName() + " | LINE : "
                + Thread.currentThread().getStackTrace()[3].getLineNumber() + " | TAT : "
                + (System.currentTimeMillis() - this.benchmark);

        if (this.key != null) {
            logDetails += " | KEY : " + this.key;
            this.key = null;
        }

        if (this.value != null) {
            logDetails += " | VALUE : " + this.value;
            this.value = null;
        }

        if (this.message != null) {
            logDetails += " | MESSAGE : " + this.message;
        }

        return logDetails;
    }

    public void info() {
        logger.info(this.logDetails());
    }

    public void error() {
        logger.error(this.logDetails());
    }

    public void debug() {
        logger.debug(this.logDetails());
    }
}
