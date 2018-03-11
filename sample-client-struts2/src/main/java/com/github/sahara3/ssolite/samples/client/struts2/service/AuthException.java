package com.github.sahara3.ssolite.samples.client.struts2.service;

public class AuthException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AuthException() {
		super();
	}

	public AuthException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public AuthException(String message, Throwable cause) {
		super(message, cause);
	}

	public AuthException(String message) {
		super(message);
	}

	public AuthException(Throwable cause) {
		super(cause);
	}
}
