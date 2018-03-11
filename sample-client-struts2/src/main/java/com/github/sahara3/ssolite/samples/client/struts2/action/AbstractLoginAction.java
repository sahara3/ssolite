package com.github.sahara3.ssolite.samples.client.struts2.action;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.github.sahara3.ssolite.samples.client.struts2.model.AuthToken;
import com.github.sahara3.ssolite.samples.client.struts2.service.AuthException;
import com.github.sahara3.ssolite.samples.client.struts2.service.AuthManager;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractLoginAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {

	private static final long serialVersionUID = 1L;

	@Getter(AccessLevel.PROTECTED)
	@Setter
	private HttpServletRequest servletRequest;

	@Getter(AccessLevel.PROTECTED)
	@Setter
	private HttpServletResponse servletResponse;

	@Getter
	private String redirectUrl;

	@Getter(AccessLevel.PROTECTED)
	private final AuthManager authManager = AuthManager.getInstance();

	@Override
	public String execute() {
		LOG.debug("Executing...");

		AuthToken token;
		try {
			token = this.authenticate();
		}
		catch (AuthException e) {
			LOG.debug("Failed to authenticate.", e);
			return Action.ERROR;
		}

		// renew a session.
		HttpSession session = this.authManager.renewSession(token, this.servletRequest);
		assert session != null;

		// send the new session ID via Cookie.
		Cookie cookie = new Cookie("SESSIONID", session.getId());
		cookie.setHttpOnly(true);
		this.servletResponse.addCookie(cookie);

		// redirect.
		this.redirectUrl = this.determineRedirectUrl(session);
		if (this.redirectUrl == null) {
			LOG.debug("redirect to default.");
			return "redirect.default";
		}

		LOG.debug("redirect to {}", this.redirectUrl);
		return "redirect";
	}

	@NotNull
	protected abstract AuthToken authenticate() throws AuthException;

	protected abstract String determineRedirectUrl(HttpSession session);
}
