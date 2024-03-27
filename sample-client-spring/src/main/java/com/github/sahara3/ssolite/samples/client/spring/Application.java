package com.github.sahara3.ssolite.samples.client.spring;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.SecurityFilterChain;

import com.github.sahara3.ssolite.spring.actuator.SecurityFilterChainActuator;

/**
 * Client application sample.
 *
 * @author sahara3
 */
@SpringBootApplication(proxyBeanMethods = false)
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

    // for debug purpose.
    @Bean
    SecurityFilterChainActuator securityFilterChainActuator(List<SecurityFilterChain> securityFilterChains) {
        return new SecurityFilterChainActuator(securityFilterChains);
    }
}
