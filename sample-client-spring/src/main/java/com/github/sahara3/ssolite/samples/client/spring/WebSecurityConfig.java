package com.github.sahara3.ssolite.samples.client.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.github.sahara3.ssolite.client.ExternalAuthenticationEntryPoint;
import com.github.sahara3.ssolite.client.SsoLiteAccessTokenAuthenticationProvider;
import com.github.sahara3.ssolite.client.SsoLiteClientLoginConfigurer;
import com.github.sahara3.ssolite.client.SsoLiteClientProperties;

/**
 * Sample web security configuration for SSOLite client.
 *
 * @author sahara3
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private RestTemplateBuilder restTemplateBuilder;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private SsoLiteClientProperties ssoLiteClientProperties;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// entry point for authentication failure.
		AuthenticationEntryPoint entryPoint = new ExternalAuthenticationEntryPoint(
				this.ssoLiteClientProperties.getLoginUrl(), this.ssoLiteClientProperties.isSameDomain());

		// @formatter:off
		http
			.exceptionHandling()
				.authenticationEntryPoint(entryPoint)
				.and()
			.authorizeRequests()
				.antMatchers("/login", "/sso-login").permitAll()
				.anyRequest().authenticated()
				.and()
			.formLogin()
				.loginPage("/login")
				.permitAll();
		// @formatter:on

		http.apply(new SsoLiteClientLoginConfigurer("/sso-login"));
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// SSOLite authentication provider.
		SsoLiteAccessTokenAuthenticationProvider ssoProvider = new SsoLiteAccessTokenAuthenticationProvider(
				this.ssoLiteClientProperties.getTokenApiUrl(), this.restTemplateBuilder.build(),
				this.userDetailsService);
		auth.authenticationProvider(ssoProvider);

		// local authentication provider.
		DaoAuthenticationProvider localProvider = new DaoAuthenticationProvider();
		localProvider.setUserDetailsService(this.userDetailsService);
		auth.authenticationProvider(localProvider);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/css/**", "/js/**", "/images/**", "/webjars/**");
	}
}