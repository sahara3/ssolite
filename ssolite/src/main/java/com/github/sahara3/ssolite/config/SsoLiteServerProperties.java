package com.github.sahara3.ssolite.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.github.sahara3.ssolite.util.SsoLiteUriUtils;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * Server properties of SSOLite.
 *
 * @author sahara3
 */
@ConfigurationProperties(prefix = "ssolite.server")
@Data
public class SsoLiteServerProperties {

	private static final Logger LOG = LoggerFactory.getLogger(SsoLiteServerProperties.class);

	/**
	 * Default top page URL after login.
	 *
	 * If a URL starts with &quot;internal:&quot;, a path is relative from the
	 * context root.
	 */
	private String defaultTopPageUrl = "internal:/";

	/**
	 * List of the SSO permitted domain URLs.
	 *
	 * Each URL points the SSO login processing path in the client.
	 */
	private List<String> permittedDomains = new ArrayList<>();

	/**
	 * Map of SSO permitted domains.
	 *
	 * Key is a domain only URL, and value is a URL pointed at the SSO login
	 * processing path in a client.
	 */
	@Setter(AccessLevel.NONE)
	private Map<URI, URI> permittedDomainMap;

	@PostConstruct
	protected void init() {
		this.permittedDomainMap = new HashMap<>();

		this.permittedDomains.forEach(s -> {
			try {
				URI ssoLoginUri = new URI(s);
				URI domainUri = SsoLiteUriUtils.getDomainUri(ssoLoginUri);
				this.permittedDomainMap.put(domainUri, ssoLoginUri);
				LOG.debug("Permitted domain: {}", s);
			}
			catch (URISyntaxException e) {
				LOG.warn("Invalid URI: " + s, e);
			}
		});
	}
}
