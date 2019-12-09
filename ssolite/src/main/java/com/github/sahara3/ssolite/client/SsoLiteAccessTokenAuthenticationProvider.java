package com.github.sahara3.ssolite.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import com.github.sahara3.ssolite.model.SsoLiteAccessToken;

/**
 * Authentication provider for SSOLite client.
 *
 * @author sahara3
 *
 */
public class SsoLiteAccessTokenAuthenticationProvider implements AuthenticationProvider {

	private static final Logger LOG = LoggerFactory.getLogger(SsoLiteAccessTokenAuthenticationProvider.class);

	@Override
	public boolean supports(Class<?> authentication) {
		return SsoLiteAccessTokenAuthenticationToken.class.isAssignableFrom(authentication);
	}

	private final String accessTokenApiUrl;

	public String getAccessTokenApiUrl() {
		return this.accessTokenApiUrl;
	}

	private final RestTemplate restTemplate;

	protected RestTemplate getRestTemplate() {
		return this.restTemplate;
	}

	private final UserDetailsService userDetailsService;

	protected UserDetailsService getUserDetailsService() {
		return this.userDetailsService;
	}

	/**
	 * @param accessTokenApiUrl
	 *            the URL of the access token API.
	 * @param restTemplate
	 *            REST template object to request to the access token API.
	 * @param userDetailsService
	 *            Your {@link UserDetailsService} implementation.
	 */
	public SsoLiteAccessTokenAuthenticationProvider(String accessTokenApiUrl, RestTemplate restTemplate,
			UserDetailsService userDetailsService) {
		Assert.notNull(accessTokenApiUrl, "accessTokenApiUrl cannot be null");
		Assert.notNull(restTemplate, "restTemplate cannot be null");
		Assert.notNull(userDetailsService, "userDetailsService cannot be null");

		this.accessTokenApiUrl = accessTokenApiUrl;
		this.restTemplate = restTemplate;
		this.userDetailsService = userDetailsService;
	}

	private UserDetailsChecker authenticationChecks = new DefaultAuthenticationChecks();

	protected UserDetailsChecker getAuthenticationChecks() {
		return this.authenticationChecks;
	}

	public void setAuthenticationChecks(UserDetailsChecker authenticationChecks) {
		Assert.notNull(authenticationChecks, "authenticationChecks cannot be null");
		this.authenticationChecks = authenticationChecks;
	}

	private boolean hideUserNotFoundExceptions = false;

	protected boolean isHideUserNotFoundExceptions() {
		return this.hideUserNotFoundExceptions;
	}

	public void setHideUserNotFoundExceptions(boolean hideUserNotFoundExceptions) {
		this.hideUserNotFoundExceptions = hideUserNotFoundExceptions;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		Assert.isInstanceOf(SsoLiteAccessTokenAuthenticationToken.class, authentication,
				"Only SsoLiteAccessTokenAuthenticationToken is supported");

		// determine an access token ID.
		String tokenId = authentication.getCredentials().toString();

		UserDetails user;
		try {
			user = this.retrieveUser(tokenId);
		}
		catch (UsernameNotFoundException e) {
			LOG.debug("User '{}' not found.", tokenId);

			if (this.isHideUserNotFoundExceptions()) {
				throw new BadCredentialsException("Bad credentials.");
			}
			throw e;
		}

		Assert.notNull(user, "retrieveUser returned null - a violation of the interface contract");

		this.getAuthenticationChecks().check(user);

		// create a new authentication object to return.
		return this.createSuccessAuthentication(authentication, user);
	}

	@SuppressWarnings("static-method")
	protected Authentication createSuccessAuthentication(Authentication authentication, UserDetails user) {
		SsoLiteAccessTokenAuthenticationToken result = new SsoLiteAccessTokenAuthenticationToken(user,
				authentication.getCredentials(), user.getAuthorities());
		result.setDetails(authentication.getDetails());
		return result;
	}

	protected final UserDetails retrieveUser(String accessTokenId) throws AuthenticationException {
		if (accessTokenId == null || accessTokenId.isEmpty()) {
			LOG.debug("Invalid access token ID: {}", accessTokenId);
			throw new BadCredentialsException("Bad credentials.");
		}

		// call RESTful API to retrieve the username in the access token.
		String url = this.getAccessTokenApiUrl() + "/" + accessTokenId;
		LOG.debug("GET {}", url);
		ResponseEntity<SsoLiteAccessToken> response = this.restTemplate.getForEntity(url, SsoLiteAccessToken.class);
		LOG.debug("Token API response: {}", response);

		if (!response.getStatusCode().is2xxSuccessful()) {
			LOG.debug("Access token is not found, or HTTP error.");
			throw new BadCredentialsException("Bad credentials.");
		}

		String username = response.getBody().getUsername();

		// retrieve the local user details object via UserDetailsService.
		UserDetails loadedUser;
		try {
			loadedUser = this.getUserDetailsService().loadUserByUsername(username);
		}
		catch (UsernameNotFoundException e) {
			throw e;
		}
		catch (Exception e) {
			throw new InternalAuthenticationServiceException(e.getMessage(), e);
		}

		if (loadedUser == null) {
			throw new InternalAuthenticationServiceException(
					"UserDetailsService returned null, which is an interface contract violation.");
		}
		return loadedUser;
	}

	protected static class DefaultAuthenticationChecks implements UserDetailsChecker {
		@Override
		public void check(UserDetails user) {
			if (!user.isAccountNonLocked()) {
				throw new LockedException("User account is locked.");
			}
			if (!user.isEnabled()) {
				throw new DisabledException("User is disabled.");
			}
			if (!user.isAccountNonExpired()) {
				throw new AccountExpiredException("User account has expired.");
			}
			if (!user.isCredentialsNonExpired()) {
				throw new CredentialsExpiredException("User credentials have expired.");
			}
		}
	}
}
