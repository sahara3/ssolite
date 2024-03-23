package com.github.sahara3.ssolite.spring.server;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.github.sahara3.ssolite.core.util.SsoLiteRedirectUrlBuilder;

/**
 * Authentication success handler for SSOLite server.
 *
 * @author sahara3
 */
public class SsoLiteServerAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    protected final SsoLiteServerRedirectResolver redirectResolver;

    public SsoLiteServerAuthenticationSuccessHandler(SsoLiteServerRedirectResolver redirectResolver,
            String defaultTopPageUrl) {
        Assert.notNull(redirectResolver, "redirectResolver cannot be null");
        Assert.notNull(defaultTopPageUrl, "defaultTopPageUrl cannot be null");
        this.redirectResolver = redirectResolver;
        this.defaultTopPageUrl = defaultTopPageUrl;
    }

    protected String defaultTopPageUrl;

    /**
     * @return the default top page URL.
     */
    public String getDefaultTopPageUrl() {
        return this.defaultTopPageUrl;
    }

    /**
     * @param defaultTopPageUrl the default top page URL to set.
     */
    public void setDefaultTopPageUrl(String defaultTopPageUrl) {
        Assert.notNull(defaultTopPageUrl, "defaultTopPageUrl cannot be null");
        this.defaultTopPageUrl = defaultTopPageUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {
        if (this.isAlwaysUseDefaultTargetUrl()) {
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        String target = this.redirectResolver.resolveRedirectUrlOnSuccess(request, authentication);
        if (target != null) {
            this.clearAuthenticationAttributes(request);

            this.logger.debug("Redirect URL: " + target);
            Assert.notNull(target, "Redirect URL cannot be null");
            this.getRedirectStrategy().sendRedirect(request, response, target);
        }
        else {
            // internal redirection.
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }

    // ========================================================================
    // for internal success handler
    // ========================================================================

    private final SsoLiteRedirectUrlBuilder redirectUrlBuilder = new SsoLiteRedirectUrlBuilder();

    private boolean useReferer = false;

    protected boolean isUseReferer() {
        return this.useReferer;
    }

    @Override
    public void setUseReferer(boolean useReferer) {
        super.setUseReferer(useReferer);
        this.useReferer = useReferer;
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        if (this.isAlwaysUseDefaultTargetUrl()) {
            return this.determineDefaultTargetUrl(request);
        }

        // Check for the parameter and use that if available
        String target = null;

        if (this.getTargetUrlParameter() != null) {
            target = request.getParameter(this.getTargetUrlParameter());

            if (StringUtils.hasText(target)) {
                this.logger.debug("Found targetUrlParameter in request: " + target);

                return target;
            }
        }

        if (this.isUseReferer() && !StringUtils.hasLength(target)) {
            target = request.getHeader("Referer");
            this.logger.debug("Using Referer header: " + target);
        }

        if (!StringUtils.hasText(target)) {
            target = this.determineDefaultTargetUrl(request);
            this.logger.debug("Using default Url: " + target);
        }

        return target;
    }

    protected String determineDefaultTargetUrl(HttpServletRequest request) {
        Assert.notNull(request, "request cannot be null");

        String top = this.getDefaultTopPageUrl();
        return this.redirectUrlBuilder.buildRedirectUrl(request, top);
    }
}
