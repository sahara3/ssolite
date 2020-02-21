package com.github.sahara3.ssolite.samples.server.spring.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception that is thrown when an access token for SSOLite is not found.
 *
 * @author sahara3
 */
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class SsoLiteAccessTokenNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message
     */
    public SsoLiteAccessTokenNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message
     * @param cause
     */
    public SsoLiteAccessTokenNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
