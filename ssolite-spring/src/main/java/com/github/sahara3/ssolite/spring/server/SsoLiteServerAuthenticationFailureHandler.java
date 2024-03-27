package com.github.sahara3.ssolite.spring.server;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;

/**
 * Authentication failure handler for SSOLite server.
 *
 * @author sahara3
 */
public class SsoLiteServerAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SsoLiteServerAuthenticationFailureHandler.class);

    private final SsoLiteServerRedirectResolver redirectResolver;

    private final String forwardUrl;

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    /**
     * @param redirectResolver Redirect resolver of SSOLite.
     * @param forwardUrl       URL to forward when login failure.
     */
    public SsoLiteServerAuthenticationFailureHandler(SsoLiteServerRedirectResolver redirectResolver,
            String forwardUrl) {
        Assert.notNull(redirectResolver, "redirectResolver cannot be null");
        Assert.isTrue(UrlUtils.isValidRedirectUrl(forwardUrl), "'" + forwardUrl + "' is not a valid forward URL");
        this.redirectResolver = redirectResolver;
        this.forwardUrl = forwardUrl;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException {
        Assert.notNull(request, "request cannot be null");
        Assert.notNull(response, "response cannot be null");

        String url = this.redirectResolver.resolveRedirectUrlOnFailure(request, this.forwardUrl);
        LOG.debug("Redirect URL: {}", url);

        request.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, exception);

        Assert.notNull(url, "Redirect URL cannot be null");
        this.redirectStrategy.sendRedirect(request, response, url);
    }

}
