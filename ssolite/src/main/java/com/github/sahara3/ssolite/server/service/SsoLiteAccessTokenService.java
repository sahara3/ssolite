package com.github.sahara3.ssolite.server.service;

import com.github.sahara3.ssolite.model.SsoLiteAccessToken;

/**
 * Service interface of SSOLite access tokens.
 *
 * @author sahara3
 */
public interface SsoLiteAccessTokenService {

	/**
	 * Retrieves an access token by its ID.
	 *
	 * If there is no valid token, this returns null.
	 * 
	 * @param tokenId
	 *            must not be null.
	 * @return the access token, or null if there is no valid token.
	 */
	SsoLiteAccessToken findValidAccessToken(String tokenId);

	/**
	 * Creates a new access token for the user.
	 *
	 * @param username
	 *            must not be null.
	 * @return the newly created access token.
	 */
	SsoLiteAccessToken createAccessToken(String username);

}
