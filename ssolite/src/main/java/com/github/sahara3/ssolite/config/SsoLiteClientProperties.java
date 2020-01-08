package com.github.sahara3.ssolite.config;

import java.util.Objects;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Client properties of SSOLite.
 *
 * @author sahara3
 */
@ConfigurationProperties(prefix = "ssolite.client")
public class SsoLiteClientProperties {

    private String loginUrl = "/login";

    /**
     * Server URL of SSO login page.
     *
     * @return the login page URL
     */
    public String getLoginUrl() {
        return this.loginUrl;
    }

    /**
     * @param loginUrl the login page URL to set
     */
    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    private String tokenApiUrl = "/api/tokens";

    /**
     * Server URL of SSO token RESTful API.
     *
     * @return the token API URL
     */
    public String getTokenApiUrl() {
        return this.tokenApiUrl;
    }

    /**
     * @param tokenApiUrl the token API URL to set
     */
    public void setTokenApiUrl(String tokenApiUrl) {
        this.tokenApiUrl = tokenApiUrl;
    }

    /**
     */
    private boolean sameDomain = false;

    /**
     * True if the server and client are at the same domain.
     *
     * @return the same domain flag
     */
    public boolean isSameDomain() {
        return this.sameDomain;
    }

    /**
     * @param sameDomain the same domain flag to set
     */
    public void setSameDomain(boolean sameDomain) {
        this.sameDomain = sameDomain;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.loginUrl, this.tokenApiUrl, Boolean.valueOf(this.sameDomain));
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
        SsoLiteClientProperties other = (SsoLiteClientProperties) obj;
        return Objects.equals(this.loginUrl, other.loginUrl) && Objects.equals(this.tokenApiUrl, other.tokenApiUrl)
                && this.sameDomain == other.sameDomain;
    }

    @Override
    public String toString() {
        return "SsoLiteClientProperties(loginUrl=" + this.loginUrl + ", sameDomain=" + this.sameDomain
                + ", tokenApiUrl=" + this.tokenApiUrl + ")";
    }
}
