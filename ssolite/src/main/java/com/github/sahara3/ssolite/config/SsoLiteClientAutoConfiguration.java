package com.github.sahara3.ssolite.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import com.github.sahara3.ssolite.client.SsoLiteClientProperties;

/**
 * Client auto configuration for SSOLite.
 *
 * @author sahara3
 */
@Configuration
@ConditionalOnClass(EnableWebSecurity.class)
@EnableConfigurationProperties(SsoLiteClientProperties.class)
public class SsoLiteClientAutoConfiguration {
	// no additional configurations.
}