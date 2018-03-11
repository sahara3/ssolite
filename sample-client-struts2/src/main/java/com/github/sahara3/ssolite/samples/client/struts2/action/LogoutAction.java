package com.github.sahara3.ssolite.samples.client.struts2.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.github.sahara3.ssolite.samples.client.struts2.service.AuthManager;
import com.opensymphony.xwork2.ActionSupport;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogoutAction extends ActionSupport implements ServletRequestAware {

	private static final long serialVersionUID = 1L;

	@Setter
	private HttpServletRequest servletRequest;

	AuthManager authenticationManager = AuthManager.getInstance();

	@Override
	public String execute() {
		LOG.debug("Executing...");

		HttpSession session = this.servletRequest.getSession(false);
		this.authenticationManager.removeAuthenticationToken(session);

		return SUCCESS;
	}
}
