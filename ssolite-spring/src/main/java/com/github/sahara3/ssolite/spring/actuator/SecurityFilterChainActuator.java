package com.github.sahara3.ssolite.spring.actuator;

import java.util.List;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.security.web.SecurityFilterChain;

@Endpoint(id = "securityfilterchains")
public class SecurityFilterChainActuator {

    private final List<SecurityFilterChain> securityFilterChains;

    public SecurityFilterChainActuator(List<SecurityFilterChain> securityFilterChains) {
        this.securityFilterChains = securityFilterChains;
    }

    @ReadOperation
    public List<List<String>> getSecurityFilterChains() {
        return securityFilterChains.stream()
                .map(chain -> chain.getFilters().stream().map(filter -> filter.getClass().getName()).toList())
                .toList();
    }
}
