package com.github.sahara3.ssolite.server.repository;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.validation.constraints.NotNull;

import com.github.sahara3.ssolite.model.SsoLiteAccessToken;

/**
 * In-memory implementation of {@link SsoLiteAccessTokenRepository} interface.
 *
 * @author sahara3
 */
public class SsoLiteAccessTokenRepositoryImpl implements SsoLiteAccessTokenRepository {

	private final ConcurrentMap<String, SsoLiteAccessToken> tokens = new ConcurrentHashMap<>();

	@Override
	public SsoLiteAccessToken findById(@NotNull String id) {
		SsoLiteAccessToken token = this.tokens.get(id);
		return token;
	}

	@Override
	public void save(@NotNull SsoLiteAccessToken token) {
		this.tokens.put(token.getId(), token);

		this.cleanupExpiredTokens();
	}

	@Override
	public void delete(@NotNull String id) {
		this.tokens.remove(id);

		this.cleanupExpiredTokens();
	}

	protected void cleanupExpiredTokens() {
		Date now = new Date();
		this.tokens.entrySet().removeIf(entry -> entry.getValue().getExpired().before(now));
	}
}
