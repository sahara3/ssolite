package com.github.sahara3.ssolite.spring.server;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;
import org.springframework.web.util.UriUtils;

import com.github.sahara3.ssolite.core.model.SsoLiteAccessToken;
import com.github.sahara3.ssolite.core.util.SsoLiteUriUtils;
import com.github.sahara3.ssolite.spring.server.service.SsoLiteAccessTokenService;

/**
 * SSOLite redirect resolver.
 *
 * <p>
 * This resolves and returns the redirect URL after login (both successful and failure).
 * </p>
 *
 * @author sahara3
 */
public class SsoLiteServerRedirectResolver {

    private static final Logger LOG = LoggerFactory.getLogger(SsoLiteServerRedirectResolver.class);

    protected final SsoLiteAccessTokenService tokenService;

    public SsoLiteServerRedirectResolver(SsoLiteAccessTokenService tokenService) {
        Assert.notNull(tokenService, "tokenService cannot be null");
        this.tokenService = tokenService;
    }

    protected Map<URI, URI> permittedDomainMap = new HashMap<>();

    public void setPermittedDomainMap(Map<URI, URI> permittedDomainMap) {
        Assert.notNull(permittedDomainMap, "permittedDomainMap cannot be null");
        this.permittedDomainMap = permittedDomainMap;
    }

    public void setPermittedDomains(@Nullable List<String> permittedDomains) {
        this.permittedDomainMap = new HashMap<>();
        if (permittedDomains == null) {
            return;
        }

        for (String permittedDomain : permittedDomains) {
            try {
                URI ssoLoginUri = new URI(permittedDomain);
                URI domainUri = SsoLiteUriUtils.getDomainUri(ssoLoginUri);
                this.permittedDomainMap.put(domainUri, ssoLoginUri);
                LOG.debug("Permitted domain: {}", permittedDomain);
            }
            catch (URISyntaxException e) {
                LOG.warn("Invalid URI: " + permittedDomain, e);
            }
        }
    }

    /**
     * Resolves and returns the redirect URL after login.
     *
     * @param from           the URL where an user came from before login.
     * @param authentication the authentication token when logged in.
     * @return the redirect URL.
     * @throws AccessDeniedException thrown if the domain where the request came from is not permitted.
     */
    public String resolveRedirectUrlOnSuccess(String from, Authentication authentication) throws AccessDeniedException {
        Assert.notNull(from, "from cannot be null");
        Assert.notNull(authentication, "authentication cannot be null");

        if (!UrlUtils.isAbsoluteUrl(from)) {
            return from;
        }

        URI destination = this.getSsoRedirectUri(from);
        if (destination == null) {
            LOG.debug("{} is not permitted domain, so throws AccessDenied.", from);
            throw new AccessDeniedException("Not permitted domain: " + from);
        }

        String target = destination.toASCIIString();
        Assert.isTrue(UrlUtils.isAbsoluteUrl(target), "Redirect target is not absolute: " + target);

        StringBuilder builder = new StringBuilder(target);
        if (destination.getQuery() != null) {
            builder.append('&');
        }
        else {
            builder.append('?');
        }

        // generate access token.
        Object principal = authentication.getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        }
        else {
            username = principal.toString();
        }
        SsoLiteAccessToken token = this.tokenService.createAccessToken(username);
        builder.append("token=").append(this.encodeQueryParam(token.getId()));

        // next URL after SSO processing.
        builder.append("&next=").append(this.encodeQueryParam(from));

        // generate redirect URL.
        return builder.toString();
    }

    /**
     * Resolves and returns the redirect URL after login using &quot;from&quot; request parameter.
     *
     * <p>
     * This method is a useful wrapper of {@link #resolveRedirectUrlOnSuccess(String, Authentication)}.
     * </p>
     *
     * @param request        the HTTP request.
     * @param authentication the authentication token when logged in.
     * @return the redirect URL, or {@code null} if &quot;from&quot; parameter is not found.
     * @throws AccessDeniedException thrown if the domain where the request came from is not permitted.
     */
    public @Nullable String resolveRedirectUrlOnSuccess(HttpServletRequest request, Authentication authentication)
            throws AccessDeniedException {
        Assert.notNull(request, "request cannot be null");

        String from = request.getParameter("from"); // can be null.
        LOG.debug("Request parameter: from={}", from);

        return (from == null) ? null : this.resolveRedirectUrlOnSuccess(from, authentication);
    }

    protected @Nullable URI getSsoRedirectUri(String from) {
        Assert.notNull(from, "from cannot be null");

        // TODO: support a domain that have multiple applications (sso-login).
        try {
            URI uri = new URI(from);
            return this.permittedDomainMap.get(SsoLiteUriUtils.getDomainUri(uri));
        }
        catch (URISyntaxException e) {
            LOG.debug("Invalid from URL: " + from, e);
            return null;
        }
    }

    /**
     * Resolves and returns the redirect URL after login failure, using &quot;from&quot; request parameter.
     *
     * @param request    the HTTP request.
     * @param forwardUrl the forward URL on login failure.
     * @return the redirect URL.
     */
    public String resolveRedirectUrlOnFailure(HttpServletRequest request, String forwardUrl) {
        String from = request.getParameter("from"); // can be null.
        LOG.debug("Request parameter: from={}", from);

        if (from == null) {
            return forwardUrl;
        }

        StringBuilder builder = new StringBuilder(forwardUrl);

        if (forwardUrl.contains("?")) {
            if (!forwardUrl.endsWith("?")) {
                builder.append('&');
            }
        }
        else {
            builder.append('?');
        }
        builder.append("from=").append(this.encodeQueryParam(from));
        return builder.toString();
    }

    private final Charset urlCharset = StandardCharsets.UTF_8;

    private String encodeQueryParam(String value) {
        Assert.notNull(value, "value cannot be null");
        return UriUtils.encodeQueryParam(value, this.urlCharset);
    }
}
