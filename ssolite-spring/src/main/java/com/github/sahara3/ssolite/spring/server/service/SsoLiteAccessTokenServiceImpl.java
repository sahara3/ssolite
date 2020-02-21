package com.github.sahara3.ssolite.spring.server.service;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import com.github.sahara3.ssolite.core.model.SsoLiteAccessToken;
import com.github.sahara3.ssolite.spring.server.repository.SsoLiteAccessTokenRepository;

/**
 * Implementation of {@link SsoLiteAccessTokenService} interface.
 *
 * @author sahara3
 */
public class SsoLiteAccessTokenServiceImpl implements SsoLiteAccessTokenService {

    private final SsoLiteAccessTokenRepository accessTokenRepository;

    public SsoLiteAccessTokenServiceImpl(SsoLiteAccessTokenRepository accessTokenRepository) {
        Assert.notNull(accessTokenRepository, "accessTokenRepository cannot be null");
        this.accessTokenRepository = accessTokenRepository;
    }

    @Override
    public @Nullable SsoLiteAccessToken findValidAccessToken(String tokenId) {
        Assert.notNull(tokenId, "tokenId cannot be null");

        SsoLiteAccessToken token = this.accessTokenRepository.findById(tokenId);
        if (token != null) {
            this.accessTokenRepository.delete(token.getId());
            if (token.getExpired().isAfter(OffsetDateTime.now())) {
                token.setExpired(null); // hide expired.
                return token;
            }
            // fall through.
        }
        return null;
    }

    @Override
    public SsoLiteAccessToken createAccessToken(String username) {
        Assert.notNull(username, "username cannot be null");

        String id = UUID.randomUUID().toString();
        OffsetDateTime expired = OffsetDateTime.now().plusSeconds(30);
        SsoLiteAccessToken token = new SsoLiteAccessToken(id, username, expired);
        this.accessTokenRepository.save(token);
        return token;
    }
}
