package com.github.sahara3.ssolite.spring.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

import com.github.sahara3.ssolite.core.client.SsoLiteAccessTokenApiClient;
import com.github.sahara3.ssolite.core.client.SsoLiteAccessTokenApiException;
import com.github.sahara3.ssolite.core.model.SsoLiteAccessToken;

/**
 * Authentication provider for SSOLite client.
 *
 * @author sahara3
 */
public class SsoLiteAccessTokenAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(SsoLiteAccessTokenAuthenticationProvider.class);

    @Override
    public boolean supports(Class<?> authentication) {
        return SsoLiteAccessTokenAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private final String accessTokenApiUrl;

    public String getAccessTokenApiUrl() {
        return this.accessTokenApiUrl;
    }

    private final SsoLiteAccessTokenApiClient accessTokenApiClient;

    protected SsoLiteAccessTokenApiClient getAccessTokenApiClient() {
        return this.accessTokenApiClient;
    }

    private final UserDetailsService userDetailsService;

    protected UserDetailsService getUserDetailsService() {
        return this.userDetailsService;
    }

    /**
     * @param accessTokenApiUrl    the URL of the access token API.
     * @param accessTokenApiClient RESTful API client object to request to the access token API.
     * @param userDetailsService   Your {@link UserDetailsService} implementation.
     */
    public SsoLiteAccessTokenAuthenticationProvider(String accessTokenApiUrl,
            SsoLiteAccessTokenApiClient accessTokenApiClient, UserDetailsService userDetailsService) {
        Assert.notNull(accessTokenApiUrl, "accessTokenApiUrl cannot be null");
        Assert.notNull(accessTokenApiClient, "accessTokenApiClient cannot be null");
        Assert.notNull(userDetailsService, "userDetailsService cannot be null");

        this.accessTokenApiUrl = accessTokenApiUrl;
        this.accessTokenApiClient = accessTokenApiClient;
        this.userDetailsService = userDetailsService;
    }

    private UserDetailsChecker authenticationChecks = new DefaultAuthenticationChecks();

    protected UserDetailsChecker getAuthenticationChecks() {
        return this.authenticationChecks;
    }

    public void setAuthenticationChecks(UserDetailsChecker authenticationChecks) {
        Assert.notNull(authenticationChecks, "authenticationChecks cannot be null");
        this.authenticationChecks = authenticationChecks;
    }

    private boolean hideUserNotFoundExceptions = false;

    protected boolean isHideUserNotFoundExceptions() {
        return this.hideUserNotFoundExceptions;
    }

    public void setHideUserNotFoundExceptions(boolean hideUserNotFoundExceptions) {
        this.hideUserNotFoundExceptions = hideUserNotFoundExceptions;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(SsoLiteAccessTokenAuthenticationToken.class, authentication,
                "Only SsoLiteAccessTokenAuthenticationToken is supported");

        // determine an access token ID.
        String tokenId = authentication.getCredentials().toString();

        UserDetails user;
        try {
            user = this.retrieveUser(tokenId);
        }
        catch (UsernameNotFoundException e) {
            LOG.debug("User '{}' not found.", tokenId);

            if (this.isHideUserNotFoundExceptions()) {
                throw new BadCredentialsException("Bad credentials.");
            }
            throw e;
        }

        Assert.notNull(user, "retrieveUser returned null - a violation of the interface contract");

        this.getAuthenticationChecks().check(user);

        // create a new authentication object to return.
        return this.createSuccessAuthentication(authentication, user);
    }

    protected Authentication createSuccessAuthentication(Authentication authentication, UserDetails user) {
        SsoLiteAccessTokenAuthenticationToken result =
                new SsoLiteAccessTokenAuthenticationToken(user, authentication.getCredentials(), user.getAuthorities());
        result.setDetails(authentication.getDetails());
        return result;
    }

    protected final UserDetails retrieveUser(String accessTokenId) throws AuthenticationException {
        if (accessTokenId.isEmpty()) {
            LOG.debug("Invalid access token ID: {}", accessTokenId);
            throw new BadCredentialsException("Bad credentials.");
        }

        // call RESTful API to retrieve the username in the access token.
        String url = this.getAccessTokenApiUrl() + "/" + accessTokenId;
        SsoLiteAccessToken token = null;
        try {
            token = this.getAccessTokenApiClient().retriveAccessToken(url);
        }
        catch (SsoLiteAccessTokenApiException e) {
            LOG.debug("Access token API error.", e);
            // fall through.
        }
        if (token == null) {
            throw new BadCredentialsException("Bad credentials.");
        }

        // retrieve the local user details object via UserDetailsService.
        UserDetails loadedUser;
        try {
            loadedUser = this.getUserDetailsService().loadUserByUsername(token.getUsername());
        }
        catch (UsernameNotFoundException e) {
            throw e;
        }
        catch (Exception e) {
            throw new InternalAuthenticationServiceException(e.getMessage(), e);
        }

        if (loadedUser == null) {
            throw new InternalAuthenticationServiceException(
                    "UserDetailsService returned null, which is an interface contract violation.");
        }
        return loadedUser;
    }

    /**
     * Default implementation of {@link UserDetailsChecker}.
     *
     * @author sahara3
     */
    protected static class DefaultAuthenticationChecks implements UserDetailsChecker {
        @Override
        public void check(UserDetails user) {
            if (!user.isAccountNonLocked()) {
                throw new LockedException("User account is locked.");
            }
            if (!user.isEnabled()) {
                throw new DisabledException("User is disabled.");
            }
            if (!user.isAccountNonExpired()) {
                throw new AccountExpiredException("User account has expired.");
            }
            if (!user.isCredentialsNonExpired()) {
                throw new CredentialsExpiredException("User credentials have expired.");
            }
        }
    }
}
