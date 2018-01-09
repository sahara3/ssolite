package com.github.sahara3.ssolite.server.service;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.github.sahara3.ssolite.model.SsoLiteAccessToken;
import com.github.sahara3.ssolite.server.repository.SsoLiteAccessTokenRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Implementation of {@link SsoLiteAccessTokenService} interface.
 *
 * @author sahara3
 */
@RequiredArgsConstructor
public class SsoLiteAccessTokenServiceImpl implements SsoLiteAccessTokenService {

	@NotNull
	private final SsoLiteAccessTokenRepository tokenRepository;

	@Override
	public SsoLiteAccessToken findValidAccessToken(@NonNull String tokenId) {
		SsoLiteAccessToken token = this.tokenRepository.findById(tokenId);
		if (token != null) {
			OffsetDateTime now = OffsetDateTime.now();
			this.tokenRepository.delete(token.getId());
			if (token.getExpired().isAfter(now)) {
				// FIXME: session id should not be included.
				return token;
			}
			// fall through.
		}
		return null;
	}

	@Override
	public SsoLiteAccessToken createAccessToken(@NonNull String username) {
		String id = UUID.randomUUID().toString();
		OffsetDateTime expired = OffsetDateTime.now().plusSeconds(30);
		SsoLiteAccessToken token = new SsoLiteAccessToken(id, username, expired);
		this.tokenRepository.save(token);
		return token;
	}
}
