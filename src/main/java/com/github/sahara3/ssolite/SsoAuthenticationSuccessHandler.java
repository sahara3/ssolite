package com.github.sahara3.ssolite;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.github.sahara3.ssolite.config.SsoLoginServerProperties;
import com.github.sahara3.ssolite.model.SsoAccessToken;
import com.github.sahara3.ssolite.service.SsoAccessTokenService;
import com.github.sahara3.ssolite.util.ContextAwareRedirectUrlBuilder;

import lombok.NonNull;

public class SsoAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@NotNull
	protected final SsoAccessTokenService tokenService;

	@NotNull
	protected final SsoLoginServerProperties serverProperties;

	@NotNull
	protected final Map<URI, String> permittedDomainMap;

	@SuppressWarnings("static-method")
	protected URI getDomainUri(@NonNull URI uri) throws URISyntaxException {
		return new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), null, null, null);
	}

	public SsoAuthenticationSuccessHandler(@NonNull SsoAccessTokenService tokenService,
			@NonNull SsoLoginServerProperties serverProperties) {
		if (tokenService == null) {
			throw new IllegalArgumentException("tokenService cannot be null.");
		}
		this.tokenService = tokenService;
		this.serverProperties = serverProperties;

		this.permittedDomainMap = new HashMap<>();
		serverProperties.getPermittedDomains().forEach(s -> {
			try {
				URI uri = new URI(s);
				this.permittedDomainMap.put(this.getDomainUri(uri), s);
			}
			catch (URISyntaxException e) {
				this.logger.warn("Invalid URI: " + s, e);
			}
		});
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
			this.redirectToExternal(request, response, from);
		}
		return;
	}

	// == for internal success handler == //
	private RequestCache requestCache = new HttpSessionRequestCache();

	private final ContextAwareRedirectUrlBuilder redirectUrlBuilder = new ContextAwareRedirectUrlBuilder();

	private boolean useReferer = false;

	public boolean getUseReferer() {
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
		SavedRequest saved_request = this.requestCache.getRequest(request, response);

		if (saved_request == null) {
			super.onAuthenticationSuccess(request, response, authentication);
			return;
		}

		String target_param = this.getTargetUrlParameter();
		if (this.isAlwaysUseDefaultTargetUrl()
				|| (target_param != null && StringUtils.hasText(request.getParameter(target_param)))) {
			this.requestCache.removeRequest(request, response);
			super.onAuthenticationSuccess(request, response, authentication);
			return;
		}

		this.clearAuthenticationAttributes(request);

		// Use the DefaultSavedRequest URL
		String target = saved_request.getRedirectUrl();
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
		String top = this.serverProperties.getDefaultTopPageUrl();
		return this.redirectUrlBuilder.buildRedirectUrl(request, top);
	}

	// == for external success handler == //
	protected void redirectToExternal(HttpServletRequest request, HttpServletResponse response, @NonNull String from)
			throws IOException {

		String target;
		if (!UrlUtils.isAbsoluteUrl(from)) {
			target = from;
		}
		else {
			Map<String, String> queries = new HashMap<>();

			target = this.getSsoRedirectDestination(from);
			if (target == null) {
				// not permitted domain.
				// FIXME.
			}

			// generate access token.
			HttpSession session = request.getSession();
			SsoAccessToken token = this.tokenService.createAccessToken(session.getId());
			queries.put("token", token.getId());

			// next URL after SSO processing.
			queries.put("next", from);
		}

		this.clearAuthenticationAttributes(request);

		this.logger.debug("Redirect URL: " + target);
		Assert.notNull(target, "Redirect URL cannot be null.");
		this.getRedirectStrategy().sendRedirect(request, response, target);
	}

	protected String getSsoRedirectDestination(@NonNull String from) {
		// TODO: support a domain that have multiple applications (sso-login).
		try {
			URI uri = new URI(from);
			return this.permittedDomainMap.get(this.getDomainUri(uri));
		}
		catch (URISyntaxException e) {
			this.logger.debug("Invalid from URL: " + from, e);
			return null;
		}
	}
}
