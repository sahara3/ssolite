package com.github.sahara3.ssolite.samples.client.struts2.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.struts2.ServletActionContext;

import com.github.sahara3.ssolite.samples.client.struts2.model.AuthToken;
import com.github.sahara3.ssolite.samples.client.struts2.service.AuthManager;

@Slf4j
public class AuthInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = 1L;

    private static final String SESSION_KEY_SAVEDREQUEST = "my-saved-request";

    private final AuthManager authenticationManager = AuthManager.getInstance();

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        LOG.trace("Intercepting.");
        HttpServletRequest request = ServletActionContext.getRequest();

        // get or create a new session to record the request URL.
        HttpSession session = request.getSession(true);

        // record access URI into the session.
        setRequestURL(request, session);

        // check whether logged in status.
        AuthToken token = this.authenticationManager.getAuthenticationToken(session);
        if (token == null) {
            LOG.trace("Need login.");
            return "need.login";
        }

        // user has logged in.
        LOG.trace("User has been logged in.");
        return invocation.invoke();
    }

    public static String getSavedRequestURL(HttpSession session) {
        if (session == null) {
            return null;
        }

        Object saved = session.getAttribute(SESSION_KEY_SAVEDREQUEST);
        return saved instanceof String ? (String) saved : null;
    }

    protected static void setRequestURL(@NonNull HttpServletRequest request, @NonNull HttpSession session) {
        if (!"GET".equals(request.getMethod())) {
            return;
        }

        String uri = request.getRequestURI();
        String query = request.getQueryString();
        if (query != null && !query.isEmpty()) {
            uri += "?" + query;
        }

        session.setAttribute(SESSION_KEY_SAVEDREQUEST, uri);
    }
}
