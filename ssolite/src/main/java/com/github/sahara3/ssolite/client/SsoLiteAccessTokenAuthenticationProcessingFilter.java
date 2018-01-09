package com.github.sahara3.ssolite.client;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

public class SsoLiteAccessTokenAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

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
