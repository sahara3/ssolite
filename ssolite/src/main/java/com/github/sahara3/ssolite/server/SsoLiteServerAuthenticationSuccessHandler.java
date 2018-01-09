package com.github.sahara3.ssolite.server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriUtils;

import com.github.sahara3.ssolite.model.SsoLiteAccessToken;
import com.github.sahara3.ssolite.server.service.SsoLiteAccessTokenService;
import com.github.sahara3.ssolite.util.ContextAwareRedirectUrlBuilder;
import com.github.sahara3.ssolite.util.DomainUriUtils;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Authentication success handler for SSOLite server.
 *
 * @author sahara3
 */
public class SsoLiteServerAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@NotNull
	protected final SsoLiteAccessTokenService tokenService;

	// @NotNull
	// protected final SsoLiteServerProperties serverProperties;

	@NotNull
	@Getter
	@Setter
	protected String defaultTopPageUrl;

	@NotNull
	@Setter
	protected Map<URI, URI> permittedDomainMap = new HashMap<>();

	public SsoLiteServerAuthenticationSuccessHandler(@NonNull SsoLiteAccessTokenService tokenService) {
		this.tokenService = tokenService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		if (this.isAlwaysUseDefaultTargetUrl()) {
			super.onAuthenticationSuccess(request, response, authentication);
			return;
		}

		String from = request.getParameter("from"); // can be null.
		this.logger.debug("onAuthenticationSuccess: from=" + from);

		if (from == null) {
			// internal redirection.
			this.redirectToInternal(request, response, authentication);
		}
		else {
			this.redirectToExternal(request, response, authentication, from);
		}
		return;
	}

	// ========================================================================
	// for internal success handler
	// ========================================================================

	private RequestCache requestCache = new HttpSessionRequestCache();

	private final ContextAwareRedirectUrlBuilder redirectUrlBuilder = new ContextAwareRedirectUrlBuilder();

	private boolean useReferer = false;

	protected boolean getUseReferer() {
		return this.useReferer;
	}

	@Override
	public void setUseReferer(boolean useReferer) {
		this.useReferer = useReferer;
	}

	/**
	 * Process after authentication success for internal requests.
	 *
	 * @see SavedRequestAwareAuthenticationSuccessHandler#onAuthenticationSuccess(HttpServletRequest,
	 *      HttpServletResponse, Authentication)
	 */
	protected void redirectToInternal(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		SavedRequest savedRequest = this.requestCache.getRequest(request, response);

		if (savedRequest == null) {
			super.onAuthenticationSuccess(request, response, authentication);
			return;
		}

		String targetUrlParameter = this.getTargetUrlParameter();
		if (this.isAlwaysUseDefaultTargetUrl()
				|| (targetUrlParameter != null && StringUtils.hasText(request.getParameter(targetUrlParameter)))) {
			this.requestCache.removeRequest(request, response);
			super.onAuthenticationSuccess(request, response, authentication);
			return;
		}

		this.clearAuthenticationAttributes(request);

		// Use the DefaultSavedRequest URL
		String target = savedRequest.getRedirectUrl();
		this.logger.debug("Redirecting to DefaultSavedRequest Url: " + target);
		this.getRedirectStrategy().sendRedirect(request, response, target);
	}

	@Override
	protected String determineTargetUrl(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response) {
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

		if (this.getUseReferer() && !StringUtils.hasLength(target)) {
			target = request.getHeader("Referer");
			this.logger.debug("Using Referer header: " + target);
		}

		if (!StringUtils.hasText(target)) {
			target = this.determineDefaultTargetUrl(request);
			this.logger.debug("Using default Url: " + target);
		}

		return target;
	}

	protected String determineDefaultTargetUrl(@NonNull HttpServletRequest request) {
		String top = this.getDefaultTopPageUrl();
		return this.redirectUrlBuilder.buildRedirectUrl(request, top);
	}

	// ========================================================================
	// for external success handler
	// ========================================================================

	protected void redirectToExternal(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication, @NonNull String from) throws IOException {

		String target;
		if (!UrlUtils.isAbsoluteUrl(from)) {
			target = from;
		}
		else {
			URI destination = this.getSsoRedirectDestinationUri(from);
			if (destination == null) {
				// FIXME: runtime error is collect?
				throw new RuntimeException("Not permitted domain: " + from);
			}

			StringBuilder builder = new StringBuilder(destination.toASCIIString());
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
			builder.append("token=").append(UriUtils.encodeQueryParam(token.getId(), "utf-8"));

			// next URL after SSO processing.
			builder.append("&next=").append(UriUtils.encodeQueryParam(from, "utf-8"));

			// generate redirect URL.
			target = builder.toString();
		}

		this.clearAuthenticationAttributes(request);

		this.logger.debug("Redirect URL: " + target);
		Assert.notNull(target, "Redirect URL cannot be null.");
		this.getRedirectStrategy().sendRedirect(request, response, target);
	}

	protected URI getSsoRedirectDestinationUri(@NonNull String from) {
		// TODO: support a domain that have multiple applications (sso-login).
		try {
			URI uri = new URI(from);
			return this.permittedDomainMap.get(DomainUriUtils.getDomainUri(uri));
		}
		catch (URISyntaxException e) {
			this.logger.debug("Invalid from URL: " + from, e);
			return null;
		}
	}
}
