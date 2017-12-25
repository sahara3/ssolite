package com.github.sahara3.ssolite.service;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.github.sahara3.ssolite.model.SsoAccessToken;
import com.github.sahara3.ssolite.repository.SsoAccessTokenRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SsoAccessTokenServiceImpl implements SsoAccessTokenService {

	@NotNull
	private final SsoAccessTokenRepository tokenRepository;

	@Override
	public SsoAccessToken findValidAccessToken(String tokenId) {
		SsoAccessToken token = this.tokenRepository.findOne(tokenId);
		if (token != null) {
			OffsetDateTime now = OffsetDateTime.now();
			this.tokenRepository.delete(token.getId());
			if (token.getExpired().isAfter(now)) {
				return token;
			}
			// fall through.
		}
		return null; // FIXME: Raise 404-like exception? or null?
	}

	@Override
	public SsoAccessToken createAccessToken(@NonNull String sessionId) {
		String id = UUID.randomUUID().toString();
		OffsetDateTime expired = OffsetDateTime.now().plusSeconds(30);
		SsoAccessToken token = new SsoAccessToken(id, sessionId, expired);
		this.tokenRepository.create(token);
		return token;
	}
}
