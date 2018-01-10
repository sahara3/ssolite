package com.github.sahara3.ssolite.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.web.util.UriUtils;

import com.github.sahara3.ssolite.util.SsoLiteRedirectUrlBuilder;

import lombok.NonNull;

public class ExternalAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private static final Logger LOG = LoggerFactory.getLogger(ExternalAuthenticationEntryPoint.class);

	@NotNull
	private final String loginFormUrl;

	private final boolean sameDomain;

	private final SsoLiteRedirectUrlBuilder urlBuilder = new SsoLiteRedirectUrlBuilder();

	public ExternalAuthenticationEntryPoint(@NonNull String loginFormUrl, boolean sameDomain) {
		this.loginFormUrl = loginFormUrl;
		this.sameDomain = sameDomain;
	}

	public ExternalAuthenticationEntryPoint(@NonNull String loginFormUrl) {
		this(loginFormUrl, false);
	}

	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {

		String url = this.buildRedirectUrl(request, this.loginFormUrl);
		this.redirectStrategy.sendRedirect(request, response, url);
	}

	private String buildRedirectUrl(@NonNull HttpServletRequest request, @NonNull String url) {
		String from = this.generateFromUrl(request);
		LOG.debug("buildRedirectUrl: url={}, from={}", url, from);

		String redirectUrl = this.urlBuilder.buildRedirectUrl(request, url);

		// append "from".
		if (from != null) {
			try {
				String encoded = UriUtils.encodeQueryParam(from, "utf-8");
				redirectUrl += "?from=" + encoded;
			}
			catch (UnsupportedEncodingException e) {
				// NOTE: thrown only if "utf-8" is not supported.
				throw new RuntimeException(e);
			}
		}

		LOG.debug("Redirect URL: {}", redirectUrl);
		return redirectUrl;
	}

	private String generateFromUrl(@NonNull HttpServletRequest request) {
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
