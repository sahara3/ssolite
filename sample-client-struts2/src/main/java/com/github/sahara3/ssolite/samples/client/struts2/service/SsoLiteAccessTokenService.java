package com.github.sahara3.ssolite.samples.client.struts2.service;

import javax.validation.constraints.NotNull;

import com.github.sahara3.ssolite.core.client.SsoLiteAccessTokenApiClient;
import com.github.sahara3.ssolite.core.client.SsoLiteAccessTokenApiClientOkHttp3Impl;
import com.github.sahara3.ssolite.core.client.SsoLiteAccessTokenApiException;
import com.github.sahara3.ssolite.core.model.SsoLiteAccessToken;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SsoLiteAccessTokenService {

    private static final SsoLiteAccessTokenService instance = new SsoLiteAccessTokenService();

    public static SsoLiteAccessTokenService getInstance() {
        return instance;
    }

    private final SsoLiteAccessTokenApiClient ssoLiteAccessTokenApiClient;

    private SsoLiteAccessTokenService() {
        this.ssoLiteAccessTokenApiClient = new SsoLiteAccessTokenApiClientOkHttp3Impl();
    }

    public @NotNull SsoLiteAccessToken retriveAccessToken(@NotNull String url) throws AuthException {
        try {
            return this.ssoLiteAccessTokenApiClient.retriveAccessToken(url);
        }
        catch (SsoLiteAccessTokenApiException e) {
            LOG.debug("Access token is not found, or HTTP error.", e);
            throw new BadPasswordException("Bad credentials.");
        }
    }
}
