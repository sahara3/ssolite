package com.github.sahara3.ssolite.samples.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.github.sahara3.ssolite.ExternalAuthenticationEntryPoint;
import com.github.sahara3.ssolite.config.SsoLiteClientProperties;

/**
 * Sample web security configuration for SSOLite client.
 *
 * @author sahara3
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private SsoLiteClientProperties ssoLiteLoginClientProperties;

	@Override
	public void configure(HttpSecurity http) throws Exception {
		String loginUrl = this.ssoLiteLoginClientProperties.getLoginUrl();
		boolean sameDomain = this.ssoLiteLoginClientProperties.isSameDomain();

		AuthenticationEntryPoint entryPoint = new ExternalAuthenticationEntryPoint(loginUrl, sameDomain);

		// @formatter:off
		http
			.exceptionHandling()
				.authenticationEntryPoint(entryPoint)
				.and()
			.authorizeRequests()
				.antMatchers("/sso-login").permitAll()
				.anyRequest().authenticated();
		// @formatter:on
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/css/**", "/js/**", "/images/**", "/webjars/**");
	}
}
