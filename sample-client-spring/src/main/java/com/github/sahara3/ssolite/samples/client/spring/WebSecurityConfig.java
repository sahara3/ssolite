package com.github.sahara3.ssolite.samples.client.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import com.github.sahara3.ssolite.core.client.SsoLiteAccessTokenApiClient;
import com.github.sahara3.ssolite.spring.boot.autoconfigure.SsoLiteClientProperties;
import com.github.sahara3.ssolite.spring.client.ExternalAuthenticationEntryPoint;
import com.github.sahara3.ssolite.spring.client.SsoLiteAccessTokenAuthenticationProvider;
import com.github.sahara3.ssolite.spring.client.SsoLiteClientLoginConfigurer;

/**
 * Sample web security configuration for SSOLite client.
 *
 * @author sahara3
 */
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserDetailsService userDetailsService,
            SsoLiteAccessTokenApiClient ssoLiteAccessTokenApiClient,
            SsoLiteClientProperties ssoLiteClientProperties) throws Exception {
        // entry point for authentication failure.
        AuthenticationEntryPoint entryPoint = new ExternalAuthenticationEntryPoint(
                ssoLiteClientProperties.getLoginUrl(), ssoLiteClientProperties.isSameDomain());

        // security context repository.
        SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

        // SSOLite authentication provider.
        SsoLiteAccessTokenAuthenticationProvider ssoProvider =
                new SsoLiteAccessTokenAuthenticationProvider(ssoLiteClientProperties.getTokenApiUrl(),
                        ssoLiteAccessTokenApiClient, userDetailsService);

        http.authenticationProvider(ssoProvider)
                .securityContext(context -> context
                        .securityContextRepository(securityContextRepository)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/actuator/**").permitAll() // for testing purpose only.
                        .requestMatchers("/sso-login").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .permitAll()
                )
                .exceptionHandling(customizer -> customizer
                        .authenticationEntryPoint(entryPoint)
                )
                .with(new SsoLiteClientLoginConfigurer<>(), customizer -> customizer
                        .filterProcessesUrl("/sso-login"));

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // TODO: You should implement more secure UserDetailsService.
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        User.UserBuilder builder = User.builder().passwordEncoder(encoder::encode);
        UserDetails admin = builder.username("admin").password("spring").roles("ADMIN").build();
        return new InMemoryUserDetailsManager(admin);
    }
}
