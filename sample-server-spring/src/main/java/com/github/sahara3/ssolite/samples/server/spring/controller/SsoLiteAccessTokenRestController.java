package com.github.sahara3.ssolite.samples.server.spring.controller;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.sahara3.ssolite.core.model.SsoLiteAccessToken;
import com.github.sahara3.ssolite.spring.server.service.SsoLiteAccessTokenService;

/**
 * RESTful API server controller sample.
 *
 * @author sahara3
 */
@RestController
@RequestMapping(path = "/api")
@RequiredArgsConstructor
public class SsoLiteAccessTokenRestController {

    @NotNull
    private final SsoLiteAccessTokenService service;

    /**
     * Retrieves an access token by its ID.
     *
     * @param tokenId must not be null.
     * @return the access token.
     * @throws SsoLiteAccessTokenNotFoundException thrown if the token ID is invalid.
     */
    @GetMapping(path = "tokens/{tokenId}")
    public SsoLiteAccessToken findValidAccessToken(@PathVariable @NonNull String tokenId)
            throws SsoLiteAccessTokenNotFoundException {
        SsoLiteAccessToken token = this.service.findValidAccessToken(tokenId);
        if (token == null) {
            throw new SsoLiteAccessTokenNotFoundException("Access token is not found.");
        }
        return token;
    }
}
