package com.github.sahara3.ssolite.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import com.github.sahara3.ssolite.server.SsoLiteServerProperties;
import com.github.sahara3.ssolite.server.repository.SsoLiteAccessTokenRepository;
import com.github.sahara3.ssolite.server.repository.SsoLiteAccessTokenRepositoryImpl;
import com.github.sahara3.ssolite.server.service.SsoLiteAccessTokenService;
import com.github.sahara3.ssolite.server.service.SsoLiteAccessTokenServiceImpl;
import com.github.sahara3.ssolite.server.service.SsoLiteServerRedirectResolver;

import lombok.NonNull;

/**
 * Server auto configuration for SSOLite.
 *
 * @author sahara3
 */
@Configuration
@ConditionalOnClass(EnableWebSecurity.class)
@ConditionalOnProperty(prefix = "ssolite.server", name = "enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(SsoLiteServerProperties.class)
public class SsoLiteServerAutoConfiguration {

	/**
	 * {@code SsoAccessTokenRepository} bean.
	 *
	 * @return the {@code SsoAccessTokenRepository} bean.
	 */
	@Bean
	@ConditionalOnMissingBean(SsoLiteAccessTokenRepository.class)
	@SuppressWarnings("static-method")
	public SsoLiteAccessTokenRepository ssoAccessTokenRepository() {
		return new SsoLiteAccessTokenRepositoryImpl();
	}

	/**
	 * {@code SsoAccessTokenService} bean.
	 *
	 * @param ssoLiteAccessTokenRepository
	 *            must not be null.
	 * @return the {@code SsoAccessTokenService} bean.
	 */
	@Bean
	@ConditionalOnMissingBean(SsoLiteAccessTokenService.class)
	@SuppressWarnings("static-method")
	public SsoLiteAccessTokenService ssoAccessTokenService(
			@NonNull SsoLiteAccessTokenRepository ssoLiteAccessTokenRepository) {
		return new SsoLiteAccessTokenServiceImpl(ssoLiteAccessTokenRepository);
	}

	/**
	 * {@code SsoLiteServerRedirectResolver} bean.
	 *
	 * @param ssoLiteAccessTokenService
	 *            must not be null.
	 * @param ssoLiteServerProperties
	 *            must not be null.
	 * @return the {@code SsoLiteServerRedirectResolver} bean.
	 */
	@Bean
	@ConditionalOnMissingBean(SsoLiteServerRedirectResolver.class)
	@SuppressWarnings("static-method")
	public SsoLiteServerRedirectResolver ssoLiteServerRedirectResolver(
			@NonNull SsoLiteAccessTokenService ssoLiteAccessTokenService,
			@NonNull SsoLiteServerProperties ssoLiteServerProperties) {
		SsoLiteServerRedirectResolver resolver = new SsoLiteServerRedirectResolver(ssoLiteAccessTokenService);
		resolver.setPermittedDomainMap(ssoLiteServerProperties.getPermittedDomainMap());
		return resolver;
	}
}
