package com.github.sahara3.ssolite.samples.client.struts2.service;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import com.github.sahara3.ssolite.samples.client.struts2.model.LocalUser;

public class LocalUserService {

    @Getter
    private static final LocalUserService instance = new LocalUserService();

    private LocalUserService() {
        // empty.
    }

    public @NotNull LocalUser findByName(String username) throws UserNotFoundException {
        // NOTE: this is a psuedo implementation.
        if ("admin".equals(username)) {
            return new LocalUser("admin", "struts2");
        }
        throw new UserNotFoundException(username);
    }
}
