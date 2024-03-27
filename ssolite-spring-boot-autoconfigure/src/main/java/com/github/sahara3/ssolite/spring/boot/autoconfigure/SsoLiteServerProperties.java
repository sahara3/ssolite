package com.github.sahara3.ssolite.spring.boot.autoconfigure;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Server properties of SSOLite.
 *
 * @author sahara3
 */
@ConfigurationProperties(prefix = "ssolite.server")
public class SsoLiteServerProperties {

    private String defaultTopPageUrl = "internal:/";

    /**
     * Default top page URL after login.
     *
     * <p>
     * If a URL starts with &quot;internal:&quot;, a path is relative from the context root.
     * </p>
     *
     * @return the default top page URL.
     */
    public String getDefaultTopPageUrl() {
        return this.defaultTopPageUrl;
    }

    /**
     * @param defaultTopPageUrl the default top page URL to set.
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
     * @return the list of the permitted domain URLs.
     */
    public List<String> getPermittedDomains() {
        return this.permittedDomains;
    }

    /**
     * @param permittedDomains the list of the permitted domain URLs.
     */
    public void setPermittedDomains(List<String> permittedDomains) {
        this.permittedDomains = permittedDomains;
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
