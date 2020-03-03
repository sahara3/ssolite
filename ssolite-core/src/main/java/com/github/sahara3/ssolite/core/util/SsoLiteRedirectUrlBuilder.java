package com.github.sahara3.ssolite.core.util;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility for building redirect URL builder.
 *
 * @author sahara3
 */
public class SsoLiteRedirectUrlBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(SsoLiteRedirectUrlBuilder.class);

    /**
     * Builds redirect URL and return it.
     *
     * @param request the request object.
     * @param url     the URL to redirect.
     * @return the redirect URL.
     * @throws IllegalArgumentException thrown if the {@code request} or {@code url} is {@code null}.
     */
    public String buildRedirectUrl(HttpServletRequest request, String url) throws IllegalArgumentException {
        Assert.notNull(request, "request cannot be null");
        Assert.notNull(url, "url cannot be null");

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

        String redirectUrl = buildUrl(request, path, internal);
        LOG.debug("Redirect URL: {} => {}", url, redirectUrl);
        return redirectUrl;
    }

    private static String buildUrl(HttpServletRequest request, String path, boolean isInternal) {
        StringBuilder url = new StringBuilder();

        String schema = request.getScheme();
        url.append(schema).append("://");

        url.append(request.getServerName());

        int port = request.getServerPort();
        if (port != -1 && port != ("http".equals(schema) ? 80 : 443)) {
            url.append(':').append(port);
        }

        if (isInternal) {
            url.append(request.getContextPath());
        }

        url.append(path);
        return url.toString();
    }
}
