package com.github.sahara3.ssolite.model;

import java.util.Date;
import java.util.Objects;

/**
 * SSOLite access token entity.
 *
 * @author sahara3
 */
public class SsoLiteAccessToken {

	public SsoLiteAccessToken() {
		// do nothing.
	}

	public SsoLiteAccessToken(String id, String username, Date expired) {
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

	private Date expired;

	public Date getExpired() {
		return this.expired;
	}

	public void setExpired(Date expired) {
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
		return Objects.equals(this.id, other.id) && Objects.equals(this.username, other.username) &&
				Objects.equals(this.expired, other.expired);
	}

	@Override
	public String toString() {
		return "SsoLiteAccessToken(id=" + this.id + ", username=" + this.username + ", expired=" + this.expired + ")";
	}
}
