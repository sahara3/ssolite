package com.github.sahara3.ssolite.client;

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

	private String loginUrl = "/login";

	private String tokenApiUrl = "/api/tokens";

	private boolean sameDomain = false;
}
