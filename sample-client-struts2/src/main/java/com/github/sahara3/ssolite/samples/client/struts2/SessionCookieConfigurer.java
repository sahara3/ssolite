package com.github.sahara3.ssolite.samples.client.struts2;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.SessionCookieConfig;
import javax.servlet.annotation.WebListener;

import lombok.extern.slf4j.Slf4j;

@WebListener
@Slf4j
public class SessionCookieConfigurer implements ServletContextListener {

	public static final String COOKIE_NAME = "SESSIONID";

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		LOG.debug("Context initialized: event = {}", sce);
		SessionCookieConfig cookieConfig = sce.getServletContext().getSessionCookieConfig();
		cookieConfig.setName(COOKIE_NAME);
		cookieConfig.setHttpOnly(true);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		LOG.debug("Context destroyed: event = {}", sce);
		// nothing to do.
	}
}
