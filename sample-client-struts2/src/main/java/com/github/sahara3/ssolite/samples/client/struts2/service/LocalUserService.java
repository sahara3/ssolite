package com.github.sahara3.ssolite.samples.client.struts2.service;

import javax.validation.constraints.NotNull;

import com.github.sahara3.ssolite.samples.client.struts2.model.LocalUser;

public class LocalUserService {

    private static final LocalUserService instance = new LocalUserService();

    public static LocalUserService getInstance() {
        return instance;
    }

    private LocalUserService() {
        // empty.
    }

    @SuppressWarnings("static-method")
    public @NotNull LocalUser findByName(String username) throws UserNotFoundException {
        // NOTE: this is a psuedo implementation.
        if ("admin".equals(username)) {
            LocalUser user = new LocalUser("admin", "struts2");
            return user;
        }
        throw new UserNotFoundException(username);
    }
}
