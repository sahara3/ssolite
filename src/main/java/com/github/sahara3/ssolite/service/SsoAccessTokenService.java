package com.github.sahara3.ssolite.service;

import com.github.sahara3.ssolite.model.SsoAccessToken;

public interface SsoAccessTokenService {

	SsoAccessToken findValidAccessToken(String tokenId);

	SsoAccessToken createAccessToken(String sessionId);

}
