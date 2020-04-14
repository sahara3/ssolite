package com.github.sahara3.ssolite.spring.client;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.github.sahara3.ssolite.core.client.SsoLiteAccessTokenApiClient;
import com.github.sahara3.ssolite.core.client.SsoLiteAccessTokenApiException;
import com.github.sahara3.ssolite.core.model.SsoLiteAccessToken;

/**
 * {@link SsoLiteAccessTokenApiClient} implementation using Spring {@link RestTemplate}.
 *
 * @author sahara3
 */
public class SsoLiteAccessTokenApiClientRestTemplateImpl implements SsoLiteAccessTokenApiClient {

    private static final Logger LOG = LoggerFactory.getLogger(SsoLiteAccessTokenApiClientRestTemplateImpl.class);

    private final RestTemplate restTemplate;

    /**
     * @param restTemplate Spring {@code RestTemplate} object.
     */
    public SsoLiteAccessTokenApiClientRestTemplateImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public SsoLiteAccessToken retriveAccessToken(String url)
            throws IllegalArgumentException, SsoLiteAccessTokenApiException {
        Assert.notNull(url, "url cannot be null");
        LOG.debug("GET {}", url);

        try {
            ResponseEntity<SsoLiteAccessToken> response = this.restTemplate.getForEntity(url, SsoLiteAccessToken.class);
            LOG.debug("Token API response: {}", response);

            SsoLiteAccessToken token = null;
            if (!response.getStatusCode().is2xxSuccessful()) {
                LOG.debug("Access token is not found, or HTTP error.");
            }
            else {
                token = response.getBody();
                if (token == null) {
                    LOG.debug("Response body is null.");
                }
            }

            if (token == null) {
                throw new SsoLiteAccessTokenApiException("error in retrive access token.");
            }
            return token;
        }
        catch (RestClientException e) {
            LOG.warn("HTTP error.", e);
            throw new SsoLiteAccessTokenApiException("HTTP error.", e);
        }
    }
}
