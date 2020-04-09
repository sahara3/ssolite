package com.github.sahara3.ssolite.spring.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.util.Assert;
import org.springframework.web.util.UriUtils;

import com.github.sahara3.ssolite.core.util.SsoLiteRedirectUrlBuilder;

/**
 * {@link AuthenticationEntryPoint} implementation that redirects to an external server URL.
 *
 * @author sahara3
 */
public class ExternalAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger LOG = LoggerFactory.getLogger(ExternalAuthenticationEntryPoint.class);

    private final String loginFormUrl;

    private final boolean sameDomain;

    private final SsoLiteRedirectUrlBuilder urlBuilder = new SsoLiteRedirectUrlBuilder();

    /**
     * Constructor that can specify external or not.
     *
     * @param loginFormUrl the URL of login form.
     * @param sameDomain   set true if the {@code loginFormUrl} is on the same domain.
     */
    public ExternalAuthenticationEntryPoint(String loginFormUrl, boolean sameDomain) {
        Assert.notNull(loginFormUrl, "loginFormUrl cannot be null");
        this.loginFormUrl = loginFormUrl;
        this.sameDomain = sameDomain;
    }

    /**
     * Constructs the external entry point.
     *
     * <p>
     * This is same as {@code new ExternalAuthenticationEntryPoint(loginFormUrl, false)}.
     * </p>
     *
     * @param loginFormUrl the URL of login form.
     */
    public ExternalAuthenticationEntryPoint(String loginFormUrl) {
        this(loginFormUrl, false);
    }

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        String url = this.buildRedirectUrl(request, this.loginFormUrl);
        this.redirectStrategy.sendRedirect(request, response, url);
    }

    private String buildRedirectUrl(HttpServletRequest request, String url) {
        Assert.notNull(request, "request cannot be null");
        Assert.notNull(url, "url cannot be null");

        String from = this.generateFromUrl(request);
        LOG.debug("buildRedirectUrl: url={}, from={}", url, from);

        String redirectUrl = this.urlBuilder.buildRedirectUrl(request, url);

        // append "from".
        String encoded = UriUtils.encodeQueryParam(from, StandardCharsets.UTF_8.name());
        redirectUrl += "?from=" + encoded;

        LOG.debug("Redirect URL: {}", redirectUrl);
        return redirectUrl;
    }

    private String generateFromUrl(HttpServletRequest request) {
        Assert.notNull(request, "request cannot be null");

        if (this.sameDomain) {
            String from = request.getRequestURI();
            String query = request.getQueryString();
            if (query != null) {
                from += "?" + query;
            }
            return from;
        }

        // not a same domain.
        StringBuffer from = request.getRequestURL();
        String query = request.getQueryString();
        if (query != null) {
            from.append('?').append(query);
        }
        return from.toString();
    }
}
