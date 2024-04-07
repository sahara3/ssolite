package com.github.sahara3.ssolite.spring.client;

import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.util.Assert;

/**
 * Configures SSOLite client authentication.
 *
 * @author sahara3
 */
public class SsoLiteClientLoginConfigurer<H extends HttpSecurityBuilder<H>> extends
        AbstractHttpConfigurer<SsoLiteClientLoginConfigurer<H>, H> {

    /**
     * Creates a new SSOLite client authentication configurator.
     */
    public SsoLiteClientLoginConfigurer() {
        // no-op.
    }

    /**
     * Creates a new SSOLite client authentication configurator.
     *
     * @param filterProcessesUrl the URL where a {@link SsoLiteAccessTokenAuthenticationProcessingFilter} works.
     */
    public SsoLiteClientLoginConfigurer(String filterProcessesUrl) {
        Assert.notNull(filterProcessesUrl, "filterProcessesUrl cannot be null");
        this.filterProcessesUrl = filterProcessesUrl;
    }

    private String filterProcessesUrl = "/sso-login";

    public SsoLiteClientLoginConfigurer<H> filterProcessesUrl(String filterProcessesUrl) {
        Assert.notNull(filterProcessesUrl, "filterProcessesUrl cannot be null");
        this.filterProcessesUrl = filterProcessesUrl;
        return this;
    }

    @Nullable
    private AuthenticationSuccessHandler successHandler;

    @Nullable
    private AuthenticationFailureHandler failureHandler;

    @Override
    public void init(H http) throws Exception {
        super.init(http);
    }

    @Override
    public void configure(H http) throws Exception {
        super.configure(http);

        if (this.successHandler == null) {
            this.successHandler = new SsoLiteClientAuthenticationSuccessHandler();
        }
        if (this.failureHandler == null) {
            this.failureHandler = new SimpleUrlAuthenticationFailureHandler();
        }

        SsoLiteAccessTokenAuthenticationProcessingFilter authenticationFilter =
                new SsoLiteAccessTokenAuthenticationProcessingFilter(filterProcessesUrl);

        authenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        authenticationFilter.setAuthenticationSuccessHandler(this.successHandler);
        authenticationFilter.setAuthenticationFailureHandler(this.failureHandler);
        SessionAuthenticationStrategy sessionAuthenticationStrategy =
                http.getSharedObject(SessionAuthenticationStrategy.class);
        if (sessionAuthenticationStrategy != null) {
            authenticationFilter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        }
        RememberMeServices rememberMeServices = http.getSharedObject(RememberMeServices.class);
        if (rememberMeServices != null) {
            authenticationFilter.setRememberMeServices(rememberMeServices);
        }

        SecurityContextRepository securityContextRepository = http.getSharedObject(SecurityContextRepository.class);
        if (securityContextRepository != null) {
            authenticationFilter.setSecurityContextRepository(securityContextRepository);
        }

        authenticationFilter.setSecurityContextHolderStrategy(getSecurityContextHolderStrategy());

        SsoLiteAccessTokenAuthenticationProcessingFilter filter = this.postProcess(authenticationFilter);
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }
}
