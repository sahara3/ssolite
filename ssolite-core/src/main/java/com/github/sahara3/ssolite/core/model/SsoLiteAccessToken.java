package com.github.sahara3.ssolite.core.model;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * SSOLite access token entity.
 *
 * @author sahara3
 */
public class SsoLiteAccessToken {

    /**
     * Empty constructor.
     */
    public SsoLiteAccessToken() {
        // do nothing.
    }

    /**
     * Constructor with full parameters.
     *
     * @param id       the ID of this token.
     * @param username the username of this token.
     * @param expired  the expired datetime of this token.
     */
    public SsoLiteAccessToken(String id, String username, OffsetDateTime expired) {
        this.id = id;
        this.username = username;
        this.expired = expired;
    }

    private String id;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String username;

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private OffsetDateTime expired;

    public OffsetDateTime getExpired() {
        return this.expired;
    }

    public void setExpired(OffsetDateTime expired) {
        this.expired = expired;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.username, this.expired);
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
        SsoLiteAccessToken other = (SsoLiteAccessToken) obj;
        return Objects.equals(this.id, other.id) && Objects.equals(this.username, other.username)
                && Objects.equals(this.expired, other.expired);
    }

    @Override
    public String toString() {
        return "SsoLiteAccessToken(id=" + this.id + ", username=" + this.username + ", expired=" + this.expired + ")";
    }
}
