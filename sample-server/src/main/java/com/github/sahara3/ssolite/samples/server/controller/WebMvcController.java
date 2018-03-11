package com.github.sahara3.ssolite.samples.server.controller;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.github.sahara3.ssolite.config.SsoLiteServerProperties;
import com.github.sahara3.ssolite.server.service.SsoLiteServerRedirectResolver;

import lombok.RequiredArgsConstructor;

/**
 * SSOLite server controller sample.
 *
 * In the login page, you should handle {@code from} parameter in the query
 * string.
 *
 * @author sahara3
 */
@Controller
@RequiredArgsConstructor
public class WebMvcController {

	private static final Logger LOG = LoggerFactory.getLogger(WebMvcController.class);

	@NotNull
	private final SsoLiteServerProperties ssoLiteServerProperties;

	@NotNull
	private final SsoLiteServerRedirectResolver redirectResolver;

	/**
	 * Login page.
	 *
	 * If an user has logged in, this method returns a redirect URL for the top
	 * page.
	 *
	 * @param request
	 *            must not be null.
	 * @param authentication
	 *            not null for logged in users.
	 * @param model
	 *            must not be null.
	 * @return &quot;login&quot; for the login page, or redirect URL.
	 */
	@GetMapping(path = "/login")
	public String login(HttpServletRequest request, Authentication authentication, Model model) {
		String from = request.getParameter("from");

		if (authentication != null && authentication.isAuthenticated() && from != null) {
			String next = this.redirectResolver.getRedirectDestination(from, authentication);
			LOG.debug("Already logged in. Redirect to {}", next);
			return "redirect:" + next;
		}

		// not logged in.
		Arrays.asList("error", "logout").forEach(key -> model.addAttribute(key, request.getParameter(key)));
		model.addAttribute("from", from);
		return "login";
	}

	@GetMapping(path = "/")
	@SuppressWarnings("static-method")
	public String index() {
		return "index";
	}

	@GetMapping(path = "/page")
	@SuppressWarnings("static-method")
	public String page() {
		return "page";
	}
}
