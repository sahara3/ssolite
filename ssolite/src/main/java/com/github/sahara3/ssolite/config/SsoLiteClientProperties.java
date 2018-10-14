package com.github.sahara3.ssolite.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Client properties of SSOLite.
 *
 * @author sahara3
 */
@ConfigurationProperties(prefix = "ssolite.client")
@Data
public class SsoLiteClientProperties {

	/**
	 * Server URL of SSO login page.
	 */
	private String loginUrl = "/login";

	/**
	 * Server URL of SSO token RESTful API.
	 */
	private String tokenApiUrl = "/api/tokens";

	/**
	 * True if the server and client are at the same domain.
	 */
	private boolean sameDomain = false;
}
