package com.github.sahara3.ssolite.core.util;

import javax.servlet.http.HttpServletRequest;

/**
 * Utility for building redirect URL builder, for Servlet API 3 or older.
 *
 * @author sahara3
 */
public class LegacySsoLiteRedirectUrlBuilder {

    /**
     * Builds redirect URL and return it.
     *
     * @param request the request object.
     * @param url     the URL to redirect.
     * @return the redirect URL.
     * @throws IllegalArgumentException thrown if the {@code request} or {@code url} is {@code null}.
     */
    public String buildRedirectUrl(HttpServletRequest request, String url)
            throws IllegalArgumentException {
        Assert.notNull(request, "request cannot be null");
        Assert.notNull(url, "url cannot be null");
        return SsoLiteUriUtils.buildRedirectUrl(url,
                request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath());
    }
}
