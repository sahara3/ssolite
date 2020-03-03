
package com.github.sahara3.ssolite.spring.boot.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.util.Assert;

import com.github.sahara3.ssolite.spring.server.SsoLiteServerRedirectResolver;
import com.github.sahara3.ssolite.spring.server.repository.SsoLiteAccessTokenRepository;
import com.github.sahara3.ssolite.spring.server.repository.SsoLiteAccessTokenRepositoryImpl;
import com.github.sahara3.ssolite.spring.server.service.SsoLiteAccessTokenService;
import com.github.sahara3.ssolite.spring.server.service.SsoLiteAccessTokenServiceImpl;

/**
 * Server auto configuration for SSOLite.
 *
 * @author sahara3
 */
@Configuration
@ConditionalOnClass(EnableWebSecurity.class)
@ConditionalOnProperty(prefix = "ssolite.server", name = "enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(SsoLiteServerProperties.class)
@SuppressWarnings("static-method")
public class SsoLiteServerAutoConfiguration {

    /**
     * {@link SsoLiteAccessTokenRepository} bean.
     *
     * @return the {@code SsoLiteAccessTokenRepository} bean.
     */
    @Bean
    @ConditionalOnMissingBean(SsoLiteAccessTokenRepository.class)
    public SsoLiteAccessTokenRepository ssoAccessTokenRepository() {
        return new SsoLiteAccessTokenRepositoryImpl();
    }

    /**
     * {@link SsoLiteAccessTokenService} bean.
     *
     * @param ssoLiteAccessTokenRepository must not be null.
     * @return the {@code SsoLiteAccessTokenService} bean.
     */
    @Bean
    @ConditionalOnMissingBean(SsoLiteAccessTokenService.class)
    public SsoLiteAccessTokenService ssoAccessTokenService(SsoLiteAccessTokenRepository ssoLiteAccessTokenRepository) {
        Assert.notNull(ssoLiteAccessTokenRepository, "ssoLiteAccessTokenRepository cannot be null");
        return new SsoLiteAccessTokenServiceImpl(ssoLiteAccessTokenRepository);
    }

    /**
     * {@link SsoLiteServerRedirectResolver} bean.
     *
     * @param ssoLiteAccessTokenService must not be null.
     * @param ssoLiteServerProperties   must not be null.
     * @return the {@code SsoLiteServerRedirectResolver} bean.
     */
    @Bean
    @ConditionalOnMissingBean(SsoLiteServerRedirectResolver.class)
    public SsoLiteServerRedirectResolver ssoLiteServerRedirectResolver(
            SsoLiteAccessTokenService ssoLiteAccessTokenService, SsoLiteServerProperties ssoLiteServerProperties) {
        Assert.notNull(ssoLiteAccessTokenService, "ssoLiteAccessTokenService cannot be null");
        Assert.notNull(ssoLiteServerProperties, "ssoLiteServerProperties cannot be null");
        SsoLiteServerRedirectResolver resolver = new SsoLiteServerRedirectResolver(ssoLiteAccessTokenService);
        resolver.setPermittedDomains(ssoLiteServerProperties.getPermittedDomains());
        return resolver;
    }
}
