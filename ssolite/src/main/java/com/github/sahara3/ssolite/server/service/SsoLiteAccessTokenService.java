package com.github.sahara3.ssolite.server.service;

import javax.validation.constraints.NotNull;

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
	 *            the access token ID.
	 * @return the access token, or null if there is no valid token.
	 */
	SsoLiteAccessToken findValidAccessToken(@NotNull String tokenId);

	/**
	 * Creates a new access token for the user.
	 *
	 * @param username
	 *            the user name.
	 * @return the newly created access token.
	 */
	@NotNull
	SsoLiteAccessToken createAccessToken(@NotNull String username);

}
