package com.github.sahara3.ssolite.samples.server.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.web.bind.annotation.RequestMethod;

import com.github.sahara3.ssolite.spring.boot.autoconfigure.SsoLiteServerProperties;
import com.github.sahara3.ssolite.spring.server.SsoLiteServerAuthenticationFailureHandler;
import com.github.sahara3.ssolite.spring.server.SsoLiteServerAuthenticationSuccessHandler;
import com.github.sahara3.ssolite.spring.server.SsoLiteServerRedirectResolver;

/**
 * Web security configuration for SSOLite server.
 *
 * @author sahara3
 */
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
public class WebSecurityConfig {

    /**
     * Security configuration for RESTful API of SSO access token.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain ssoLiteAccessTokenApiSecurityFilterChain(HttpSecurity http) throws Exception {
        return http.securityMatcher("/api/tokens/**")
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }

    /**
     * Security configuration for the form login.
     *
     * @author sahara3
     */
    @Bean
    @Order(2)
    public SecurityFilterChain formLoginSecurityFilterChain(HttpSecurity http,
            SsoLiteServerProperties ssoServerProperties, SsoLiteServerRedirectResolver redirectResolver)
            throws Exception {

        // success handler.
        SsoLiteServerAuthenticationSuccessHandler successHandler;
        successHandler = new SsoLiteServerAuthenticationSuccessHandler(redirectResolver,
                    ssoServerProperties.getDefaultTopPageUrl());

        // failure handler.
        SsoLiteServerAuthenticationFailureHandler failureHandler;
        failureHandler = new SsoLiteServerAuthenticationFailureHandler(redirectResolver, "/login?error");

        // security context repository.
        SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

        return http
                .securityContext(context -> context
                        .securityContextRepository(securityContextRepository)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/actuator/**").permitAll() // for testing purpose only.
                        .requestMatchers(RegexRequestMatcher.regexMatcher("/login\\?.*")).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", RequestMethod.GET.name()))
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // TODO: You should implement more secure UserDetailsService.
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        User.UserBuilder builder = User.builder().passwordEncoder(encoder::encode);
        UserDetails admin = builder.username("admin").password("admin").roles("ADMIN").build();
        return new InMemoryUserDetailsManager(admin);
    }
}
