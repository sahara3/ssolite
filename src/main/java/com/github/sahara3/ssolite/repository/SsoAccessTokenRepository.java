package com.github.sahara3.ssolite.repository;

import com.github.sahara3.ssolite.model.SsoAccessToken;

public interface SsoAccessTokenRepository {

	SsoAccessToken findOne(String id);

	void create(SsoAccessToken token);

	void delete(String id);

}
