package com.github.sahara3.ssolite.core.client;

import com.github.sahara3.ssolite.core.model.SsoLiteAccessToken;

/**
 * SSOLite access token API client for retrieving access tokens.
 *
 * @author sahara3
 */
public interface SsoLiteAccessTokenApiClient {

    /**
     * Calls RESTful API to retrieve the access token that contains the authenticated username.
     *
     * @param url the RESTful API URL for retrieving the access token.
     * @return SSOLite access token contains the authenticated username.
     * @throws IllegalArgumentExcepiton       thrown if the {@code url} is {@code null}.
     * @throws SsoLiteAccessTokenApiException thrown if any HTTP error occurs, or access token is not found.
     */
    SsoLiteAccessToken retriveAccessToken(String url) throws IllegalArgumentException, SsoLiteAccessTokenApiException;
}
