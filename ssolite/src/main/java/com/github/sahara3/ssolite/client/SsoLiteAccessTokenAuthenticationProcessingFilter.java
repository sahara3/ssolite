package com.github.sahara3.ssolite.client;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

/**
 * Authentication processing filter for SSOLite client.
 *
 * @author sahara3
 */
public class SsoLiteAccessTokenAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

	/**
	 * @param filterProcessesUrl
	 *            the default filter processing URL.
	 */
	public SsoLiteAccessTokenAuthenticationProcessingFilter(String filterProcessesUrl) {
		super(filterProcessesUrl);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {

		String accessTokenId = request.getParameter("token");
		if (accessTokenId == null) {
			accessTokenId = "";
		}

		SsoLiteAccessTokenAuthenticationToken authRequest = new SsoLiteAccessTokenAuthenticationToken(accessTokenId);
		authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
		return this.getAuthenticationManager().authenticate(authRequest);
	}
}
