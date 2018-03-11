package com.github.sahara3.ssolite.samples.client.struts2.action;

import javax.servlet.http.HttpSession;

import com.github.sahara3.ssolite.samples.client.struts2.interceptor.AuthInterceptor;
import com.github.sahara3.ssolite.samples.client.struts2.model.AuthToken;
import com.github.sahara3.ssolite.samples.client.struts2.service.AuthException;

import lombok.Setter;

public class LoginAction extends AbstractLoginAction {

	private static final long serialVersionUID = 1L;

	public String show() {
		HttpSession session = this.getServletRequest().getSession(false);
		if (this.getAuthManager().getAuthenticationToken(session) != null) {
			return "redirect.default";
		}

		return SUCCESS;
	}

	@Setter
	private String username;

	@Setter
	private String password;

	@Override
	protected AuthToken authenticate() throws AuthException {
		return this.getAuthManager().authenticate(this.getServletRequest(), this.username, this.password);
	}

	@Override
	protected String determineRedirectUrl(HttpSession session) {
		return AuthInterceptor.getSavedRequestURL(session);
	}
}
