package com.github.sahara3.ssolite.spring.boot.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import com.github.sahara3.ssolite.core.client.SsoLiteAccessTokenApiClient;
import com.github.sahara3.ssolite.spring.client.SsoLiteAccessTokenApiClientRestTemplateImpl;

/**
 * Client auto configuration for SSOLite.
 *
 * @author sahara3
 */
@Configuration
@ConditionalOnClass(EnableWebSecurity.class)
@AutoConfigureAfter(RestTemplateAutoConfiguration.class)
@EnableConfigurationProperties(SsoLiteClientProperties.class)
public class SsoLiteClientAutoConfiguration {

    /**
     * {@link SsoLiteAccessTokenApiClient} bean using Spring {@link RestTemplateBuilder}.
     * 
     * @param restTemplateBuilder must not be null.
     * @return the {@code SsoLiteAccessTokenApiClient} bean.
     */
    @Bean
    @ConditionalOnBean(RestTemplateBuilder.class)
    @ConditionalOnMissingBean
    public SsoLiteAccessTokenApiClient ssoLiteAccessTokenApiClient(RestTemplateBuilder restTemplateBuilder) {
        return new SsoLiteAccessTokenApiClientRestTemplateImpl(restTemplateBuilder.build());
    }
}
