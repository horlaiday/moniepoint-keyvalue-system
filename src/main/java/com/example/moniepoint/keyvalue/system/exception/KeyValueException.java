package com.example.moniepoint.keyvalue.system.exception;

public class KeyValueException extends RuntimeException{

    public KeyValueException() {
        super();
    }

    public KeyValueException(String message) {
        super(message);
    }

    public KeyValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyValueException(Throwable cause) {
        super(cause);
    }

    protected KeyValueException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
