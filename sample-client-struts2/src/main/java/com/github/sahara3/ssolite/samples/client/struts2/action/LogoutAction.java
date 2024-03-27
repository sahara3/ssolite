package com.github.sahara3.ssolite.samples.client.struts2.action;

import java.io.Serial;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.opensymphony.xwork2.ActionSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.struts2.action.ServletRequestAware;

import com.github.sahara3.ssolite.samples.client.struts2.service.AuthManager;

@Slf4j
public class LogoutAction extends ActionSupport implements ServletRequestAware {

    @Serial
    private static final long serialVersionUID = 1L;

    private HttpServletRequest servletRequest;

    @Override
    public void withServletRequest(HttpServletRequest request) {
        this.servletRequest = request;
    }

    AuthManager authenticationManager = AuthManager.getInstance();

    @Override
    public String execute() {
        LOG.debug("Executing...");

        HttpSession session = this.servletRequest.getSession(false);
        this.authenticationManager.removeAuthenticationToken(session);

        return SUCCESS;
    }
}
