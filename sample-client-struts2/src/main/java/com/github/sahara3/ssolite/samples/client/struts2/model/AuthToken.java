package com.github.sahara3.ssolite.samples.client.struts2.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthToken implements Serializable {

	private static final long serialVersionUID = 1L;

	private String username;

	private String password;

	private boolean authenticated;
}
