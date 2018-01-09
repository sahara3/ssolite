package com.github.sahara3.ssolite.model;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SSOLite access token entity.
 *
 * @author sahara3
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SsoLiteAccessToken {

	private String id;

	private String username;

	private OffsetDateTime expired;
}
