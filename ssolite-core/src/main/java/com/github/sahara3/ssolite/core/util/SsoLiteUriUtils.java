package com.github.sahara3.ssolite.core.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

import org.dmfs.rfc3986.encoding.Encoded;

/**
 * URI utilities for SSOLIte.
 *
 * @author sahara3
 */
public class SsoLiteUriUtils {

    /**
     * Returns a domain URI.
     *
     * @param uri the original URI.
     * @return the domain URI.
     * @throws IllegalArgumentException thrown if uri is null.
     */
    public static URI getDomainUri(URI uri) throws IllegalArgumentException {
        Assert.notNull(uri, "uri cannot be null");

        try {
            return new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), null, null, null);
        }
        catch (URISyntaxException e) {
            // Unreachable code.
            throw new IllegalArgumentException("invalid URI form: " + uri, e);
        }
    }

    private static final Pattern ABSOLUTE_URL_PATTERN = Pattern.compile("\\A[a-zA-Z][a-zA-Z0-9+-.]*://.*");

    /**
     * Returns true if a URL is absolute.
     *
     * @param url the URL to check.
     * @return true if the URL is absolute.
     */
    public static boolean isAbsoluteUrl(String url) {
        if (url == null) {
            return false;
        }
        return ABSOLUTE_URL_PATTERN.matcher(url).matches();
    }

    /**
     * Returns an encoded query parameter.
     *
     * @param text    the text to be encoded.
     * @param charset the string encoding character set.
     * @return the encoded query parameter.
     * @throws IllegalArgumentException thrown if text or charset is null, or charset is not supported.
     */
    public static String encodeQueryParam(String text, Charset charset) throws IllegalArgumentException {
        Assert.notNull(text, "text cannot be null");
        Assert.notNull(charset, "charset cannot be null");
        Encoded encoded = new Encoded(text, charset.name());
        return encoded.toString();
    }
}
