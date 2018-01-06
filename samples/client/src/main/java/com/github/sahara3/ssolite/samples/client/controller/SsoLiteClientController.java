package com.github.sahara3.ssolite.samples.client.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import com.github.sahara3.ssolite.config.SsoLiteClientProperties;
import com.github.sahara3.ssolite.model.SsoLiteAccessToken;

import lombok.RequiredArgsConstructor;

/**
 * SSOLite client controller sample.
 *
 * For SSOLite, you should create an end point that queries an access token and
 * processes SSO login.
 *
 * @author sahara3
 */
@Controller
@RequiredArgsConstructor
public class SsoLiteClientController {

	private static final Logger LOG = LoggerFactory.getLogger(SsoLiteClientController.class);

	@NotNull
	private final SsoLiteClientProperties ssoLiteClientProperties;

	@NotNull
	private final RestTemplate restTemplate;

	/**
	 * SSO login redirect.
	 *
	 * {@code token} and {@code next} parameters must be specified in the query
	 * string.
	 *
	 * @param request
	 *            must not be null.
	 * @return the redirect page URL.
	 */
	@GetMapping(path = "/sso-login")
	public String ssoLogin(HttpServletRequest request) {
		String tokenId = request.getParameter("token");
		String next = request.getParameter("next");

		if (tokenId != null && next != null) {
			String url = this.ssoLiteClientProperties.getTokenApiUrl() + "/" + tokenId;
			LOG.debug("GET {}", url);
			ResponseEntity<SsoLiteAccessToken> response = this.restTemplate.getForEntity(url, SsoLiteAccessToken.class);
			LOG.debug("Token API response: {}", response);
			if (response.getStatusCode().is2xxSuccessful()) {
				request.getSession(true); // ensure a session.
				request.changeSessionId();
				// FIXME: authenticate and authorize the session.

				return "redirect:" + next;
			}
			// fall through.
		}

		// redirect to the login page.
		String login = this.ssoLiteClientProperties.getLoginUrl();
		if (next != null) {
			login += "?from=" + next;
		}
		return "redirect:" + login;
	}
}
