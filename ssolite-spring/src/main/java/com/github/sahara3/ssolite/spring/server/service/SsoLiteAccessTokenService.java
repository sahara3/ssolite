package com.github.sahara3.ssolite.spring.server.service;

import org.springframework.lang.Nullable;

import com.github.sahara3.ssolite.core.model.SsoLiteAccessToken;

/**
 * Service interface of SSOLite access tokens.
 *
 * @author sahara3
 */
public interface SsoLiteAccessTokenService {

    /**
     * Retrieves an access token by its ID.
     *
     * The expired property of the token is hidden (filled in null). If there is no valid token, this returns null.
     *
     * @param tokenId the access token ID.
     * @return the access token, or null if there is no valid token.
     */
    @Nullable
    SsoLiteAccessToken findValidAccessToken(String tokenId);

    /**
     * Creates a new access token for the user.
     *
     * @param username the user name.
     * @return the newly created access token.
     */
    SsoLiteAccessToken createAccessToken(String username);

}
