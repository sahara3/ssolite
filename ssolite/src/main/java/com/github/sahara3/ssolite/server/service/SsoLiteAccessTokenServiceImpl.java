package com.github.sahara3.ssolite.server.service;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import com.github.sahara3.ssolite.model.SsoLiteAccessToken;
import com.github.sahara3.ssolite.server.repository.SsoLiteAccessTokenRepository;

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
    @Nullable
    public SsoLiteAccessToken findValidAccessToken(String tokenId) {
        Assert.notNull(tokenId, "tokenId cannot be null");

        SsoLiteAccessToken token = this.accessTokenRepository.findById(tokenId);
        if (token != null) {
            Date now = new Date();
            this.accessTokenRepository.delete(token.getId());
            if (token.getExpired().after(now)) {
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
        Date expired = Date.from(OffsetDateTime.now().plusSeconds(30).toInstant());
        SsoLiteAccessToken token = new SsoLiteAccessToken(id, username, expired);
        this.accessTokenRepository.save(token);
        return token;
    }
}
