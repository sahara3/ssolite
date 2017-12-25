package com.github.sahara3.ssolite.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
@ConditionalOnClass(EnableWebSecurity.class)
@EnableConfigurationProperties({ SsoLoginServerProperties.class, SsoLoginClientProperties.class,
		SsoLoginCookieProperties.class })
public class AutoConfiguration {

	@Autowired
	private SsoLoginCookieProperties ssoLoginCookieProperties;

	@Bean
	public CookieSerializer cookieSerializer() {
		DefaultCookieSerializer serializer = new DefaultCookieSerializer();

		String name = this.ssoLoginCookieProperties.getSessionCookieName();
		serializer.setCookieName(name);

		String path = this.ssoLoginCookieProperties.getSessionCookiePath();
		if (path != null) {
			serializer.setCookiePath(path);
		}

		return serializer;
	}

}
