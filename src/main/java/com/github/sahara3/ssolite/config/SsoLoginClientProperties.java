package com.github.sahara3.ssolite.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@ConfigurationProperties(prefix = "sso-auth.client")
@Data
@NoArgsConstructor
public class SsoLoginClientProperties {

	private String loginUrl = "/login";

	private boolean sameDomain = false;

}
