package com.github.sahara3.ssolite.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Server properties of SSOLite.
 *
 * @author sahara3
 */
@ConfigurationProperties(prefix = "ssolite.server")
@Data
public class SsoLiteServerProperties {

	private String defaultTopPageUrl = "internal:/";

	private List<String> permittedDomains = new ArrayList<>();
}
