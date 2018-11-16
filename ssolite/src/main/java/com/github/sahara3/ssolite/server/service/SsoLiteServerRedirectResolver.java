package com.github.sahara3.ssolite.server.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.util.UriUtils;

import com.github.sahara3.ssolite.model.SsoLiteAccessToken;
import com.github.sahara3.ssolite.util.SsoLiteUriUtils;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * SSOLite redirect resolver.
 *
 * This resolves and returns the redirect URL after login.
 *
 * @author sahara3
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SsoLiteServerRedirectResolver {

	@NotNull
	protected final SsoLiteAccessTokenService tokenService;

	@NotNull
	@Setter
	protected Map<URI, URI> permittedDomainMap = new HashMap<>();

	/**
	 * Resolves and returns the redirect URL after login.
	 *
	 * @param from
	 *            the URL where an user came from before login.
	 * @param authentication
	 *            the authentication token when logged in.
	 * @return the redirect URL.
	 * @throws AccessDeniedException
	 *             thrown if the domain where the request came from is not
	 *             permitted.
	 */
	public String getRedirectDestination(@NonNull String from, @NonNull Authentication authentication)
			throws AccessDeniedException {
		if (!UrlUtils.isAbsoluteUrl(from)) {
			return from;
		}

		URI destination = this.getSsoRedirectDestinationUri(from);
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
		builder.append("token=").append(encodeQueryParam(token.getId()));

		// next URL after SSO processing.
		builder.append("&next=").append(encodeQueryParam(from));

		// generate redirect URL.
		return builder.toString();
	}

	protected URI getSsoRedirectDestinationUri(@NonNull String from) {
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

	private static String encodeQueryParam(@NonNull String value) {
		return UriUtils.encodeQueryParam(value, StandardCharsets.UTF_8);
	}
}
