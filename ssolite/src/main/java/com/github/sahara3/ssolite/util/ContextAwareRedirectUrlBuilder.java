package com.github.sahara3.ssolite.util;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.PortResolver;
import org.springframework.security.web.PortResolverImpl;
import org.springframework.security.web.util.RedirectUrlBuilder;
import org.springframework.security.web.util.UrlUtils;

import lombok.NonNull;

public class ContextAwareRedirectUrlBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(ContextAwareRedirectUrlBuilder.class);

	private final PortResolver portResolver = new PortResolverImpl();

	public String buildRedirectUrl(@NonNull HttpServletRequest request, @NonNull String url) {
		if (UrlUtils.isAbsoluteUrl(url)) {
			LOG.debug("URL is absolute: {}", url);
			return url;
		}

		boolean internal = false;
		String path = url;
		if (url.startsWith("internal:")) {
			path = url.substring("internal:".length());
			internal = true;
		}

		RedirectUrlBuilder builder = new RedirectUrlBuilder();
		builder.setScheme(request.getScheme());
		builder.setServerName(request.getServerName());
		builder.setPort(this.portResolver.getServerPort(request));
		if (internal) {
			builder.setContextPath(request.getContextPath());
		}
		builder.setPathInfo(path);

		String redirectUrl = builder.getUrl();
		LOG.debug("Redirect URL: {}", redirectUrl);
		return redirectUrl;
	}
}
