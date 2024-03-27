package com.github.sahara3.ssolite.core.client;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sahara3.ssolite.core.model.SsoLiteAccessToken;
import com.github.sahara3.ssolite.core.util.Assert;

/**
 * {@link SsoLiteAccessTokenApiClient} implementation using OkHttp3.
 *
 * @author sahara3
 */
public class SsoLiteAccessTokenApiClientOkHttp3Impl implements SsoLiteAccessTokenApiClient {

    private static final Logger LOG = LoggerFactory.getLogger(SsoLiteAccessTokenApiClientOkHttp3Impl.class);

    private final OkHttpClient httpClient;

    private final ObjectMapper objectMapper;

    /**
     * @param httpClient   OkHttp3 HTTP client object.
     * @param objectMapper Jackson object mapper for converting JSON.
     */
    public SsoLiteAccessTokenApiClientOkHttp3Impl(OkHttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Constructor using default {@link OkHttpClient} and {@link ObjectMapper}.
     */
    public SsoLiteAccessTokenApiClientOkHttp3Impl() {
        this(new OkHttpClient.Builder().build(), new ObjectMapper());
    }

    @Override
    public SsoLiteAccessToken retriveAccessToken(String url)
            throws IllegalArgumentException, SsoLiteAccessTokenApiException {
        Assert.notNull(url, "url cannot be null");
        LOG.debug("GET {}", url);

        Request request = new Request.Builder().get().url(url).build();
        try (Response response = this.httpClient.newCall(request).execute()) {
            LOG.debug("Token API response: {}", response);
            if (!response.isSuccessful()) {
                LOG.debug("Access token is not found, or HTTP error.");
                throw new SsoLiteAccessTokenApiException("error in retrieve access token.");
            }

            ResponseBody body = response.body();
            if (body == null) {
                LOG.warn("No response body.");
                throw new SsoLiteAccessTokenApiException("error in retrieve access token.");
            }
            String json = body.string();
            return this.objectMapper.readValue(json, SsoLiteAccessToken.class);
        }
        catch (IOException e) {
            LOG.warn("HTTP error.", e);
            throw new SsoLiteAccessTokenApiException("HTTP error.", e);
        }
    }

}
