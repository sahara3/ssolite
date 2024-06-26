package com.github.sahara3.ssolite.spring.client;

import java.io.Serial;
import java.util.Collection;

import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * Authentication token used in SSOLite client authentication.
 *
 * @author sahara3
 */
public class SsoLiteAccessTokenAuthenticationToken extends AbstractAuthenticationToken {

    @Serial
    private static final long serialVersionUID = 1L;

    @Nullable
    private final Object principal;

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Nullable
    private Object credentials;

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    /**
     * Creates a new authentication token.
     *
     * @param accessTokenId the access token ID to use in authentication.
     */
    public SsoLiteAccessTokenAuthenticationToken(String accessTokenId) {
        super(null);
        this.principal = null;
        this.credentials = accessTokenId;
        super.setAuthenticated(false);
    }

    /**
     * Creates a new authentication token using given principal, credentials, and authorities.
     *
     * @param principal   the user principal.
     * @param credentials the user credentials.
     * @param authorities the user authorities.
     */
    public SsoLiteAccessTokenAuthenticationToken(Object principal, @Nullable Object credentials,
            @Nullable Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }
}
