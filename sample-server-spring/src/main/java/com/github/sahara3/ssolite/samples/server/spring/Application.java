package com.github.sahara3.ssolite.samples.server.spring;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

import com.github.sahara3.ssolite.spring.actuator.SecurityFilterChainActuator;

/**
 * Server application sample.
 *
 * @author sahara3
 */
@SpringBootApplication(proxyBeanMethods = false)
// @EnableTransactionManagement
public class Application extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    /**
     * Entry point.
     *
     * @param args program arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * {@link CookieSerializer} bean.
     *
     * @return the {@code CookieSerializer} bean.
     */
    @Bean
    CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("SESSIONID");
        serializer.setUseHttpOnlyCookie(true);
        return serializer;
    }

    // for debug purpose.
    @Bean
    SecurityFilterChainActuator securityFilterChainActuator(List<SecurityFilterChain> securityFilterChains) {
        return new SecurityFilterChainActuator(securityFilterChains);
    }
}
