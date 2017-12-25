package com.github.sahara3.ssolite.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@ConfigurationProperties(prefix = "sso-auth.server")
@Data
@NoArgsConstructor
public class SsoLoginServerProperties {

	private String defaultTopPageUrl = "internal:/";

	private List<String> permittedDomains = new ArrayList<>();

}
