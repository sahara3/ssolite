package com.github.sahara3.ssolite.repository;

import java.time.OffsetDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.github.sahara3.ssolite.model.SsoAccessToken;

public class SsoAccessTokenRepositoryImpl implements SsoAccessTokenRepository {

	private ConcurrentMap<String, SsoAccessToken> tokens;

	public SsoAccessTokenRepositoryImpl() {
		this.tokens = new ConcurrentHashMap<>();
	}

	@Override
	public SsoAccessToken findOne(String id) {
		SsoAccessToken token = this.tokens.get(id);
		return token;
	}

	@Override
	public void create(SsoAccessToken token) {
		SsoAccessToken current = this.tokens.putIfAbsent(token.getId(), token);
		if (current != null) {
			// FIXME: duplicate!
		}

		this.cleanupExpiredTokens();
	}

	@Override
	public void delete(String id) {
		this.tokens.remove(id);

		this.cleanupExpiredTokens();
	}

	protected void cleanupExpiredTokens() {
		OffsetDateTime now = OffsetDateTime.now();
		this.tokens.entrySet().removeIf(entry -> entry.getValue().getExpired().isBefore(now));
	}
}
