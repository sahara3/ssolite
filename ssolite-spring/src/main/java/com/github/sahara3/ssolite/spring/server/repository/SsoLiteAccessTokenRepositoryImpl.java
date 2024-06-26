package com.github.sahara3.ssolite.spring.server.repository;

import java.time.OffsetDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import com.github.sahara3.ssolite.core.model.SsoLiteAccessToken;

/**
 * In-memory implementation of {@link SsoLiteAccessTokenRepository} interface.
 *
 * @author sahara3
 */
public class SsoLiteAccessTokenRepositoryImpl implements SsoLiteAccessTokenRepository {

    private final ConcurrentMap<String, SsoLiteAccessToken> tokens = new ConcurrentHashMap<>();

    @Override
    public @Nullable SsoLiteAccessToken findById(String id) {
        Assert.notNull(id, "id cannot be null");
        return this.tokens.get(id);
    }

    @Override
    public void save(SsoLiteAccessToken token) {
        Assert.notNull(token, "token cannot be null");
        this.tokens.put(token.getId(), token);

        this.cleanupExpiredTokens();
    }

    @Override
    public void delete(String id) {
        Assert.notNull(id, "id cannot be null");
        this.tokens.remove(id);

        this.cleanupExpiredTokens();
    }

    protected void cleanupExpiredTokens() {
        OffsetDateTime now = OffsetDateTime.now();
        this.tokens.entrySet().removeIf(entry -> entry.getValue().getExpired().isBefore(now));
    }
}
