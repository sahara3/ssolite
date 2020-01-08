package com.github.sahara3.ssolite.client;

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
import org.springframework.util.Assert;

/**
 * Configures SSOLite client authentication.
 *
 * @author sahara3
 */
public class SsoLiteClientLoginConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final SsoLiteAccessTokenAuthenticationProcessingFilter authenticationFilter;

    /**
     * Creates a new SSOLIte client authentication configurator.
     *
     * @param filterProcessesUrl the URL where a {@link SsoLiteAccessTokenAuthenticationProcessingFilter} works.
     */
    public SsoLiteClientLoginConfigurer(String filterProcessesUrl) {
        Assert.notNull(filterProcessesUrl, "filterProcessesUrl cannot be null");
        this.authenticationFilter = new SsoLiteAccessTokenAuthenticationProcessingFilter(filterProcessesUrl);
        this.successHandler = new SsoLiteClientAuthenticationSuccessHandler();
        this.failureHandler = new SimpleUrlAuthenticationFailureHandler();
    }

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
        SessionAuthenticationStrategy sessionAuthenticationStrategy =
                http.getSharedObject(SessionAuthenticationStrategy.class);
        if (sessionAuthenticationStrategy != null) {
            this.authenticationFilter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        }
        RememberMeServices rememberMeServices = http.getSharedObject(RememberMeServices.class);
        if (rememberMeServices != null) {
            this.authenticationFilter.setRememberMeServices(rememberMeServices);
        }

        SsoLiteAccessTokenAuthenticationProcessingFilter filter = this.postProcess(this.authenticationFilter);
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }
}
