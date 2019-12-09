package com.github.sahara3.ssolite.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.github.sahara3.ssolite.util.SsoLiteUriUtils;

/**
 * Server properties of SSOLite.
 *
 * @author sahara3
 */
@ConfigurationProperties(prefix = "ssolite.server")
public class SsoLiteServerProperties {

	private static final Logger LOG = LoggerFactory.getLogger(SsoLiteServerProperties.class);

	private String defaultTopPageUrl = "internal:/";

	/**
	 * Default top page URL after login.
	 *
	 * <p>
	 * If a URL starts with &quot;internal:&quot;, a path is relative from the
	 * context root.
	 * </p>
	 *
	 * @return the default top page URL
	 */
	public String getDefaultTopPageUrl() {
		return this.defaultTopPageUrl;
	}

	/**
	 * @param defaultTopPageUrl
	 *            the default top page URL to set
	 */
	public void setDefaultTopPageUrl(String defaultTopPageUrl) {
		this.defaultTopPageUrl = defaultTopPageUrl;
	}

	private List<String> permittedDomains = new ArrayList<>();

	/**
	 * List of the SSO permitted domain URLs.
	 *
	 * <p>
	 * Each URL points the SSO login processing path in the client.
	 * </p>
	 *
	 * @return the list of the permitted domain URLs
	 */
	public List<String> getPermittedDomains() {
		return this.permittedDomains;
	}

	/**
	 * @param permittedDomains
	 *            the permitted domain URLs to set
	 */
	public void setPermittedDomains(List<String> permittedDomains) {
		this.permittedDomains = permittedDomains;
		this.updatePermittedDomainMap();
	}

	private Map<URI, URI> permittedDomainMap;

	/**
	 * Map of SSO permitted domains.
	 *
	 * <p>
	 * Key is a domain only URL, and value is a URL pointed at the SSO login
	 * processing path in a client.
	 * </p>
	 *
	 * @return the permitted domain map
	 */
	public Map<URI, URI> getPermittedDomainMap() {
		return this.permittedDomainMap;
	}

	private void updatePermittedDomainMap() {
		this.permittedDomainMap = new HashMap<>();

		this.permittedDomains.forEach(s -> {
			try {
				URI ssoLoginUri = new URI(s);
				URI domainUri = SsoLiteUriUtils.getDomainUri(ssoLoginUri);
				this.permittedDomainMap.put(domainUri, ssoLoginUri);
				LOG.debug("Permitted domain: {}", s);
			} catch (URISyntaxException e) {
				LOG.warn("Invalid URI: " + s, e);
			}
		});
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.defaultTopPageUrl, this.permittedDomains);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		SsoLiteServerProperties other = (SsoLiteServerProperties) obj;
		return Objects.equals(this.defaultTopPageUrl, other.defaultTopPageUrl)
				&& Objects.equals(this.permittedDomains, other.permittedDomains);
	}

	@Override
	public String toString() {
		return "SsoLiteServerProperties(defaultTopPageUrl=" + this.defaultTopPageUrl + ", permittedDomains="
				+ this.permittedDomains + ")";
	}
}
