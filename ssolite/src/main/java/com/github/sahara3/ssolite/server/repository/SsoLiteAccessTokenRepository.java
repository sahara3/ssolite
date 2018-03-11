package com.github.sahara3.ssolite.server.repository;

import javax.validation.constraints.NotNull;

import com.github.sahara3.ssolite.model.SsoLiteAccessToken;

/**
 * Repository interface of SSOLite access tokens.
 *
 * @author sahara3
 */
public interface SsoLiteAccessTokenRepository {

	/**
	 * Retrieves an access token by its ID.
	 *
	 * @param id
	 *            must not be null.
	 * @return the access token with the given ID, or null if not found.
	 */
	SsoLiteAccessToken findById(@NotNull String id);

	/**
	 * Saves a given access token.
	 *
	 * @param token
	 *            must not be null.
	 */
	void save(@NotNull SsoLiteAccessToken token);

	/**
	 * Deletes an access token by its ID.
	 *
	 * @param id
	 *            must not be null.
	 */
	void delete(@NotNull String id);

}
