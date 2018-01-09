package com.github.sahara3.ssolite.client;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class SsoLiteAccessTokenAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = 1L;

	private final Object principal;

	@Override
	public Object getPrincipal() {
		return this.principal;
	}

	private Object credentials;

	@Override
	public Object getCredentials() {
		return this.credentials;
	}

	public SsoLiteAccessTokenAuthenticationToken(String accessTokenId) {
		super(null);
		this.principal = null;
		this.credentials = accessTokenId;
		super.setAuthenticated(false);
	}

	public SsoLiteAccessTokenAuthenticationToken(Object principal, Object credentials,
			Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
		this.credentials = credentials;
		super.setAuthenticated(true);
	}
}
