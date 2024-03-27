package com.github.sahara3.ssolite.core.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

import org.dmfs.rfc3986.encoding.Encoded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * URI utilities for SSOLIte.
 *
 * @author sahara3
 */
public class SsoLiteUriUtils {

    private static final Logger LOG = LoggerFactory.getLogger(SsoLiteUriUtils.class);

    /**
     * Returns a domain URI.
     *
     * @param uri the original URI.
     * @return the domain URI.
     * @throws IllegalArgumentException thrown if the {@code uri} is null.
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
     * @throws IllegalArgumentException thrown if the {@code text} or {@code charset} is {@code null}, or the
     *                                  {@code charset} is not supported.
     */
    public static String encodeQueryParam(String text, Charset charset) throws IllegalArgumentException {
        Assert.notNull(text, "text cannot be null");
        Assert.notNull(charset, "charset cannot be null");
        Encoded encoded = new Encoded(text, charset.name());
        return encoded.toString();
    }

    /**
     * Builds redirect URL and return it.
     *
     * @param url         the URL to redirect.
     * @param schema      the schema of the current request.
     * @param serverName  the server name of the current request.
     * @param serverPort  the server port of the current request.
     * @param contextPath the context path of the current request.
     * @return the redirect URL.
     */
    static String buildRedirectUrl(String url, String schema, String serverName, int serverPort, String contextPath) {
        if (SsoLiteUriUtils.isAbsoluteUrl(url)) {
            LOG.debug("URL is absolute: {}", url);
            return url;
        }

        boolean internal = false;
        String path = url;
        if (url.startsWith("internal:")) {
            path = url.substring("internal:".length());
            internal = true;
        }

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(schema).append("://").append(serverName);

        if (serverPort != -1 && serverPort != ("http".equals(schema) ? 80 : 443)) {
            urlBuilder.append(':').append(serverPort);
        }
        if (internal) {
            urlBuilder.append(contextPath);
        }
        urlBuilder.append(path);
        String redirectUrl = urlBuilder.toString();

        LOG.debug("Redirect URL: {} => {}", url, redirectUrl);
        return redirectUrl;
    }
}
