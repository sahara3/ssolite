package com.github.sahara3.ssolite.core.client;

/**
 * SSOLite access token API exception.
 *
 * @author sahara3
 */
public class SsoLiteAccessTokenApiException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param message the detail message.
     * @see RuntimeException#RuntimeException(String)
     */
    public SsoLiteAccessTokenApiException(String message) {
        super(message);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     * 
     * @param message the detail message.
     * @param cause   the cause.
     * @see RuntimeException#RuntimeException(String, Throwable)
     */
    public SsoLiteAccessTokenApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
