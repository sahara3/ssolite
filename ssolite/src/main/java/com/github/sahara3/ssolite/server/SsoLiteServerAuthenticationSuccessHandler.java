package com.github.sahara3.ssolite.server;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.github.sahara3.ssolite.server.service.SsoLiteServerRedirectResolver;
import com.github.sahara3.ssolite.util.SsoLiteRedirectUrlBuilder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Authentication success handler for SSOLite server.
 *
 * @author sahara3
 */
@RequiredArgsConstructor
public class SsoLiteServerAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	// @NotNull
	// protected final SsoLiteAccessTokenService tokenService;

	@NotNull
	protected final SsoLiteServerRedirectResolver redirectResolver;

	@NotNull
	@Getter
	@Setter
	protected String defaultTopPageUrl;

	@NotNull
	@Setter
	protected Map<URI, URI> permittedDomainMap = new HashMap<>();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		if (this.isAlwaysUseDefaultTargetUrl()) {
			super.onAuthenticationSuccess(request, response, authentication);
			return;
		}

		String from = request.getParameter("from"); // can be null.
		this.logger.debug("onAuthenticationSuccess: from=" + from);

		if (from != null) {
			String target = this.redirectResolver.getRedirectDestination(from, authentication);
			this.clearAuthenticationAttributes(request);

			this.logger.debug("Redirect URL: " + target);
			Assert.notNull(target, "Redirect URL cannot be null.");
			this.getRedirectStrategy().sendRedirect(request, response, target);
		}
		else {
			// internal redirection.
			super.onAuthenticationSuccess(request, response, authentication);
		}
		return;
	}

	// ========================================================================
	// for internal success handler
	// ========================================================================

	private final SsoLiteRedirectUrlBuilder redirectUrlBuilder = new SsoLiteRedirectUrlBuilder();

	@Getter(AccessLevel.PROTECTED)
	private boolean useReferer = false;

	@Override
	public void setUseReferer(boolean useReferer) {
		super.setUseReferer(useReferer);
		this.useReferer = useReferer;
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

	protected String determineDefaultTargetUrl(@NonNull HttpServletRequest request) {
		String top = this.getDefaultTopPageUrl();
		return this.redirectUrlBuilder.buildRedirectUrl(request, top);
	}
}
