package com.github.sahara3.ssolite.samples.client.struts2.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.github.sahara3.ssolite.samples.client.struts2.model.AuthToken;
import com.github.sahara3.ssolite.samples.client.struts2.model.LocalUser;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthManager {

	private static final AuthManager instance = new AuthManager();

	public static AuthManager getInstance() {
		return instance;
	}

	public static final String SESSION_KEY_AUTHTOKEN = "my-auth-token";

	private LocalUserService userService = LocalUserService.getInstance();

	public AuthToken authenticate(@NonNull HttpServletRequest request, String username, String password)
			throws AuthException {
		LocalUser user = this.userService.findByName(username);
		if (password == null || !password.equals(user.getPassword())) {
			throw new BadPasswordException("Bad password.");
		}

		return new AuthToken(username, "[SECRET]", true);
	}

	public HttpSession renewSession(@NonNull AuthToken token, @NonNull HttpServletRequest request) {
		HttpSession session = request.getSession(false);

		// save attributes in the current session.
		Map<String, Object> attributes = new HashMap<>();
		if (session != null) {
			LOG.debug("Old session: {}", session.getId());
			for (String name : Collections.list(session.getAttributeNames())) {
				attributes.put(name, session.getAttribute(name));
			}
			session.invalidate();
		}

		// renew the session.
		session = request.getSession(true); // new session.
		LOG.debug("New session: {}", session.getId());
		for (Entry<String, Object> attribute : attributes.entrySet()) {
			session.setAttribute(attribute.getKey(), attribute.getValue());
		}

		this.setAuthenticationToken(session, token);
		return session;
	}

	@SuppressWarnings("static-method")
	public AuthToken getAuthenticationToken(HttpSession session) {
		if (session == null) {
			return null;
		}
		Object token = session.getAttribute(SESSION_KEY_AUTHTOKEN);
		if (token == null || !(token instanceof AuthToken)) {
			return null;
		}
		return (AuthToken) token;
	}

	@SuppressWarnings("static-method")
	protected void setAuthenticationToken(@NonNull HttpSession session, AuthToken token) {
		session.setAttribute(SESSION_KEY_AUTHTOKEN, token);
	}

	@SuppressWarnings("static-method")
	public void removeAuthenticationToken(HttpSession session) {
		if (session != null) {
			session.removeAttribute(SESSION_KEY_AUTHTOKEN);
		}
	}
}
