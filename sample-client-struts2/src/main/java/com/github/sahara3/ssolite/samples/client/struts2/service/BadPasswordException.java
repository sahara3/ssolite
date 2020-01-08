package com.github.sahara3.ssolite.samples.client.struts2.service;

public class BadPasswordException extends AuthException {

    private static final long serialVersionUID = 1L;

    public BadPasswordException() {
        super();
    }

    public BadPasswordException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public BadPasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadPasswordException(String message) {
        super(message);
    }

    public BadPasswordException(Throwable cause) {
        super(cause);
    }
}
