package com.github.sahara3.ssolite.server.service;

import java.time.OffsetDateTime;
import java.util.Date;
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
	private final SsoLiteAccessTokenRepository accessTokenRepository;

	@Override
	public SsoLiteAccessToken findValidAccessToken(@NonNull String tokenId) {
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
	public SsoLiteAccessToken createAccessToken(@NonNull String username) {
		String id = UUID.randomUUID().toString();
		Date expired = Date.from(OffsetDateTime.now().plusSeconds(30).toInstant());
		SsoLiteAccessToken token = new SsoLiteAccessToken(id, username, expired);
		this.accessTokenRepository.save(token);
		return token;
	}
}
