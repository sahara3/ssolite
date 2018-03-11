package com.github.sahara3.ssolite.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.github.sahara3.ssolite.util.SsoLiteUriUtils;

import lombok.Data;

/**
 * Server properties of SSOLite.
 *
 * @author sahara3
 */
@ConfigurationProperties(prefix = "ssolite.server")
@Data
public class SsoLiteServerProperties {

	private static final Logger LOG = LoggerFactory.getLogger(SsoLiteServerProperties.class);

	private String defaultTopPageUrl = "internal:/";

	private List<String> permittedDomains = new ArrayList<>();

	/**
	 * Returns the map of SSO permitted domains.
	 *
	 * @return the map of SSO permitted domains.
	 */
	public Map<URI, URI> getPermittedDomainMap() {
		Map<URI, URI> map = new HashMap<>();

		this.permittedDomains.forEach(s -> {
			try {
				URI uri = new URI(s);
				map.put(SsoLiteUriUtils.getDomainUri(uri), uri);
			}
			catch (URISyntaxException e) {
				LOG.warn("Invalid URI: " + s, e);
			}
		});
		return map;
	}
}
