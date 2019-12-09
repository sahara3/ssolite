package com.github.sahara3.ssolite.server;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;
import org.springframework.web.util.UriUtils;

/**
 * Authentication failure handler for SSOLite server.
 *
 * @author sahara3
 */
public class SsoLiteServerAuthenticationFailureHandler implements AuthenticationFailureHandler {

	protected final Log logger = LogFactory.getLog(this.getClass());

	private final String forwardUrl;

	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	/**
	 * @param forwardUrl
	 *            URL to forward when login failure.
	 */
	public SsoLiteServerAuthenticationFailureHandler(String forwardUrl) {
		Assert.isTrue(UrlUtils.isValidRedirectUrl(forwardUrl), "'" + forwardUrl + "' is not a valid forward URL");
		this.forwardUrl = forwardUrl;
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		String url = this.forwardUrl;

		String from = request.getParameter("from"); // can be null.
		this.logger.debug("onAuthenticationSuccess: from=" + from);

		if (from != null) {
			String query = "from=" + UriUtils.encodeQueryParam(from, StandardCharsets.UTF_8);
			if (this.forwardUrl.contains("?")) {
				if (this.forwardUrl.endsWith("?")) {
					url += query;
				}
				else {
					url += "&" + query;
				}
			}
			else {
				url += "?" + query;
			}
		}

		request.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, exception);

		this.logger.debug("Redirect URL: " + url);
		Assert.notNull(url, "Redirect URL cannot be null.");
		this.redirectStrategy.sendRedirect(request, response, url);
	}

}
