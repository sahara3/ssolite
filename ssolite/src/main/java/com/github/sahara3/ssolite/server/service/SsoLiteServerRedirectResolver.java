package com.github.sahara3.ssolite.server.service;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service
@RequiredArgsConstructor
public class SsoLiteServerRedirectResolver {

	private static Logger LOG = LoggerFactory.getLogger(SsoLiteServerRedirectResolver.class);

	@NotNull
	protected final SsoLiteAccessTokenService tokenService;

	@NotNull
	@Setter
	protected Map<URI, URI> permittedDomainMap = new HashMap<>();

	public String getRedirectDestination(@NonNull String from, @NonNull Authentication authentication) {
		if (!UrlUtils.isAbsoluteUrl(from)) {
			return from;
		}

		URI destination = this.getSsoRedirectDestinationUri(from);
		if (destination == null) {
			// FIXME: runtime error is collect?
			throw new RuntimeException("Not permitted domain: " + from);
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
		try {
			return UriUtils.encodeQueryParam(value, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			// wrap the exception.
			throw new RuntimeException(e);
		}
	}
}
