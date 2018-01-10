package com.github.sahara3.ssolite.samples.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.bind.annotation.RequestMethod;

import com.github.sahara3.ssolite.server.SsoLiteServerAuthenticationSuccessHandler;
import com.github.sahara3.ssolite.server.SsoLiteServerProperties;
import com.github.sahara3.ssolite.server.service.SsoLiteServerRedirectResolver;

/**
 * Web security configuration for SSOLite server.
 *
 * @author sahara3
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	/**
	 * Security configuration for RESTful API of SSO access token.
	 *
	 * @author sahara3
	 */
	@Configuration
	@Order(1)
	public static class SsoLiteAccessTokenApiSecurityConfig extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			// @formatter:off
			http.antMatcher("/api/tokens/**") // FIXME: Is it OK to permit all?
				.sessionManagement()
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
					.and()
				.csrf().disable()
				.authorizeRequests()
					.anyRequest().permitAll();
			// @formatter:on
		}
	}

	/**
	 * Security configuration for the form login.
	 *
	 * @author sahara3
	 */
	@Configuration
	@Order(2)
	public static class FormLoginSecurityConfig extends WebSecurityConfigurerAdapter {
		@Autowired
		private SsoLiteServerProperties ssoServerProperties;

		@Autowired
		private SsoLiteServerRedirectResolver redirectResolver;

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			SsoLiteServerAuthenticationSuccessHandler successHandler;
			successHandler = new SsoLiteServerAuthenticationSuccessHandler(this.redirectResolver);
			successHandler.setDefaultTopPageUrl(this.ssoServerProperties.getDefaultTopPageUrl());
			successHandler.setPermittedDomainMap(this.ssoServerProperties.getPermittedDomainMap());

			// @formatter:off
			http.authorizeRequests()
				.regexMatchers("/login", "/login\\?(logout|error|from=.*)").permitAll()
				.antMatchers("/**").authenticated()
				.anyRequest().authenticated()
				.and()
			.formLogin()
				.loginPage("/login")
				.loginProcessingUrl("/login")
				.successHandler(successHandler)
				.permitAll()
				.and()
			.logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout", RequestMethod.GET.name()))
				.logoutSuccessUrl("/login?logout")
				.clearAuthentication(true)
				.invalidateHttpSession(true);
			// @formatter:on
		}

		@Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring().antMatchers("/css/**", "/js/**", "/images/**", "/webjars/**");
		}

		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			// TODO: implement as a bean. (UserDetailsService?) and use DB.
			auth.inMemoryAuthentication().withUser("admin").password("admin").roles("ADMIN");
		}
	}
}
