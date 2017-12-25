package com.github.sahara3.ssolite.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@ConfigurationProperties(prefix = "sso-auth.cookie")
@Data
@NoArgsConstructor
public class SsoLoginCookieProperties {

	private String sessionCookieName = "SESSIONID";

	private String sessionCookiePath = null;

}
