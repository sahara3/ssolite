package com.github.sahara3.ssolite.samples.client.service;

import java.util.Arrays;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LocalUserDetailsService implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// NOTE: this is a psuedo implementation.
		if ("admin".equals(username)) {
			User user = new User("admin", "password", Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")));
			return user;
		}
		throw new UsernameNotFoundException(username);
	}
}
