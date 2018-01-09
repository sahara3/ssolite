package com.github.sahara3.ssolite.client;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

public class SsoLiteClientLoginConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

	private final SsoLiteAccessTokenAuthenticationProcessingFilter authenticationFilter;

	public SsoLiteClientLoginConfigurer(String filterProcessesUrl) {
		this.authenticationFilter = new SsoLiteAccessTokenAuthenticationProcessingFilter(filterProcessesUrl);
		this.successHandler = new SsoLiteClientAuthenticationSuccessHandler();
		this.failureHandler = new SimpleUrlAuthenticationFailureHandler();
	}

	private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;

	private AuthenticationSuccessHandler successHandler;

	private AuthenticationFailureHandler failureHandler;

	@Override
	public void init(HttpSecurity http) throws Exception {
		super.init(http);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		super.configure(http);

		this.authenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
		this.authenticationFilter.setAuthenticationSuccessHandler(this.successHandler);
		this.authenticationFilter.setAuthenticationFailureHandler(this.failureHandler);
		if (this.authenticationDetailsSource != null) {
			this.authenticationFilter.setAuthenticationDetailsSource(this.authenticationDetailsSource);
		}
		SessionAuthenticationStrategy sessionAuthenticationStrategy = http
				.getSharedObject(SessionAuthenticationStrategy.class);
		if (sessionAuthenticationStrategy != null) {
			this.authenticationFilter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
		}
		RememberMeServices rememberMeServices = http.getSharedObject(RememberMeServices.class);
		if (rememberMeServices != null) {
			this.authenticationFilter.setRememberMeServices(rememberMeServices);
		}

		SsoLiteAccessTokenAuthenticationProcessingFilter filter = postProcess(this.authenticationFilter);
		http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
	}
}
