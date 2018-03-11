package com.github.sahara3.ssolite.util;

import java.net.URI;
import java.net.URISyntaxException;

import lombok.NonNull;

/**
 * URI utilities for SSOLIte.
 *
 * @author sahara3
 */
public class SsoLiteUriUtils {

	/**
	 * Returns a domain URI.
	 *
	 * @param uri
	 *            the original URI.
	 * @return the domain URI.
	 */
	public static URI getDomainUri(@NonNull URI uri) {
		try {
			return new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), null, null, null);
		}
		catch (URISyntaxException e) {
			// Unreachable code.
			throw new RuntimeException(e);
		}
	}
}
